package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Set;

import static javax.persistence.EnumType.STRING;

@Data
@Entity
@EqualsAndHashCode(exclude = {"paymentRuleGroups"})
@Table(name = "accredited_network")
public class AccreditedNetwork {

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToMany
    @BatchSize(size = 10)
    @JsonView({Views.Public.class})
    @JoinTable(name = "accredited_payment_rules", joinColumns = { @JoinColumn(name = "accredited_network_id") }, inverseJoinColumns = { @JoinColumn(name = "payment_rule_group_id") })
    private Set<PaymentRuleGroup> paymentRuleGroups;

    @Valid
    @ManyToOne
    @JoinColumn(name="person_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Person person;

    @DecimalMin("0")
    @DecimalMax("100")
    @Column(name = "merchant_discount_rate")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private BigDecimal merchantDiscountRate;

    @Valid
    @Enumerated(STRING)
    @Column(name="type")
    @JsonView({Views.Public.class,Views.List.class})
    private AccreditedNetworkType type;

    @Valid
    @Embedded
    @JsonView({Views.Public.class})
    private PaymentMethod paymentMethod;

    @Valid
    @Embedded
    @JsonView({Views.Public.class})
    private InvoiceReceipt invoiceReceipt;

    @Valid
    @ManyToOne
    @JoinColumn(name="bank_account_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private BankAccount bankAccount;

    public void validate() {
    }

    public void updateModel(AccreditedNetwork accreditedNetwork) {
        this.person.updateForAccreditedNetwork(accreditedNetwork.getPerson());
        this.merchantDiscountRate = accreditedNetwork.getMerchantDiscountRate();
        this.bankAccount = accreditedNetwork.getBankAccount();
        this.paymentMethod.updateModel(accreditedNetwork.getPaymentMethod());
    }
}
