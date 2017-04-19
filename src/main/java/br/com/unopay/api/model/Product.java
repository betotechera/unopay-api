package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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

    private Issuer issuer;
    private PaymentRuleGroup paymentRuleGroup;
    private AccreditedNetwork accreditedNetwork;
    private PaymentInstrumentType paymentInstrumentType;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ServiceType.class)
    @JsonView({Views.Public.class,Views.List.class})
    @CollectionTable(name = "product_service_type", joinColumns = @JoinColumn(name = "product_id"))
    private List<ServiceType> serviceType;

    private CreditInsertionType creditInsertionType;
    private BigDecimal minimumCreditInsertion;
    private BigDecimal maximumCreditInsertion;
    private Integer paymentInstrumentValidDays;
    private ProductSituation situation;
    private BigDecimal membershipFee;
    private BigDecimal creditInsertionFee;
    private BigDecimal paymentInstrumentEmissionFee;
    private BigDecimal paymentInstrumentSecondCopyFee;
    private BigDecimal administrationCreditInsertionFee;
    private Integer version;

}
