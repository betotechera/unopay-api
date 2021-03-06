package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.billing.creditcard.model.PaymentMethod;
import br.com.unopay.api.credit.model.CreditInsertionType;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.network.model.Partner;
import br.com.unopay.api.network.model.ServiceType;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.util.Rounder;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.ACCREDITED_NETWORK_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.BONUS_PERCENTAGE_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.CODE_LENGTH_NOT_ACCEPTED;
import static br.com.unopay.api.uaa.exception.Errors.ISSUER_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.MONTHS_TO_EXPIRE_BONUS_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_RULE_GROUP_ID_REQUIRED;

@Data
@Entity
@Table(name = "product")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = {"id", "code", "name"})
@ToString(exclude = {"partners", "accreditedNetwork", "paymentRuleGroup", "issuer"})
public class Product implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;
    public static final String EMPTY = "";
    public static final int MAX_CODE_LENGTH = 4, JANUARY = 1, DECEMBER = 12;
    public static final Double ZERO = 0.0;

    public Product(){}

    @Id
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @Column(name="code")
    @Size(min=1, max = 4, groups = {Create.class, Update.class})
    @NotNull(groups = {Create.class, Update.class})
    private String code;

    @Column(name="name")
    @NotNull(groups = {Create.class, Update.class})
    @Size(min=2, max = 100, groups = {Create.class, Update.class})
    private String name;

    @ManyToOne
    @JoinColumn(name="issuer_id")
    @JsonView({Views.Product.List.class})
    private Issuer issuer;

    @ManyToOne
    @JoinColumn(name="payment_rule_group_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class})
    private PaymentRuleGroup paymentRuleGroup;

    @ManyToOne
    @JoinColumn(name="accredited_network_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class})
    private AccreditedNetwork accreditedNetwork;

    @Size(min = 1)
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = PaymentInstrumentType.class)
    @Column(name = "payment_instrument_type", nullable = false)
    @JsonView({Views.Product.Detail.class, Views.Product.List.class})
    @NotNull(groups = {Create.class, Update.class})
    @CollectionTable(name = "product_payment_instrument_tp", joinColumns = @JoinColumn(name = "product_id"))
    private Set<PaymentInstrumentType> paymentInstrumentTypes;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = ServiceType.class)
    @Column(name = "service_type", nullable = false)
    @JsonView({Views.Product.Detail.class, Views.Product.List.class})
    @CollectionTable(name = "product_service_type", joinColumns = @JoinColumn(name = "product_id"))
    private Set<ServiceType> serviceTypes;

    @Size(min = 1)
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER,targetClass = CreditInsertionType.class)
    @Column(name = "credit_insertion_type", nullable = false)
    @JsonView({Views.Product.Detail.class, Views.Product.List.class})
    @NotNull(groups = {Create.class, Update.class})
    @CollectionTable(name = "product_credit_insertion_type", joinColumns = @JoinColumn(name = "product_id"))
    private Set<CreditInsertionType> creditInsertionTypes;

    @Column(name = "payment_instrument_valid_days")
    @JsonView({Views.Product.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private Integer paymentInstrumentValidDays;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.List.class})
    private ProductSituation situation;

    @Column(name = "membership_fee")
    @JsonView({Views.Product.Detail.class, Views.Product.List.class})
    private BigDecimal membershipFee;

    @Column(name = "credit_insertion_fee")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class, Views.Product.List.class})
    private BigDecimal creditInsertionFee;

    @Column(name = "pay_inst_emission_fee")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class, Views.Product.List.class})
    private BigDecimal paymentInstrumentEmissionFee;

    @Column(name = "pay_inst_second_copy_fee")
    @NotNull(groups = {Create.class, Update.class, Views.Product.List.class})
    @JsonView({Views.Product.Detail.class})
    private BigDecimal paymentInstrumentSecondCopyFee;

    @Column(name = "adm_credit_insert_fee")
    @NotNull(groups = {Create.class, Update.class, Views.Product.List.class})
    @JsonView({Views.Product.Detail.class})
    private BigDecimal administrationCreditInsertionFee;

    @Column(name = "annuity")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class, Views.Product.List.class})
    private BigDecimal annuity;

    @Column(name = "member_annuity")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class, Views.Product.List.class})
    private BigDecimal memberAnnuity;

    @Column(name = "payment_installments")
    @JsonView({Views.Product.Detail.class, Views.Product.List.class})
    private Integer paymentInstallments;

    @Min(1)
    @Column(name = "contract_validity_days")
    @JsonView({Views.Product.Detail.class, Views.Product.List.class})
    private Integer contractValidityDays;

    @JsonIgnore
    @ManyToMany(mappedBy = "products")
    private Set<Partner> partners;

    @Max(1)
    @Min(0)
    @Column(name = "bonus_percentage")
    @JsonView(Views.Product.Detail.class)
    private Double bonusPercentage;

    @Min(0)
    @Column(name="months_to_expire_bonus")
    @JsonView(Views.Product.Detail.class)
    private Integer monthsToExpireBonus;

    @Column(name = "with_partner_integration")
    @JsonView({Views.Product.Detail.class})
    private Boolean withPartnerIntegration = false;

    @Size(min = 1)
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER,targetClass = PaymentMethod.class)
    @Column(name = "payment_method", nullable = false)
    @JsonView({Views.Product.Detail.class, Views.Product.List.class})
    @NotNull(groups = {Create.class, Update.class})
    @CollectionTable(name = "product_payment_methods", joinColumns = @JoinColumn(name = "product_id"))
    private Set<PaymentMethod> paymentMethods;

    @Version
    @JsonIgnore
    private Integer version;

    public void validate(){
        if(getCode().length() > MAX_CODE_LENGTH){
            throw UnovationExceptions.unprocessableEntity().withErrors(CODE_LENGTH_NOT_ACCEPTED);
        }
        if(getIssuer() != null && getIssuer().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ISSUER_ID_REQUIRED);
        }
        if(getAccreditedNetwork() != null && getAccreditedNetwork().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ACCREDITED_NETWORK_ID_REQUIRED);
        }
        if(getPaymentRuleGroup() != null && getPaymentRuleGroup().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_RULE_GROUP_ID_REQUIRED);
        }

        validateBonus();
    }

    private void validateBonus() {
        if(bonusPercentage != null && monthsToExpireBonus == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(MONTHS_TO_EXPIRE_BONUS_REQUIRED);
        }
        if(bonusPercentage == null && monthsToExpireBonus != null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(BONUS_PERCENTAGE_REQUIRED);
        }
    }

    void validateCreditInsertionType(Set<CreditInsertionType> creditInsertionTypes){
        if(!this.creditInsertionTypes.containsAll(creditInsertionTypes)){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.CREDIT_INSERTION_TYPE_NOT_IN_PRODUCT);
        }
    }

    public void validateCreditInsertionType(CreditInsertionType creditInsertionType){
        if(!this.creditInsertionTypes.contains(creditInsertionType)){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.CREDIT_INSERTION_TYPE_NOT_IN_PRODUCT);
        }
    }

    public String issuerBin() {
        return issuer.getBin() != null ? issuer.getBin() : "";
    }

    @JsonProperty
    public BigDecimal getInstallmentValue(){
        return annuity.divide(new BigDecimal(getPaymentInstallments()),2, Rounder.ROUND_STRATEGY);
    }

    public BigDecimal installmentTotal(Integer memberTotal) {
        return this.getMemberAnnuity() == null ? getInstallmentValue() : installmentWithMembers(memberTotal);
    }

    private BigDecimal installmentWithMembers(Integer memberTotal) {
        return this.annuity.add(this.getMemberAnnuity()
                .multiply(new BigDecimal(memberTotal)))
                .divide(new BigDecimal(getPaymentInstallments()),2, Rounder.ROUND_STRATEGY);
    }

    public String issuerPersonShortName() {
        if (getIssuer() != null) {
            return getIssuer().personShortName();
        }
        return null;
    }

    public String issuerDocumentNumber() {
        if (getIssuer() != null) {
            return getIssuer().documentNumber();
        }
        return null;
    }

    public String networkId() {
        if (getAccreditedNetwork() != null) {
            return getAccreditedNetwork().getId();
        }
        return null;
    }

    public Double returnBonusPercentage() {
        if (getBonusPercentage() != null && !getBonusPercentage().toString().equals(EMPTY)) {
            return getBonusPercentage();
        }
        return ZERO;
    }

    public Boolean withClub(){
        return withPartnerIntegration == null ? Boolean.FALSE : withPartnerIntegration;
    }

    public boolean acceptOnlyCard(){
        return this.getPaymentMethods().size() == 1 &&
                this.getPaymentMethods().contains(PaymentMethod.CARD);
    }

    public boolean hasBonusExpiration() {
        return this.getMonthsToExpireBonus() != null;
    }

    public String getId() {
        return id;
    }
}
