package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.pamcary.translate.KeyBase;
import br.com.unopay.api.pamcary.translate.KeyDate;
import br.com.unopay.api.pamcary.translate.KeyField;
import br.com.unopay.api.pamcary.translate.KeyFieldReference;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.EVENT_QUANTITY_GREATER_THAN_ZERO_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.EVENT_VALUE_GREATER_THAN_CREDIT_BALANCE;
import static br.com.unopay.api.uaa.exception.Errors.EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.SERVICE_NOT_ACCEPTABLE;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;

@Slf4j
@Data
@Entity
@KeyBase(key = "viagem")
@EqualsAndHashCode(exclude = {"contract"})
@Table(name = "service_authorize")
public class ServiceAuthorize implements Serializable {

    public static final long serialVersionUID = 1L;

    public ServiceAuthorize(){}

    @Id
    @Column(name="id")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @KeyField(baseField = "abastecimento.consumo.autorizacao.numero")
    @Column(name = "authorization_number")
    private String authorizationNumber;

    @Column(name = "authorization_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date authorizationDateTime;

    @ManyToOne
    @NotNull(groups = {Reference.class})
    @JoinColumn(name="establishment_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Establishment establishment;

    @ManyToOne
    @KeyFieldReference
    @NotNull(groups = {Reference.class})
    @JoinColumn(name="contract_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Contract contract;

    @ManyToOne
    @KeyFieldReference
    @NotNull(groups = {Reference.class})
    @JoinColumn(name="contractor_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Contractor contractor;

    @Column(name = "service_type")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class,Views.List.class})
    private ServiceType serviceType;

    @ManyToOne
    @NotNull(groups = {Reference.class})
    @JoinColumn(name="event_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Event event;

    @Column(name = "event_quantity")
    private Double eventQuantity;

    @Column(name = "event_value")
    @KeyField(baseField = "abastecimento.valor")
    private BigDecimal eventValue;

    @Column(name = "value_fee")
    private BigDecimal valueFee;

    @Column(name = "solicitation_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    @KeyDate(pattern = "dd/MM/yyyy hh:mm:ss")
    @KeyField(baseField = "abastecimento.consumo.datahora")
    private Date solicitationDateTime;

    @Column(name = "credit_insertion_type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class,Views.List.class})
    private CreditInsertionType creditInsertionType;

    @ManyToOne
    @NotNull(groups = {Reference.class})
    @JoinColumn(name="contractor_inst_credit_id")
    @JsonView({Views.Public.class,Views.List.class})
    private ContractorInstrumentCredit contractorInstrumentCredit;

    @Column(name = "last_inst_credit_balance")
    private BigDecimal lastInstrumentCreditBalance;

    @Column(name = "current_inst_credit_balance")
    private BigDecimal currentInstrumentCreditBalance;

    @Column(name = "cancellation_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cancellationDateTime;

    @Column(name = "transaction_log_code")
    private Integer transactionLogCode;

    @Column(name = "transaction_log")
    private String transactionLog;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonView({Views.Public.class,Views.List.class})
    private UserDetail user;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class,Views.List.class})
    private TransactionSituation situation;

    @Column(name = "batch_closing_date_time")
    private Date batchClosingDateTime;

    @Version
    @JsonIgnore
    private Integer version;

    @Transient
    @KeyField(baseField = "abastecimento.operacao")
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
        if(eventValue == null || eventValue.compareTo(BigDecimal.ZERO) == -1 ||
                eventValue.compareTo(BigDecimal.ZERO) == 0){
            log.info("EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED {}", eventValue);
            throw UnovationExceptions.unprocessableEntity().withErrors(EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED);
        }
        if(getContractorInstrumentCredit().getAvailableBalance().compareTo(eventValue) == -1){
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

    public ServiceAuthorize toFuelSupply(FreightReceipt freightReceipt) {
        setContract(freightReceipt.getContract());
        setContractor(freightReceipt.getContractor());
        setEstablishment(freightReceipt.getEstablishment());
        setEventQuantity(freightReceipt.getFuelSupplyQuantity());
        setEventValue(freightReceipt.getFuelSupplyValue());
        setCreditInsertionType(freightReceipt.getCreditInsertionType());
        setContractorInstrumentCredit(freightReceipt.getInstrumentCredit());
        instrumentPassword(freightReceipt.getInstrumentPassword());
        setEvent(freightReceipt.getFuelEvent());
        serviceType = ServiceType.FUEL_ALLOWANCE;
        return this;
    }

    public ServiceAuthorize buildBatchSlosingDate(){
        this.batchClosingDateTime = new Date();
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

}
