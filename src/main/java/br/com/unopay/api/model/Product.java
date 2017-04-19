package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
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

@Data
@Entity
@Table(name = "product")
public class Product implements Serializable {

    public static final long serialVersionUID = 1L;

    public Product(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name="code")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    @Size(min=1, max = 4, groups = {Create.class, Update.class})
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
    @JsonView({Views.Public.class,Views.List.class})
    @CollectionTable(name = "product_service_type", joinColumns = @JoinColumn(name = "product_id"))
    private List<ServiceType> serviceType;

    @Column(name = "credit_insertion_type")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    private CreditInsertionType creditInsertionType;

    @Column(name = "minimum_credit_insertion")
    private BigDecimal minimumCreditInsertion;

    @Column(name = "maximum_credit_insertion")
    private BigDecimal maximumCreditInsertion;

    @Column(name = "payment_instrument_valid_days")
    @NotNull(groups = {Create.class, Update.class})
    private Integer paymentInstrumentValidDays;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    private ProductSituation situation;

    @Column(name = "membership_fee")
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal membershipFee;

    @Column(name = "credit_insertion_fee")
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal creditInsertionFee;

    @Column(name = "pay_inst_emission_fee")
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal paymentInstrumentEmissionFee;

    @Column(name = "pay_inst_second_copy_fee")
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal paymentInstrumentSecondCopyFee;

    @Column(name = "adm_credit_insert_fee")
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal administrationCreditInsertionFee;

    @Version
    @JsonIgnore
    private Integer version;

}
