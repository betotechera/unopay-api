package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*; // NOSONAR
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import static br.com.unopay.api.uaa.exception.Errors.ACCREDITED_NETWORK_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.ISSUER_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_RULE_GROUP_ID_REQUIRED;

@Data
@Entity
@Table(name = "product")
public class Product implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;

    public Product(){}

    @Id
    @Column(name="id")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @Column(name="code")
    @JsonView({Views.Public.class,Views.List.class})
    @Size(min=1, max = 4, groups = {Create.class, Update.class})
    @NotNull(groups = {Create.class, Update.class})
    private String code;

    @Column(name="name")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    @Size(min=2, max = 100, groups = {Create.class, Update.class})
    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ProductType type;

    @Valid
    @ManyToOne
    @JoinColumn(name="issuer_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Issuer issuer;

    @Valid
    @ManyToOne
    @JoinColumn(name="payment_rule_group_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private PaymentRuleGroup paymentRuleGroup;

    @Valid
    @ManyToOne
    @JoinColumn(name="accredited_network_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private AccreditedNetwork accreditedNetwork;

    @Column(name = "payment_instrument_type")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    private PaymentInstrumentType paymentInstrumentType;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ServiceType.class)
    @JsonView({Views.Public.class})
    @CollectionTable(name = "product_service_type", joinColumns = @JoinColumn(name = "product_id"))
    private List<ServiceType> serviceType;

    @Column(name = "credit_insertion_type")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private CreditInsertionType creditInsertionType;

    @Column(name = "minimum_credit_insertion")
    @JsonView({Views.Public.class,Views.List.class})
    private BigDecimal minimumCreditInsertion;

    @Column(name = "maximum_credit_insertion")
    @JsonView({Views.Public.class,Views.List.class})
    private BigDecimal maximumCreditInsertion;

    @Column(name = "payment_instrument_valid_days")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private Integer paymentInstrumentValidDays;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private ProductSituation situation;

    @Column(name = "membership_fee")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private BigDecimal membershipFee;

    @Column(name = "credit_insertion_fee")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private BigDecimal creditInsertionFee;

    @Column(name = "pay_inst_emission_fee")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private BigDecimal paymentInstrumentEmissionFee;

    @Column(name = "pay_inst_second_copy_fee")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private BigDecimal paymentInstrumentSecondCopyFee;

    @Column(name = "adm_credit_insert_fee")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private BigDecimal administrationCreditInsertionFee;

    @Version
    @JsonIgnore
    private Integer version;

    public void validate(){
        if(getIssuer() != null && getIssuer().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ISSUER_ID_REQUIRED);
        }
        if(getAccreditedNetwork() != null && getAccreditedNetwork().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ACCREDITED_NETWORK_ID_REQUIRED);
        }
        if(getPaymentRuleGroup() != null && getPaymentRuleGroup().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_RULE_GROUP_ID_REQUIRED);
        }
    }

    @JsonIgnore
    public boolean containsServiceType(ServiceType serviceType) {
        return this.serviceType != null && this.serviceType.contains(serviceType);
    }
}
