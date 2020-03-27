package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.network.model.Establishment;
import br.com.unopay.api.network.model.Event;
import br.com.unopay.api.market.model.AuthorizedMember;
import br.com.unopay.api.model.validation.group.Rating;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.scheduling.model.Scheduling;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.model.Product.ZERO;
import static br.com.unopay.api.uaa.exception.Errors.AUTHORIZATION_CANNOT_BE_CANCELLED;
import static br.com.unopay.api.uaa.exception.Errors.AUTHORIZATION_IN_BATCH_PROCESSING;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE;
import static br.com.unopay.api.uaa.exception.Errors.SERVICE_AUTHORIZE_SHOULD_NOT_HAVE_EXCEPTIONAL_CIRCUMSTANCE;

@Slf4j
@Data
@Entity
@EqualsAndHashCode(exclude = {"contract"})
@Table(name = "service_authorize")
public class ServiceAuthorize implements Serializable, Updatable {

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

    @ManyToOne
    @NotNull(groups = {Reference.class})
    @JoinColumn(name="payment_instrument_id")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private PaymentInstrument paymentInstrument;

    @Column(name = "last_inst_credit_balance")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private BigDecimal lastInstrumentCreditBalance;

    @Column(name = "current_inst_credit_balance")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private BigDecimal currentInstrumentCreditBalance;

    @Column(name = "paid")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private BigDecimal paid = BigDecimal.ZERO;

    @Column(name = "total")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private BigDecimal total = BigDecimal.ZERO;

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
    private AuthorizationSituation situation;

