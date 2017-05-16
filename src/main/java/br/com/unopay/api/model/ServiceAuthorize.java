package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Data
@Entity
@Table(name = "service_authorize")
public class ServiceAuthorize implements Serializable {

    public static final long serialVersionUID = 1L;

    public ServiceAuthorize(){}

    @Id
    @Column(name="id")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @Column(name = "authorization_number")
    private Long authorizationNumber;

    @Column(name = "authorization_date_time")
    private Date authorizationDateTime;

    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="establishment_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Establishment establishment;

    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="contract_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Contract contract;

    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="contractor_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Contractor contractor;

    @Column(name = "service_type")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class,Views.List.class})
    private ServiceType serviceType;

    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="event_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Event event;

    @Column(name = "event_quantity")
    private Integer eventQuantity;

    @Column(name = "event_value")
    private BigDecimal eventValue;

    @Column(name = "value_fee")
    private BigDecimal valueFee;

    @Column(name = "solicitation_date_time")
    private Date solicitationDateTime;

    @Column(name = "credit_insertion_type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class,Views.List.class})
    private CreditInsertionType creditInsertionType;

    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="contractor_instrument_credit_id")
    @JsonView({Views.Public.class,Views.List.class})
    private ContractorInstrumentCredit contractorInstrumentCredit;

    @Column(name = "last_instrument_credit_balance")
    private BigDecimal lastInstrumentCreditBalance;

    @Column(name = "current_instrument_credit_balance")
    private BigDecimal currentInstrumentCreditBalance;

    @Column(name = "cancellation_date_time")
    private Date cancellationDateTime;

    @Column(name = "transaction_log_code")
    private Integer transactionLogCode;

    @Column(name = "transaction_log")
    private String transactionLog;

    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="user_id")
    @JsonView({Views.Public.class,Views.List.class})
    private UserDetail user;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class,Views.List.class})
    private TransactionSituation situation;

    @Version
    @JsonIgnore
    private Integer version;


}
