package br.com.unopay.api.model;


import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import static javax.persistence.EnumType.STRING;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "cargo_contract")
public class CargoContract implements Serializable {

    public static final long serialVersionUID = 1L;

    public CargoContract(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToOne
    @JoinColumn(name="contract_id")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private Contract contract;

    @Valid
    @Enumerated(STRING)
    @Column(name = "caveat")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private DocumentCaveat caveat;

    @Valid
    @Enumerated(STRING)
    @Column(name = "cargo_profile")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private CargoProfile cargoProfile;

    @Column(name = "receipt_observation")
    @JsonView({Views.Public.class,Views.List.class})
    private String receiptObservation;

    @Column(name = "cargo_weight")
    @JsonView({Views.Public.class,Views.List.class})
    private Double cargoWeight;

    @Column(name = "damaged_items")
    @JsonView({Views.Public.class,Views.List.class})
    private Double damagedItems;

    @Valid
    @Enumerated(STRING)
    @Column(name = "receipt_step")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private ReceiptStep receiptStep;

    @Valid
    @Enumerated(STRING)
    @Column(name="payment_source")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private PaymentSource paymentSource;

    @Valid
    @Enumerated(STRING)
    @Column(name="travel_situation")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private TravelSituation travelSituation;


    @Column(name = "created_date_time")
    @JsonView({Views.Public.class,Views.List.class})
    private Date createdDateTime;

    @Version
    @JsonIgnore
    private Integer version;
}
