package br.com.unopay.api.payment.model;

import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;


@Data
@Entity
@ToString(exclude = { "paymentRemittance", "payee" })
@Table(name = "payment_remittance_item")
public class PaymentRemittanceItem  implements Serializable {

    public static final long serialVersionUID = 1L;

    public static final String SUCCESS_RETURN = "00";

    public PaymentRemittanceItem(){}

    public PaymentRemittanceItem(RemittancePayee payee){
        this.payee = payee;
        setMeUp(payee.getPayerBankCode());
    }

    public PaymentRemittanceItem(BatchClosing closing){
        this.payee = new RemittancePayee(closing.getEstablishment(), closing.paymentAccountBank(), closing.getValue());
        setMeUp(closing.paymentAccountBank());
    }

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="payment_remittance_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class,Views.List.class})
    private PaymentRemittance paymentRemittance;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="payee_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class,Views.List.class})
    private RemittancePayee payee;

    @Column(name = "value")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private BigDecimal value;

    @Column(name = "situation")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private RemittanceSituation situation;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_option")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private PaymentTransferOption transferOption;

    @Column(name = "occurrence_code")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private String occurrenceCode;

    @JsonIgnore
    @Version
    private Integer version;

    public void updateValue(BigDecimal value){
        if(this.value ==null ){
            this.value = value;
            return;
        }
        this.value = this.value.add(value);
    }

    public boolean payeeDocumentIs(String document){
        return Objects.equals(getPayee().getDocumentNumber(), document);
    }

    public void updateOccurrenceFields(String occurrenceCode){
        setOccurrenceCode(occurrenceCode);
        if(Objects.equals(SUCCESS_RETURN, occurrenceCode)){
            setSituation(RemittanceSituation.RETURN_PROCESSED_SUCCESSFULLY);
            return;
        }
        setSituation(RemittanceSituation.RETURN_PROCESSED_WITH_ERROR);
    }

    public boolean processedWithError(){
        return RemittanceSituation.RETURN_PROCESSED_WITH_ERROR.equals(situation);
    }

    public String payerDocumentNumber(){
        return getPaymentRemittance().getPayer().getDocumentNumber();
    }

    private void defineTransferOption(Integer bankCode) {
        if(Objects.equals(bankCode, this.payee.getBankCode())){
            this.transferOption = PaymentTransferOption.CURRENT_ACCOUNT_CREDIT;
            return;
        }
        this.transferOption = PaymentTransferOption.DOC_TED;
    }

    private void setMeUp(Integer payerBankCode) {
        this.situation = RemittanceSituation.PROCESSING;
        defineTransferOption(payerBankCode);
    }
}
