package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*; // NOSONAR
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

import static javax.persistence.EnumType.STRING;

@Data
@Entity
@EqualsAndHashCode(exclude = {"paymentRuleGroups"})
@Table(name = "accredited_network")
public class AccreditedNetwork implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator="system-uuid")
    @Column(name="id")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToMany
    @BatchSize(size = 10)
    @JsonView({Views.Public.class})
    @JoinTable(name = "accredited_payment_rules",
            joinColumns = { @JoinColumn(name = "accredited_network_id") },
            inverseJoinColumns = { @JoinColumn(name = "payment_rule_group_id") })
    private Set<PaymentRuleGroup> paymentRuleGroups;

    @Valid
    @ManyToOne
    @JoinColumn(name="person_id")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private Person person;

    @Column(name = "merchant_discount_rate")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Double merchantDiscountRate;

    @Valid
    @Enumerated(STRING)
    @Column(name="type")
    @JsonView({Views.Public.class,Views.List.class})
    private AccreditedNetworkType type;

    @Valid
    @Embedded
    @JsonView({Views.Public.class})
    private Checkout checkout;

    @Valid
    @Embedded
    @JsonView({Views.Public.class})
    private InvoiceReceipt invoiceReceipt;

    @Valid
    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JoinColumn(name="bank_account_id")
    @JsonView({Views.Public.class,Views.List.class})
    private BankAccount bankAccount;

    public void validate() {
        if(merchantDiscountRate < 0 || merchantDiscountRate > 1D) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.INVALID_MERCHANT_DISCOUNT_RATE_RANGE);
        }
        checkout.validate();
    }

    public void updateModel(AccreditedNetwork accreditedNetwork) {
        person.update(accreditedNetwork.getPerson(), (o) -> o.updateForAccreditedNetwork(o));
        this.merchantDiscountRate = accreditedNetwork.getMerchantDiscountRate();
        this.bankAccount = accreditedNetwork.getBankAccount();
        this.checkout.updateModel(this.getCheckout());
    }
}
