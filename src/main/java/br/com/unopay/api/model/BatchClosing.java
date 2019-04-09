package br.com.unopay.api.model;

import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.network.model.Establishment;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.RecurrencePeriod;
import br.com.unopay.api.billing.remittance.model.PaymentRemittanceItem;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.util.Time;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.BATCH_CANCELED;
import static br.com.unopay.api.uaa.exception.Errors.BATCH_FINALIZED;

@Data
@Slf4j
@Entity
@ToString(exclude = "batchClosingItems")
@EqualsAndHashCode(of = {"id", "number"})
@Table(name = "batch_closing")
public class BatchClosing implements Serializable {

    public static final long serialVersionUID = 1L;
    public static final int NUMBER_SIZE = 12;

    public BatchClosing(){}

    public BatchClosing(ServiceAuthorize serviceAuthorize,Long total){
        Date closingPaymentDays = Time.create(serviceAuthorize.establishmentClosingPaymentDays());
        this.accreditedNetwork = serviceAuthorize.getContract().getProduct().getAccreditedNetwork();
        this.establishment = serviceAuthorize.getEstablishment();
        this.hirer = serviceAuthorize.getContract().getHirer();
        this.issuer = serviceAuthorize.getContract().getProduct().getIssuer();
        this.period = serviceAuthorize.getEstablishment().getCheckout().getPeriod();
        this.issueInvoice = serviceAuthorize.getContract().isIssueInvoice();
        this.closingDateTime = new Date();
        this.paymentReleaseDateTime = closingPaymentDays;
        this.situation = BatchClosingSituation.PROCESSING_AUTOMATIC_BATCH;
        this.number = generateBatchNumber(total);
    }

    @SneakyThrows
    private String generateBatchNumber(Long total) {
        String batchNumber = String.valueOf(establishment.getType().ordinal()) + String.valueOf(total) +
                        String.valueOf(this.closingDateTime.getTime());
        return batchNumber.substring(0, Math.min(batchNumber.length(), NUMBER_SIZE));
    }

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.BatchClosing.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToOne
    @NotNull(groups = {Reference.class})
    @JoinColumn(name="establishment_id")
    @JsonView({Views.BatchClosing.List.class})
    private Establishment establishment;

    @Column(name = "batch_number")
    @JsonView({Views.BatchClosing.List.class})
    private String number;

    @ManyToOne
    @JoinColumn(name="issuer_id")
    @JsonView({Views.BatchClosing.Detail.class})
    private Issuer issuer;

    @ManyToOne
    @JoinColumn(name="accredited_network_id")
    @JsonView({Views.BatchClosing.Detail.class})
    private AccreditedNetwork accreditedNetwork;

    @ManyToOne
    @JoinColumn(name="hirer_id")
    @JsonView({Views.BatchClosing.Detail.class})
    private Hirer hirer;

    @JoinColumn(name="issue_invoice")
    @JsonView({Views.BatchClosing.List.class})
    private Boolean issueInvoice;

    @Column(name = "closing_date_time")
    @NotNull(groups = {Create.class})
    @JsonView({Views.BatchClosing.List.class})
    private Date closingDateTime;

    @Column(name = "value")
    @JsonView({Views.BatchClosing.Detail.class})
    private BigDecimal value;

    @Column(name = "period")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.BatchClosing.Detail.class})
    private RecurrencePeriod period;

    @Column(name = "payment_release_date_time")
    @JsonView({Views.BatchClosing.Detail.class})
    private Date paymentReleaseDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "situation")
    @NotNull(groups = {Update.class})
    @JsonView({Views.BatchClosing.List.class})
    private BatchClosingSituation situation;

    @Column(name = "payment_date_time")
    @JsonView({Views.BatchClosing.Detail.class})
    private Date paymentDateTime;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_closing_id")
    @JsonView({Views.BatchClosing.Detail.class})
    private List<BatchClosingItem> batchClosingItems;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="payment_id")
    @JsonView({Views.BatchClosing.List.class})
    private PaymentRemittanceItem payment;

    @JsonIgnore
    @Version
    private Integer version;

    public void updateValue(BigDecimal value){
        if(this.value == null ){
            this.value = value;
            return;
        }
        this.value = this.value.add(value);
    }

    public void addItem(BatchClosingItem batchClosingItem){
        if(this.batchClosingItems == null){
            this.batchClosingItems = new ArrayList<>();
        }
        if(!batchClosingItems.contains(batchClosingItem)) {
            this.batchClosingItems.add(batchClosingItem);
        }
    }

    public BatchClosing defineSituation() {
        if(issueInvoice){
            situation = BatchClosingSituation.DOCUMENT_RECEIVED;
            return this;
        }
        situation = BatchClosingSituation.FINALIZED;
        return this;
    }

    public String establishmentBatchMail(){
        if(getEstablishment()!= null) {
            return getEstablishment().getBachShipmentMail();
        }
        return null;
    }

    public boolean myEstablishmentIs(Establishment establishment) {
        return this.establishment != null && establishment != null && Objects.equals(establishment.getId(),
                this.establishment.getId());
    }

    @JsonIgnore
    public boolean isFinalized() {
        return BatchClosingSituation.FINALIZED.equals(situation);
    }

    @JsonIgnore
    public boolean isCanceled() {
        return BatchClosingSituation.CANCELED.equals(situation);
    }

    public BatchClosing cancel(){
        checkCanBeChanged();
        setSituation(BatchClosingSituation.CANCELED);
        return this;
    }

    public void checkCanBeChanged() {
        if(isFinalized()){
            throw UnovationExceptions.unprocessableEntity().withErrors(BATCH_FINALIZED);
        }
        if(isCanceled()){
            throw UnovationExceptions.unprocessableEntity().withErrors(BATCH_CANCELED);
        }
    }

    public Integer paymentAccountBank(){
        return getIssuer().getPaymentAccount().getBankAccount().bacenCode();
    }

    public String establishmentId() {
        return establishment != null ? establishment.getId() : null;
    }

    public String establishmentDocument() {
        return establishment != null ? establishment.documentNumber() : null;
    }

    public boolean establishmentBankCodeIs(Integer bacenCode){
        return Objects.equals(this.getEstablishment().getBankAccount().bacenCode(), bacenCode);
    }

    public Integer establishmentBankCode(){
        return this.getEstablishment().getBankAccount().bacenCode();
    }

    public void setPaymentDateTime(Date dateTime){
        this.paymentDateTime = ObjectUtils.clone(dateTime);
    }

    public void setPaymentReleaseDateTime(Date dateTime){
        this.paymentReleaseDateTime = ObjectUtils.clone(dateTime);
    }

    public void setClosingDateTime(Date dateTime){
        this.closingDateTime = ObjectUtils.clone(dateTime);
    }

    public Date getPaymentDateTime(){
        return ObjectUtils.clone(this.paymentDateTime);
    }

    public Date getPaymentReleaseDateTime(){
        return ObjectUtils.clone(this.paymentReleaseDateTime);
    }

    public Date getClosingDateTime(){
        return ObjectUtils.clone(this.closingDateTime);
    }

    public boolean myIssuerIs(Issuer issuer) {
        return this.issuer != null && issuer != null && Objects.equals(issuer.getId(), this.issuer.getId());
    }
}
