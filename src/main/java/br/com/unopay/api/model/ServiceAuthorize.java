package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.EstablishmentEvent;
import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.credit.model.ContractorInstrumentCredit;
import br.com.unopay.api.credit.model.CreditInsertionType;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Views;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.EVENT_QUANTITY_GREATER_THAN_ZERO_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE;
import static br.com.unopay.api.uaa.exception.Errors.EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.SERVICE_NOT_ACCEPTABLE;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Data
@Entity
@EqualsAndHashCode(exclude = {"contract"})
@Table(name = "service_authorize")
public class ServiceAuthorize implements Serializable {

    public static final long serialVersionUID = 1L;


    public ServiceAuthorize(){}

    @Id
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @Column(name = "authorization_number")
    private String authorizationNumber;

    @Column(name = "authorization_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView({Views.ServiceAuthorize.List.class})
    private Date authorizationDateTime;

    @ManyToOne
    @NotNull(groups = {Reference.class})
    @JoinColumn(name="establishment_id")
    @JsonView({Views.ServiceAuthorize.List.class})
    private Establishment establishment;

    @ManyToOne
    @NotNull(groups = {Reference.class})
    @JoinColumn(name="contract_id")
    @JsonView({Views.ServiceAuthorize.List.class})
    private Contract contract;

    @ManyToOne
    @NotNull(groups = {Reference.class})
    @JoinColumn(name="contractor_id")
    @JsonView({Views.ServiceAuthorize.List.class})
    private Contractor contractor;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    @JsonView({Views.ServiceAuthorize.Detail.class, Views.ServiceAuthorize.List.class})
    private ServiceType serviceType;

    @ManyToOne
    @JoinColumn(name="event_id")
    @JsonView({Views.ServiceAuthorize.Detail.class, Views.ServiceAuthorize.List.class})
    private Event event;

    @Transient
    @NotNull(groups = {Reference.class})
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private EstablishmentEvent establishmentEvent;

    @Column(name = "event_quantity")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private Double eventQuantity;

    @Column(name = "event_value")
    @JsonView({Views.ServiceAuthorize.List.class})
    private BigDecimal eventValue;

    @Column(name = "value_fee")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private BigDecimal valueFee;

    @Column(name = "solicitation_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private Date solicitationDateTime;

    @Column(name = "credit_insertion_type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private CreditInsertionType creditInsertionType;

    @ManyToOne
    @NotNull(groups = {Reference.class})
    @JoinColumn(name="contractor_inst_credit_id")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private ContractorInstrumentCredit contractorInstrumentCredit;

    @Column(name = "last_inst_credit_balance")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private BigDecimal lastInstrumentCreditBalance;

    @Column(name = "current_inst_credit_balance")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private BigDecimal currentInstrumentCreditBalance;

    @Column(name = "cancellation_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private Date cancellationDateTime;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private UserDetail user;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.ServiceAuthorize.List.class})
    private TransactionSituation situation;

    @Column(name = "batch_closing_date_time")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private Date batchClosingDateTime;

    @JsonIgnore
    @Column(name = "typed_password")
    private String typedPassword;

    @Version
    @JsonIgnore
    private Integer version;

    @Transient
    @JsonIgnore
    private String operation = "1";

    public String contractId(){
        if(getContract() != null){
            return getContract().getId();
        }
        return null;
    }

    public String instrumentPassword(){
        if(getContractorInstrumentCredit() != null && getContractorInstrumentCredit().getPaymentInstrument() != null){
            return getContractorInstrumentCredit().getPaymentInstrument().getPassword();
        }
        return null;
    }

    public void instrumentPassword(String password){
        if(getContractorInstrumentCredit() == null){
            setContractorInstrumentCredit(new ContractorInstrumentCredit());
        }
        if(getContractorInstrumentCredit().getPaymentInstrument() == null){
            getContractorInstrumentCredit().setPaymentInstrument(new PaymentInstrument());
        }
        getContractorInstrumentCredit().getPaymentInstrument().setPassword(password);
    }

    public String establishmentId(){
        if(getEstablishment() != null){
            return getEstablishment().getId();
        }
        return null;
    }

    public String hirerId(){
        if(getContract() != null && getContract().getHirer() != null) {
            return getContract().getHirer().getId();
        }
        return null;
    }

    public String contractorInstrumentCreditId(){
        if(getContractorInstrumentCredit() != null){
            return getContractorInstrumentCredit().getId();
        }
        return null;
    }

    public boolean withEstablishmentId(){
        return establishmentId() != null;
    }

    public void setReferences(UserDetail currentUser, ContractorInstrumentCredit instrumentCredit) {
        setContractorInstrumentCredit(instrumentCredit);
        setContractor(instrumentCredit.getContract().getContractor());
        setContract(instrumentCredit.getContract());
        setUser(currentUser);
    }

    public void checkEstablishmentIdWhenRequired(UserDetail currentUser) {
        if (!currentUser.isEstablishmentType() && !withEstablishmentId()) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ESTABLISHMENT_REQUIRED);
        }
    }

    public boolean unAuthorizeService(){
        return Arrays.asList(ServiceType.ELECTRONIC_TOLL, ServiceType.FREIGHT).contains(serviceType);
    }

    public void validateServiceType(){
        if(unAuthorizeService()){
            throw UnovationExceptions.unprocessableEntity().withErrors(SERVICE_NOT_ACCEPTABLE);
        }
    }

    public void validateEvent(Event event) {
        if(event != null && event.isRequestQuantity() && (eventQuantity == null || eventQuantity <= 0)){
            throw UnovationExceptions.unprocessableEntity().withErrors(EVENT_QUANTITY_GREATER_THAN_ZERO_REQUIRED);
        }
        if(eventValue == null || eventValue.compareTo(BigDecimal.ZERO) < 0 ||
                eventValue.compareTo(BigDecimal.ZERO) == 0){
            log.info("EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED {}", eventValue);
            throw UnovationExceptions.unprocessableEntity().withErrors(EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED);
        }
        if(getContractorInstrumentCredit().getAvailableBalance().compareTo(eventValue) < 0){
            log.info("EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE balance={} event-value={}",
                    getContractorInstrumentCredit().getAvailableBalance(), eventValue);
            throw  UnovationExceptions.unprocessableEntity().withErrors(EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE);
        }
    }

    public void setMeUp(ContractorInstrumentCredit instrumentCredit) {
        authorizationDateTime = new Date();
        solicitationDateTime = new Date();
        setLastInstrumentCreditBalance(instrumentCredit.getAvailableBalance());
        setCurrentInstrumentCreditBalance(instrumentCredit.getAvailableBalance().subtract(getEventValue()));
        situation = TransactionSituation.AUTHORIZED;
    }

    public void setEventValues(EstablishmentEvent establishmentEvent) {
        this.setEvent(establishmentEvent.getEvent());
        this.setServiceType(establishmentEvent.serviceType());
        this.setEventValue(establishmentEvent.getValue());
        this.validateEvent(establishmentEvent.getEvent());
        this.setValueFee(event.serviceFeeVal());
    }

    public ServiceAuthorize defineBatchClosingDate(){
        this.batchClosingDateTime = new Date();
        return this;
    }

    public ServiceAuthorize resetBatchClosingDate(){
        this.batchClosingDateTime = null;
        return this;
    }

    public int establishmentClosingPaymentDays(){
        if(getEstablishment() != null && getEstablishment().getCheckout() != null) {
            return getEstablishment().getCheckout().getClosingPaymentDays();
        }
        return 0;
    }

    public IssueInvoiceType establishmentIssueInvoiceType(){
        if(getEstablishment() != null) {
            return getEstablishment().getIssueInvoiceType();
        }
        return null;
    }

    @SneakyThrows
    public byte[] paymentInstrumentPasswordAsByte(){
        return getContractorInstrumentCredit().getPaymentInstrument().getPassword().getBytes();
    }

    public void setAuthorizationDateTime(Date dateTime){
        this.authorizationDateTime = ObjectUtils.clone(dateTime);
    }

    public void setSolicitationDateTime(Date dateTime){
        this.solicitationDateTime = ObjectUtils.clone(dateTime);
    }

    public void setCancellationDateTime(Date dateTime){
        this.cancellationDateTime = ObjectUtils.clone(dateTime);
    }

    public void setBatchClosingDateTime(Date dateTime){
        this.batchClosingDateTime = ObjectUtils.clone(dateTime);
    }

    public Date getAuthorizationDateTime(){
        return ObjectUtils.clone(this.authorizationDateTime);
    }

    public Date getSolicitationDateTime(){
        return ObjectUtils.clone(this.solicitationDateTime);
    }

    public Date getCancellationDateTime(){
        return ObjectUtils.clone(this.cancellationDateTime);
    }

    public Date getBatchClosingDateTime(){
        return ObjectUtils.clone(this.batchClosingDateTime);
    }

    public String establishmentEventId() {
        return establishmentEvent != null ? establishmentEvent.getId() : "";
    }

}