    @Column(name = "batch_closing_date_time")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private Date batchClosingDateTime;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="service_authorize_id")
    @JsonManagedReference
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private List<ServiceAuthorizeEvent> authorizeEvents;

    @Column(name = "partial_payment")
    private Boolean partialPayment;

    @Column(name = "exceptional_circumstance")
    private Boolean exceptionalCircumstance;

    @JsonIgnore
    @Column(name = "typed_password")
    private String typedPassword;

    @Column
    @Min(value = 10,groups = {Rating.class})
    @Max(value = 50,groups = {Rating.class})
    @NotNull(groups = {Rating.class})
    @JsonView({Views.ServiceAuthorize.List.class})
    private Integer rating;

    @ManyToOne
    @JoinColumn(name="authorized_member_id")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private AuthorizedMember authorizedMember;

    @OneToOne
    @JoinColumn(name="scheduling_id")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private Scheduling scheduling;

    @Column(name="scheduling_token")
    @Size(max = 100)
    @JsonView({Views.ServiceAuthorize.List.class})
    private String schedulingToken;

    @Version
    @JsonIgnore
    private Integer version;

    public String contractId(){
        if(getContract() != null){
            return getContract().getId();
        }
        return null;
    }

    public String instrumentPassword(){
        if(getPaymentInstrument() != null){
            return getPaymentInstrument().getPassword();
        }
        return null;
    }

    public String instrumentId(){
        if(getPaymentInstrument() != null){
            return getPaymentInstrument().getId();
        }
        return null;
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

    public String authorizedMemberId() {
        if(withAuthorizedMember()) {
            return authorizedMember.getId();
        }
        return null;
    }

    public boolean withEstablishmentId(){
        return establishmentId() != null;
    }

    public void validateMe() {
        if(hasExceptionalCircumstance() && !contract.canAuthorizeServiceWithoutContractorPassword()) {
            throw UnovationExceptions.unprocessableEntity().withErrors(SERVICE_AUTHORIZE_SHOULD_NOT_HAVE_EXCEPTIONAL_CIRCUMSTANCE);
        }
    }

    public void setReferences(UserDetail currentUser, PaymentInstrument paymentInstrument, Contract contract) {
        setPaymentInstrument(paymentInstrument);
        setContractor(contract.getContractor());
        setContract(contract);
        setUser(currentUser);
    }

    public void checkEstablishmentIdWhenRequired(UserDetail currentUser) {
        if (!currentUser.isEstablishmentType() && !withEstablishmentId()) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ESTABLISHMENT_REQUIRED);
        }
    }

    public void setMeUp(PaymentInstrument paymentInstrument) {
        authorizationDateTime = new Date();
        setLastInstrumentCreditBalance(paymentInstrument.getAvailableBalance());
        setCurrentInstrumentCreditBalance(paymentInstrument.getAvailableBalance().subtract(eventValue()));
        situation = AuthorizationSituation.AUTHORIZED;
    }

    public boolean withoutEventQuantityWheRequired(){
        return getAuthorizeEvents().stream()
                .anyMatch(serviceAuthorizeEvent ->
                        serviceAuthorizeEvent.eventRequestQuantity() &&
                                serviceAuthorizeEvent.eventQuantityNotDefined());
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

    public Product contractProduct() {
        if (getContract() != null) {
            return getContract().returnProduct();
        }
        return null;
    }

    public Person establishmentPerson() {
        if (getEstablishment() != null) {
            return getEstablishment().returnPerson();
        }
        return null;
    }

    public Contractor returnContractor() {
        if (getContractor() != null) {
            return getContractor();
        }
        return null;
    }

    public boolean productHasBonus() {
        return contractProduct() != null && contractProduct().returnBonusPercentage() > ZERO;
    }

    public void addEventValueToTotal(BigDecimal value){
        this.total = this.total.add(value);
    }

    public void checkValueWhenRequired() {
        if(!hasPartialPayment() && balanceLessThanTotal()){
            log.info("EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE balance={} total={}",
                    getPaymentInstrument().getAvailableBalance(), total);
            throw  UnovationExceptions.unprocessableEntity().withErrors(EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE);
        }
    }

    @JsonProperty
    public BigDecimal eventValue(){
        return this.total;
    }

    @JsonProperty
    public Event event(){
        return authorizeEvents.stream().map(ServiceAuthorizeEvent::getEvent).findFirst().orElse(null);
    }

    @SneakyThrows
    public byte[] paymentInstrumentPasswordAsByte(){
        return getPaymentInstrument().getPassword().getBytes();
    }

    public void setAuthorizationDateTime(Date dateTime){
        this.authorizationDateTime = ObjectUtils.clone(dateTime);
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


    public Date getCancellationDateTime(){
        return ObjectUtils.clone(this.cancellationDateTime);
    }

    public Date getBatchClosingDateTime(){
        return ObjectUtils.clone(this.batchClosingDateTime);
    }

    public BigDecimal sumEventsValues() {
        return authorizeEvents.stream()
                .map(ServiceAuthorizeEvent::getEventValue).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public void setupCancellation() {
        this.cancellationDateTime = new Date();
        this.situation = AuthorizationSituation.CANCELED;
    }

    public void validateCancellation() {
        if(AuthorizationSituation.CLOSED_PAYMENT_BATCH.equals(getSituation())){
            throw UnovationExceptions.unprocessableEntity().withErrors(AUTHORIZATION_IN_BATCH_PROCESSING);
        }
        if(!AuthorizationSituation.AUTHORIZED.equals(getSituation())){
            throw UnovationExceptions.unprocessableEntity().withErrors(AUTHORIZATION_CANNOT_BE_CANCELLED);
        }

    }

    public boolean hasEvents() {
        return authorizeEvents != null && !authorizeEvents.isEmpty();
    }

    public void resetTotal() {
        this.total = BigDecimal.ZERO;
    }

    public BigDecimal validPaidValue() {

        if(hasPartialPayment() && balanceLessThanTotal()){
            return getPaymentInstrument().getAvailableBalance();
        }
        return this.paid;
    }

    private boolean balanceLessThanTotal() {
        return getPaymentInstrument().getAvailableBalance().compareTo(total) < 0;
    }

    public boolean hasPartialPayment() {
        return partialPayment != null && partialPayment;
    }

    public boolean hasExceptionalCircumstance() {
        return exceptionalCircumstance != null && exceptionalCircumstance;
    }

    public void definePaidValue() {
        if(hasPartialPayment() && balanceLessThanTotal()){
            this.paid = getPaymentInstrument().getAvailableBalance();
            return;
        }
        this.paid = this.total;
    }

    public void canBeRated() {
        if(this.rating != null)
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.AUTHORIZATION_ALREADY_RATED);

        if(!AuthorizationSituation.AUTHORIZED.equals(getSituation()))
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.AUTHORIZATION_SHOULD_BE_AUTHORIZED);

    }

    public boolean withAuthorizedMember() {
        return authorizedMember != null && authorizedMember.getId() != null;
    }

    public boolean hasSchedulingToken() {
        return schedulingToken != null;
    }

    public boolean hasScheduling(){
        return scheduling != null && scheduling.getId() != null;
    }

    public boolean hasContract(){
        return contract != null && contract.getId() != null;
    }

    public boolean hasContractor(){
        return contractor != null && contractor.getId() != null;
    }

    public boolean hasPaymentInstrument(){
        return paymentInstrument != null && paymentInstrument.getId() != null;
    }

    public void defineContractFrom(Scheduling scheduling){
        Contract contract = new Contract();
        contract.setId(scheduling.getContract().getId());
        this.contract = contract;
    }

    public void defineContractorFrom(Scheduling scheduling){
        Contractor contractor = new Contractor();
        contractor.setId(scheduling.getContractor().getId());
        this.contractor = contractor;
    }

    public void definePaymentInstrumentFrom(Scheduling scheduling, String instrumentPassword){
        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.setId(scheduling.getPaymentInstrument().getId());
        paymentInstrument.setPassword(instrumentPassword);
        this.paymentInstrument = paymentInstrument;
    }

    public void defineAuthorizedMemberFrom(Scheduling scheduling){
        AuthorizedMember authorizedMember = new AuthorizedMember();
        authorizedMember.setId(scheduling.getAuthorizedMember().getId());
        this.authorizedMember = authorizedMember;
    }

}
