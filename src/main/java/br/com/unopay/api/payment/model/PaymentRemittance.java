package br.com.unopay.api.payment.model;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import lombok.val;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.payment.cnab240.Cnab240Generator.DATE_FORMAT;


@Data
@Entity
@ToString(exclude = "payer")
@EqualsAndHashCode(of = {"id", "number", "createdDateTime"})
@Table(name = "payment_remittance")
public class PaymentRemittance implements Serializable {

    public static final long serialVersionUID = 1L;
    public static final int NUMBER_SIZE = 6;

    public PaymentRemittance(){}

    public PaymentRemittance(RemittancePayer payer, Long total){
        this.payer = payer;
        setupMeUp(total);
    }

    public PaymentRemittance(Issuer issuer, Long total){
        this.payer = new RemittancePayer(issuer);
        setupMeUp(total);
    }

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.PaymentRemittance.Detail.class,Views.PaymentRemittance.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="payer_id")
    @JsonView({Views.PaymentRemittance.Detail.class})
    private RemittancePayer payer;

    @Column(name = "remittance_number")
    @JsonView({Views.PaymentRemittance.Detail.class,Views.PaymentRemittance.List.class})
    @NotNull(groups = {Create.class})
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    @JsonView({Views.PaymentRemittance.Detail.class})
    @NotNull(groups = {Create.class})
    private PaymentServiceType paymentServiceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type")
    @JsonView({Views.PaymentRemittance.Detail.class})
    @NotNull(groups = {Create.class})
    private PaymentOperationType operationType;

    @Column(name = "occurrence_code")
    @JsonView({Views.PaymentRemittance.Detail.class})
    @NotNull(groups = {Create.class})
    private String occurrenceCode;

    @Column(name = "created_date_time")
    @JsonView({Views.PaymentRemittance.Detail.class,Views.PaymentRemittance.List.class})
    @NotNull(groups = {Create.class})
    private Date createdDateTime;

    @Column(name = "submission_date_time")
    @JsonView({Views.PaymentRemittance.Detail.class})
    @NotNull(groups = {Create.class})
    private Date submissionDateTime;

    @Column(name = "cnab_uri")
    @JsonView({Views.PaymentRemittance.Detail.class,Views.PaymentRemittance.List.class})
    private String cnabUri;

    @Column(name = "situation")
    @JsonView({Views.PaymentRemittance.Detail.class,Views.PaymentRemittance.List.class})
    @NotNull(groups = {Create.class})
    private RemittanceSituation situation;

    @Column(name = "submission_return_date_time")
    @JsonView({Views.PaymentRemittance.Detail.class})
    @NotNull(groups = {Create.class})
    private Date submissionReturnDateTime;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonView({Views.PaymentRemittance.Detail.class})
    @JoinColumn(name = "payment_remittance_id")
    private Set<PaymentRemittanceItem> remittanceItems;

    @JsonIgnore
    @Version
    private Integer version;

    public BigDecimal getTotal() {
        if (this.remittanceItems != null) {
            return this.remittanceItems.stream()
                    .map(PaymentRemittanceItem::getValue)
                    .reduce((last, current) -> current.add(last))
                    .orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    public String getBankAgreementNumberByOperation(){
        if(forDebit()){
            return getPayer().getBankAgreementNumberForDebit();
        }
        return getPayer().getBankAgreementNumberForCredit();
    }

    public String getFileUri() {
        String prefix = PaymentOperationType.CREDIT.equals(operationType) ? "PG" : "DB";
        val toFormat = "remittance/%s/%s%s%s.REM";
        return String.format(toFormat, documentNumber(),prefix, createTimeFormatted(), numberAsString());
    }

    public String getFileName(){
        return getFileUri().split("/")[2];
    }

    public boolean payerDocumentNumberIs(String document){
        return document != null && Objects.equals(getPayer().getDocumentNumber(), document);
    }

    public boolean payerBankAgreementNumberIs(String number){
        return number != null && Objects.equals(getPayer().getBankAgreementNumberForCredit(), number);
    }

    public void setSubmissionDateTime(Date dateTime){
        this.submissionDateTime = ObjectUtils.clone(dateTime);
    }

    public void setSubmissionReturnDateTime(Date dateTime){
        this.submissionReturnDateTime = ObjectUtils.clone(dateTime);
    }

    public void setCreatedDateTime(Date dateTime){
        this.createdDateTime = ObjectUtils.clone(dateTime);
    }

    public Date getCreatedDateTime(){
        return ObjectUtils.clone(this.createdDateTime);
    }

    public Date getSubmissionDateTime(){
        return ObjectUtils.clone(this.submissionDateTime);
    }

    public Date getSubmissionReturnDateTime(){
        return ObjectUtils.clone(this.submissionReturnDateTime);
    }

    private String createTimeFormatted() {
        return new SimpleDateFormat(DATE_FORMAT).format(createdDateTime);
    }

    private String numberAsString() {
        return StringUtils.leftPad(number, NUMBER_SIZE, "0");
    }

    private String documentNumber(){
        return this.payer.getDocumentNumber();
    }

    private void setupMeUp(Long total) {
        this.situation = RemittanceSituation.PROCESSING;
        this.operationType = PaymentOperationType.CREDIT;
        this.paymentServiceType = PaymentServiceType.SUPPLIER_PAYMENT;
        this.createdDateTime = new Date();
        this.number = String.valueOf(total + 1);
    }

    public boolean forDebit() {
        return PaymentOperationType.DEBIT.equals(operationType);
    }
}
