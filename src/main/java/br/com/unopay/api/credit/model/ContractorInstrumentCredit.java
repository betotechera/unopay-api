package br.com.unopay.api.credit.model;

import br.com.unopay.api.network.model.ServiceType;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.CREDIT_ALREADY_CANCELED;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_UNAVAILABLE;
import static br.com.unopay.api.uaa.exception.Errors.EXPIRATION_DATA_GREATER_THAN_NOW_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.EXPIRED_CREDIT;
import static br.com.unopay.api.uaa.exception.Errors.SERVICE_NOT_ACCEPTED;
import static br.com.unopay.api.uaa.exception.Errors.VALUE_GREATER_THAN_BALANCE;
import static br.com.unopay.api.uaa.exception.Errors.VALUE_GREATER_THAN_ZERO_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.VALUE_GREATER_THEN_AVAILABLE_BALANCE;
import static java.math.BigDecimal.ZERO;


@Data
@Entity
@EqualsAndHashCode(exclude = {"paymentInstrument", "contract"})
@ToString(exclude = {"paymentInstrument", "contract"})
@Table(name = "contractor_instrument_credit")
public class ContractorInstrumentCredit implements Serializable, Updatable {

    public ContractorInstrumentCredit(){}

    public static final long serialVersionUID = 1L;


    @Id
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    @NotNull(groups = {Reference.class})
    private String id;

    @ManyToOne
    @JoinColumn(name="payment_instrument_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.ContractorInstrumentCredit.List.class})
    private PaymentInstrument paymentInstrument;

    @ManyToOne
    @JoinColumn(name="contract_id")
    @JsonView({Views.ContractorInstrumentCredit.Detail.class})
    private Contract contract;

    @ManyToOne
    @JoinColumn(name="credit_payment_account_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.ContractorInstrumentCredit.Detail.class})
    private CreditPaymentAccount creditPaymentAccount;

    @Column(name = "service_type")
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Column(name = "credit_source")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.ContractorInstrumentCredit.Detail.class})
    private InstrumentCreditSource creditSource;

    @Column(name = "value")
    @JsonView({Views.ContractorInstrumentCredit.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal value;

    @Column(name = "expiration_date_time")
    @JsonView({Views.ContractorInstrumentCredit.List.class,Views.ServiceAuthorize.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private Date expirationDateTime;

    @Column(name = "issuer_fee")
    @JsonView({Views.ContractorInstrumentCredit.List.class})
    private BigDecimal issuerFee;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.ContractorInstrumentCredit.List.class})
    private CreditSituation situation;

    @Column(name = "available_balance")
    @JsonView({Views.ContractorInstrumentCredit.List.class,Views.ServiceAuthorize.Detail.class})
    private BigDecimal availableBalance;

    @Column(name = "blocked_balance")
    @JsonView({Views.ContractorInstrumentCredit.Detail.class})
    private BigDecimal blockedBalance;

    @Column(name = "created_date_time")
    @JsonView({Views.ContractorInstrumentCredit.List.class})
    private Date createdDateTime;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.ContractorInstrumentCredit.Detail.class})
    private ContractorInstrumentCreditType type;

    @Version
    @JsonIgnore
    private Integer version;

    public void validateMe(Contract contract){
        validateProduct(contract);
        if(serviceType != null && !contract.containsService(serviceType)){
            throw UnovationExceptions.unprocessableEntity().withErrors(SERVICE_NOT_ACCEPTED);
        }
        if(expirationDateTime.before(new Date())){
            throw UnovationExceptions.unprocessableEntity().withErrors(EXPIRATION_DATA_GREATER_THAN_NOW_REQUIRED);
        }
    }

    public void validateValue() {
        if(ZERO.compareTo(value) == 0 || ZERO.compareTo(value) == 1){
            throw UnovationExceptions.unprocessableEntity().withErrors(VALUE_GREATER_THAN_ZERO_REQUIRED);
        }
        if(creditSourceIsHirer() && creditPaymentAccount.getAvailableBalance().compareTo(value) == -1){
            throw UnovationExceptions.unprocessableEntity().withErrors(VALUE_GREATER_THAN_BALANCE);
        }
    }

