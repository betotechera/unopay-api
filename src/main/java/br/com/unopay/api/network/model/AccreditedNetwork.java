package br.com.unopay.api.network.model;

import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.bacen.model.Checkout;
import br.com.unopay.api.bacen.model.InvoiceReceipt;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@EqualsAndHashCode(exclude = {"paymentRuleGroups", "issuers"})
@ToString(exclude = {"paymentRuleGroups", "issuers"})
@Table(name = "accredited_network")
public class AccreditedNetwork implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator="system-uuid")
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToMany
    @BatchSize(size = 10)
    @JsonView({Views.AccreditedNetwork.Detail.class})
    @JoinTable(name = "accredited_payment_rules",
            joinColumns = { @JoinColumn(name = "accredited_network_id") },
            inverseJoinColumns = { @JoinColumn(name = "payment_rule_group_id") })
    private Set<PaymentRuleGroup> paymentRuleGroups;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "accredited_network_issuer",
            joinColumns = { @JoinColumn(name = "accredited_network_id") },
            inverseJoinColumns = { @JoinColumn(name = "issuer_id") })
    private Set<Issuer> issuers;

    @Valid
    @ManyToOne
    @JoinColumn(name="person_id")
    @NotNull(groups = {Create.class, Update.class})
    private Person person;

    @Column(name = "merchant_discount_rate")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.AccreditedNetwork.Detail.class})
    private Double merchantDiscountRate;

    @Valid
    @Embedded
    @JsonView({Views.AccreditedNetwork.Detail.class})
    private Checkout checkout;

    @Valid
    @Embedded
    @JsonView({Views.AccreditedNetwork.Detail.class})
    private InvoiceReceipt invoiceReceipt;

    @Column(name = "logo_uri")
    @JsonView({Views.AccreditedNetwork.Detail.class})
    private String logoUri;

    @Column(name = "background_color")
    @JsonView({Views.AccreditedNetwork.Detail.class})
    private String backgroundColor;

    @Column(name = "text_color")
    @JsonView({Views.AccreditedNetwork.Detail.class})
    private String textColor;

    @Valid
    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JoinColumn(name="bank_account_id")
    @JsonView({Views.BankAccount.class})
    private BankAccount bankAccount;

    public AccreditedNetwork(){}

    public void validate() {
        if(merchantDiscountRate < 0 || merchantDiscountRate > 1D) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.INVALID_MERCHANT_DISCOUNT_RATE_RANGE);
        }
        checkout.validate();
    }

    public void updateModel(AccreditedNetwork accreditedNetwork) {
        person.updateMe(accreditedNetwork.getPerson(), (o) -> o.updateForAccreditedNetwork(o));
        this.merchantDiscountRate = accreditedNetwork.getMerchantDiscountRate();
        this.bankAccount = accreditedNetwork.getBankAccount();
        this.checkout.updateModel(this.getCheckout());
        this.logoUri = accreditedNetwork.logoUri;
        this.backgroundColor = accreditedNetwork.backgroundColor;
        this.textColor = accreditedNetwork.textColor;

    }

    public String getId() {
        return id;
    }

    public String documentNumber(){
        return person.documentNumber();
    }

    public Set<String> issuersIds(){
        return getIssuers().stream().map(Issuer::getId).collect(Collectors.toSet());
    }
}
