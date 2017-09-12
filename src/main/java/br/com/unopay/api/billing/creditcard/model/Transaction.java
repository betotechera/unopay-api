package br.com.unopay.api.billing.creditcard.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.billing.creditcard.model.CardBrand.fromCardNumber;

@Data
@Entity
@Table(name = "transaction",
        uniqueConstraints =
        @UniqueConstraint(name = "transaction_uk", columnNames = {"order_id"}))
@ToString
@EqualsAndHashCode(of = {"id"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {

    public Transaction(){
        this.createDateTime = new Date();
    }

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Billing.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name = "order_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Billing.List.class})
    private String orderId;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class})
    @JsonView({Views.Billing.List.class})
    private PaymentMethod paymentMethod;

    @NotNull(groups = {Create.class})
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Billing.List.class})
    private TransactionStatus status = TransactionStatus.PENDING;

    @Valid
    @Embedded
    @JsonView({Views.Billing.List.class})
    @NotNull(groups = {Create.class})
    private Amount amount;

    @Column(name = "installments")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Billing.List.class})
    private Integer installments = 1;

    @Column(name = "create_date_time")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Billing.List.class})
    private Date createDateTime;

    @Column(name = "captured_requested_at")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Billing.List.class})
    private Date capturedRequestedAt;

    @Column(name = "cancellation_requested_at")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Billing.List.class})
    private Date cancellationRequestedAt;

    @Valid
    @Transient
    private CreditCard creditCard;

    @JsonIgnore
    @Version
    private Integer version;

    public long getLongAmountValue() {
        return getAmount().getValue().multiply(new BigDecimal(100)).longValue();
    }

    public CardBrand getCardBrand() {
        return fromCardNumber(getCreditCard().getNumber());
    }

    public int getAmountCurrencyIsoCode() {
        return getAmount().getCurrency().getIso();
    }
}
