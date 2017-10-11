package br.com.unopay.api.billing.boleto.model;


import br.com.unopay.api.billing.remittance.model.RemittancePayee;
import br.com.unopay.api.billing.remittance.model.RemittancePayer;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "boleto")
public class Boleto {

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

    @ManyToOne(cascade = CascadeType.ALL)
    @NotNull(groups = {Create.class})
    @JoinColumn(name="payee_id")
    @JsonView({Views.Contract.Detail.class})
    private RemittancePayee payee;

    @ManyToOne(cascade = CascadeType.ALL)
    @NotNull(groups = {Create.class})
    @JoinColumn(name="payer_id")
    @JsonView({Views.Contract.Detail.class})
    private RemittancePayer payer;

    @Column(name = "expiration_date_time")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Billing.List.class})
    private Date expirationDateTime;

    @Column(name = "processed_at")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Billing.List.class})
    private Date processedAt;

    @Column(name = "value")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Billing.List.class})
    private BigDecimal value;

    @Column(name = "payment_penalty_value")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Billing.List.class})
    private BigDecimal paymentPenaltyValue;


    @Column(name = "create_date_time")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Billing.List.class})
    private Date createDateTime;
}
