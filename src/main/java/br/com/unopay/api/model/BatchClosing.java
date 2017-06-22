package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.RecurrencePeriod;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "batch_closing")
public class BatchClosing implements Serializable {

    public static final long serialVersionUID = 1L;

    public BatchClosing(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="establishment_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Establishment establishment;

    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="issuer_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Issuer issuer;

    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="accredited_network_id")
    @JsonView({Views.Public.class,Views.List.class})
    private AccreditedNetwork accreditedNetwork;

    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="hirer_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Hirer hirer;

    @NotNull(groups = {Create.class})
    @JoinColumn(name="issue_invoice")
    @JsonView({Views.Public.class,Views.List.class})
    private Boolean issueInvoice;

    @Column(name = "closing_date_time")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class, Update.class})
    private Date closingDateTime;

    @Column(name = "value")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal value;

    @Column(name = "period")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class, Update.class})
    private RecurrencePeriod period;

    @Column(name = "payment_release_date_time")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class, Update.class})
    private Date paymentReleaseDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "situation")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class, Update.class})
    private BatchClosingSituation situation;

    @Column(name = "payment_date_time")
    @JsonView({Views.Public.class})
    private Date paymentDateTime;

    @Column(name = "payment_id")
    @JsonView({Views.Public.class})
    private String paymentId;

    @JsonIgnore
    @Version
    private Integer version;

}
