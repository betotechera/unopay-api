package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE;

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

    @Column(name = "value")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private BigDecimal value;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="service_authorize_id")
    @JsonManagedReference
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private Set<ServiceAuthorizeEvent> authorizeEvents;

    @JsonIgnore
    @Column(name = "typed_password")
    private String typedPassword;

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

    public boolean withEstablishmentId(){
        return establishmentId() != null;
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
        situation = TransactionSituation.AUTHORIZED;
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

    public void validateEvent(BigDecimal eventValue) {
        if(getPaymentInstrument().getAvailableBalance().compareTo(eventValue) < 0){
            log.info("EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE balance={} event-value={}",
                    getPaymentInstrument().getAvailableBalance(), eventValue);
            throw  UnovationExceptions.unprocessableEntity().withErrors(EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE);
        }
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

    public BigDecimal eventValue() {
        return authorizeEvents.stream()
                .map(ServiceAuthorizeEvent::getEventValue).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }
}
