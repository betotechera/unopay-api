package br.com.unopay.api.payment.model;

import br.com.unopay.api.bacen.model.Bank;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.payment.cnab240.Cnab240Generator.DATE_FORMAT;


@Data
@Entity
@ToString(exclude = "issuer")
@EqualsAndHashCode(of = {"id", "number"})
@Table(name = "payment_remittance")
public class PaymentRemittance implements Serializable {

    public static final long serialVersionUID = 1L;

    public PaymentRemittance(){}

    public PaymentRemittance(Issuer issuer, Long total){
        this.issuer = issuer;
        this.issuerBankCode = issuer.getPaymentAccount().getBankAccount().getBacenCode();
        this.situation = RemittanceSituation.PROCESSING;
        this.operationType = PaymentOperationType.CREDIT;
        this.paymentServiceType = PaymentServiceType.SUPPLIER_PAYMENT;
        this.createdDateTime = new Date();
        this.transferOption = PaymentTransferOption.DOC_TED;
        this.number = String.valueOf(total + 1);
    }

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToOne
    @JoinColumn(name="issuer_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Issuer issuer;

    @Column(name = "issuer_bank_code")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private Integer issuerBankCode;

    @Column(name = "remittance_number")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private PaymentServiceType paymentServiceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_option")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private PaymentTransferOption transferOption;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private PaymentOperationType operationType;

    @Column(name = "occurrence_code")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private String occurrenceCode;

    @Column(name = "created_date_time")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private Date createdDateTime;

    @Column(name = "submission_date_time")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private Date submissionDateTime;

    @Column(name = "situation")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private RemittanceSituation situation;

    @Column(name = "submission_return_date_time")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private Date submissionReturnDateTime;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_remittance_id")
    private Set<PaymentRemittanceItem> remittanceItems;

    @JsonIgnore
    @Version
    private Integer version;

    public void defineTransferOption(Bank bank) {
        if(Objects.equals(bank.getBacenCode(), this.getIssuerBankCode())){
            this.transferOption = PaymentTransferOption.CURRENT_ACCOUNT_CREDIT;
            return;
        }
        this.transferOption = PaymentTransferOption.DOC_TED;
    }

    public BigDecimal total() {
        if (this.remittanceItems != null) {
            return this.remittanceItems.stream()
                    .map(PaymentRemittanceItem::getValue)
                    .reduce((last, current) -> current.add(last))
                    .orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    public String getFileUri() {
        String toFormat = "remittance/%s/Pagamento%s%s.REM";
        return String.format(toFormat, documentNumber(), createTimeFormatted(), numberAsString());
    }

    public void defineCurrentAccountTransferOption(){
        setTransferOption(PaymentTransferOption.CURRENT_ACCOUNT_CREDIT);
    }

    private String createTimeFormatted() {
        return DATE_FORMAT.format(createdDateTime);
    }

    private String numberAsString() {
        return StringUtils.leftPad(number,6, "0");
    }

    private String documentNumber(){
        return this.issuer.getPerson().getDocument().getNumber();
    }
}
