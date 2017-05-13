package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.ServiceType;
import static br.com.unopay.api.uaa.exception.Errors.EXPIRATION_DATA_GREATER_THAN_NOW_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_CODE_NOT_MET;
import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_ID_NOT_MET;
import static br.com.unopay.api.uaa.exception.Errors.SERVICE_NOT_ACCEPTED;
import static br.com.unopay.api.uaa.exception.Errors.VALUE_GREATER_THAN_BALANCE;
import static br.com.unopay.api.uaa.exception.Errors.VALUE_GREATER_THAN_ZERO_REQUIRED;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


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
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @ManyToOne
    @JoinColumn(name="payment_instrument_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private PaymentInstrument paymentInstrument;

    @ManyToOne
    @JoinColumn(name="contract_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Contract contract;

    @ManyToOne
    @JoinColumn(name="credit_payment_account_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private CreditPaymentAccount creditPaymentAccount;

    @Column(name = "service_type")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private ServiceType serviceType;

    @Column(name = "credit_insertion_type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private CreditInsertionType creditInsertionType;

    @Column(name = "installment_number")
    @JsonView({Views.Public.class,Views.List.class})
    private Long installmentNumber;

    @Column(name = "value")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal value;

    @Column(name = "expiration_date_time")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private Date expirationDateTime;

    @Column(name = "issuer_fee")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal issuerFee;

    @Column(name = "situation")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private CreditSituation situation;

    @Column(name = "available_balance")
    @JsonView({Views.Public.class,Views.List.class})
    private BigDecimal availableBalance;

    @Column(name = "blocked_balance")
    @JsonView({Views.Public.class,Views.List.class})
    private BigDecimal blockedBalance;

    @Column(name = "created_date_time")
    @JsonView({Views.Public.class,Views.List.class})
    private Date createdDateTime;

    public void validateMe(Contract contract){
        validateProduct(contract);
        if(!contract.containsService(serviceType)){
            throw UnovationExceptions.unprocessableEntity().withErrors(SERVICE_NOT_ACCEPTED);
        }
        if(expirationDateTime.before(new Date())){
            throw UnovationExceptions.unprocessableEntity().withErrors(EXPIRATION_DATA_GREATER_THAN_NOW_REQUIRED);
        }
    }

    public void validateValue() {
        if(BigDecimal.ZERO.compareTo(value) == 0 || BigDecimal.ZERO.compareTo(value) == 1){
            throw UnovationExceptions.unprocessableEntity().withErrors(VALUE_GREATER_THAN_ZERO_REQUIRED);
        }
        if(creditPaymentAccount.getAvailableBalance().compareTo(value) == -1){
            throw UnovationExceptions.unprocessableEntity().withErrors(VALUE_GREATER_THAN_BALANCE);
        }
    }

    private void validateProduct(Contract contract) {
        if(!Objects.equals(paymentInstrument.getProduct().getCode(), contract.getProduct().getCode())){
            throw UnovationExceptions.unprocessableEntity().withErrors(PRODUCT_CODE_NOT_MET);
        }
        if(!Objects.equals(paymentInstrument.getProduct().getId(), contract.getProduct().getId())){
            throw UnovationExceptions.unprocessableEntity().withErrors(PRODUCT_ID_NOT_MET);
        }
    }

    public void setupMyCreate(Contract contract){
        this.createdDateTime = new Date();
        this.contract = contract;
        this.availableBalance = this.value;
        this.situation = CreditSituation.AVAILABLE;
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

    public void incrementInstallmentNumber(ContractorInstrumentCredit last) {
        Long lastCreditNumber = Optional.ofNullable(last)
                                        .map(ContractorInstrumentCredit::getInstallmentNumber).orElse(null);
        if(lastCreditNumber == null || (last == null || last.serviceType != serviceType)){
            installmentNumber = 1L;
            return;
        }
        installmentNumber += lastCreditNumber;
    }

    public boolean myPaymentInstrumentIn(List<PaymentInstrument> paymentInstruments) {
        return paymentInstruments.stream()
                .anyMatch(p-> Objects.equals(p.getId(), getPaymentInstrumentId()));
    }

    public boolean myCreditPaymentAccountIn(List<CreditPaymentAccount> creditPaymentAccounts) {
        return creditPaymentAccounts.stream()
                .anyMatch(p-> Objects.equals(p.getId(), getCreditPaymentAccountId()));
    }

}
