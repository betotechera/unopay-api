package br.com.unopay.api.billing.boleto.model;


import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;

@Slf4j
@Data
@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Ticket.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name = "source_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Ticket.Detail.class})
    private String sourceId;

    @NotNull(groups = {Create.class})
    @JoinColumn(name="issuer_document")
    @JsonView({Views.Ticket.Detail.class})
    private String issuerDocument;

    @NotNull(groups = {Create.class})
    @JoinColumn(name="payer_document")
    @JsonView({Views.Ticket.List.class})
    private String payerDocument;

    @Column(name = "expiration_date_time")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Ticket.List.class})
    private Date expirationDateTime;

    @Column(name = "processed_at")
    @JsonView({Views.Ticket.Detail.class})
    private Date processedAt;

    @Column(name = "value")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Ticket.List.class})
    private BigDecimal value;

    @Column(name = "payment_penalty_value")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Ticket.List.class})
    private BigDecimal paymentPenaltyValue;

    @Column(name = "interest")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Ticket.List.class})
    private BigDecimal interest;

    @Column(name = "uri")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Ticket.List.class})
    private String uri;

    @Column(name = "typing_code")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Ticket.List.class})
    private String typingCode;

    @Column(name = "\"number\"")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Ticket.List.class})
    private String number;

    @Column(name = "our_number")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Ticket.List.class})
    private String ourNumber;

    @Column(name = "create_date_time")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Ticket.List.class})
    private Date createDateTime;

    @Column(name = "occurrence_code")
    @JsonView({Views.Ticket.List.class})
    private String occurrenceCode;

    @Column(name = "source_type")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class})
    @JsonView({Views.Ticket.Detail.class})
    private TicketPaymentSource paymentSource;

    @JsonIgnore
    @Version
    private Integer version;

    public boolean fromContractor() {
        return TicketPaymentSource.CONTRACTOR_CREDIT.equals(paymentSource);
    }

    public boolean fromCreditHirer() {
        return TicketPaymentSource.HIRER_CREDIT.equals(paymentSource);
    }

    public boolean fromBillingHirer() {
        return TicketPaymentSource.HIRER_INSTALLMENT.equals(paymentSource);
    }

    public boolean fromBonusBilling() {
        return TicketPaymentSource.CONTRACTOR_BONUS.equals(paymentSource);
    }
}
