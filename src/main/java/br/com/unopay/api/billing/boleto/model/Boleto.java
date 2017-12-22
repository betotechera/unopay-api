package br.com.unopay.api.billing.boleto.model;


import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "boleto")
public class Boleto {

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Boleto.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name = "order_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Boleto.Detail.class})
    private String orderId;

    @NotNull(groups = {Create.class})
    @JoinColumn(name="issuer_document")
    @JsonView({Views.Boleto.Detail.class})
    private String issuerDocument;

    @NotNull(groups = {Create.class})
    @JoinColumn(name="client_document")
    @JsonView({Views.Boleto.Detail.class})
    private String clientDocument;

    @Column(name = "expiration_date_time")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Boleto.List.class})
    private Date expirationDateTime;

    @Column(name = "processed_at")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Boleto.Detail.class})
    private Date processedAt;

    @Column(name = "value")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Boleto.List.class})
    private BigDecimal value;

    @Column(name = "payment_penalty_value")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Boleto.List.class})
    private BigDecimal paymentPenaltyValue;

    @Column(name = "interest")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Boleto.List.class})
    private BigDecimal interest;

    @Column(name = "uri")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Boleto.List.class})
    private String uri;

    @Column(name = "typing_code")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Boleto.List.class})
    private String typingCode;

    @Column(name = "\"number\"")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Boleto.List.class})
    private String number;

    @Column(name = "our_number")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Boleto.List.class})
    private String ourNumber;

    @Column(name = "create_date_time")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Boleto.List.class})
    private Date createDateTime;

    @JsonIgnore
    @Version
    private Integer version;

}