    public void defineTypeWhenRequired() {
        if(type == null) {
            type = ContractorInstrumentCreditType.NORMAL;
        }
    }

    private void validateProduct(Contract contract) {
        paymentInstrument.validateContractProduct(contract);
    }
 
    public void setupMyCreate(Contract contract){
        this.createdDateTime = new Date();
        this.contract = contract;
        this.availableBalance = this.value;
        this.blockedBalance = ZERO;
        this.situation = CreditSituation.AVAILABLE;
        this.issuerFee = contract.productCreditInsertFee();
        this.creditSource = this.creditSource == null? InstrumentCreditSource.HIRER : this.creditSource;
        defineTypeWhenRequired();
    }

    public String getPaymentInstrumentId() {
        if(paymentInstrument != null){
            return  paymentInstrument.getId();
        }
        return null;
    }

    public String getCreditPaymentAccountId() {
        if(creditPaymentAccount != null){
            return creditPaymentAccount.getId();
        }
        return null;
    }

    public String getCreditPaymentAccountProductCode(){
        if(getCreditPaymentAccount() != null){
            return getCreditPaymentAccount().getProductCode();
        }
        return null;
    }

    public ServiceType getCreditPaymentAccountServiceType(){
        if(getCreditPaymentAccount() != null){
            return getCreditPaymentAccount().getServiceType();
        }
        return null;
    }

    public boolean myPaymentInstrumentIn(List<PaymentInstrument> paymentInstruments) {
        return paymentInstruments.stream()
                .anyMatch(p-> Objects.equals(p.getId(), getPaymentInstrumentId()));
    }

    public boolean myCreditPaymentAccountIn(List<CreditPaymentAccount> creditPaymentAccounts) {
        return creditPaymentAccounts.stream()
                .anyMatch(p-> Objects.equals(p.getId(), getCreditPaymentAccountId()));
    }

    public String contractId(){
        if(getContract() != null){
            return getContract().getId();
        }
        return null;
    }

    public void cancel(){
        if(CreditSituation.CANCELED.equals(situation)){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_ALREADY_CANCELED);
        }
        setSituation(CreditSituation.CANCELED);
    }

    public boolean contractIs(String contractId){
        if(getContract() != null) {
         return  Objects.equals(getContract().getId(), contractId);
        }
        return false;
    }

    public void subtract(BigDecimal value) {
        if(this.availableBalance.compareTo(value) == -1){
            throw UnovationExceptions.unprocessableEntity().withErrors(VALUE_GREATER_THEN_AVAILABLE_BALANCE);
        }
        this.availableBalance = this.availableBalance.subtract(value);
    }

    public void validate() {
        if(new Date().after(expirationDateTime)){
            throw UnovationExceptions.unprocessableEntity().withErrors(EXPIRED_CREDIT);
        }
        if(!CreditSituation.AVAILABLE.equals(situation)){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_UNAVAILABLE);
        }
    }

    public String productId(){
        if(getContract()!=null && getContract().getProduct() != null) {
            return getContract().getProduct().getId();
        }
        return null;
    }

    public Date getCreatedDateTime(){
        return ObjectUtils.clone(this.createdDateTime);
    }

    public void setCreatedDateTime(Date dateTime){
        this.createdDateTime = ObjectUtils.clone(dateTime);
    }

    public Date getExpirationDateTime(){
        return ObjectUtils.clone(this.expirationDateTime);
    }

    public void setExpirationDateTime(Date dateTime){
        this.expirationDateTime = ObjectUtils.clone(dateTime);
    }

    public boolean creditSourceIsHirer() {
        return InstrumentCreditSource.HIRER.equals(creditSource);
    }
}
