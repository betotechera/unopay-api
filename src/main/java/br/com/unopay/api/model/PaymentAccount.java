package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
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
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "payment_account")
public class PaymentAccount implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;

    public PaymentAccount(){}

    @Id
    @Column(name="id")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @Column(name = "transaction_created_date_time")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Date transactionCreatedDateTime;

    @Valid
    @ManyToOne
    @JoinColumn(name="issuer_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Issuer issuer;

    @Valid
    @ManyToOne
    @JoinColumn(name="product_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Product product;

    @Valid
    @ManyToOne
    @JoinColumn(name="payment_rule_group_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private PaymentRuleGroup paymentRuleGroup;

    @Column(name = "hirer_document")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String hirerDocument;

    @Column(name = "service_type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class,Views.List.class})
    private ServiceType serviceType;

    @Column(name = "credit_insertion_type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private CreditInsertionType creditInsertionType;

    @Column(name = "solicitation_date_time")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private Date solicitationDateTime;

    @Column(name = "credit_number")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private Long creditNumber;

    @Column(name = "insertion_created_date_time")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private Date insertionCreatedDateTime;

    @Column(name = "value")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal value;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private CreditSituation situation;

    @Column(name = "credit_source")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private String creditSource;

    @Column(name = "cnab_id")
    @JsonView({Views.Public.class,Views.List.class})
    private String cnabId;

    @Column(name = "available_balance")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal availableBalance;

    @Valid
    @ManyToOne
    @JoinColumn(name="payment_bank_account_id")
    @JsonView({Views.Public.class,Views.List.class})
    private PaymentBankAccount paymentBankAccount;

    @Version
    @JsonIgnore
    private Integer version;

    public void setupMyCreate(){
        insertionCreatedDateTime = new Date();
    }

}
