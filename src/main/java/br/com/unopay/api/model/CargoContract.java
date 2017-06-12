package br.com.unopay.api.model;


import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.pamcary.translate.KeyBase;
import br.com.unopay.api.pamcary.translate.KeyEnumField;
import br.com.unopay.api.pamcary.translate.KeyField;
import br.com.unopay.api.pamcary.translate.KeyFieldListReference;
import br.com.unopay.api.pamcary.translate.KeyFieldReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import static javax.persistence.EnumType.STRING;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@KeyBase(key = "viagem")
@Table(name = "cargo_contract")
public class CargoContract implements Serializable, Updatable {

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
    @NotNull(groups = {Update.class})
    private Contract contract;

    @Valid
    @Enumerated(STRING)
    @KeyEnumField
    @KeyField(field = "indicador.ressalva")
    @Column(name = "caveat")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private DocumentCaveat caveat;

    @Valid
    @Enumerated(STRING)
    @KeyField(field = "carga.perfil.id")
    @KeyEnumField(valueOfMethodName = "from")
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
    @NotNull(groups = {Update.class})
    private ReceiptStep receiptStep;

    @Valid
    @Enumerated(STRING)
    @Column(name="payment_source")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Update.class})
    private PaymentSource paymentSource;

    @Valid
    @Enumerated(STRING)
    @Column(name="travel_situation")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Update.class})
    private TravelSituation travelSituation;


    @Column(name = "created_date_time")
    @JsonView({Views.Public.class,Views.List.class})
    private Date createdDateTime;

    @KeyField(field = "id")
    @Column(name = "partner_id")
    private String partnerId;

    @KeyFieldListReference(listType = TravelDocument.class)
    @OneToMany(mappedBy="cargoContract")
    List<TravelDocument> travelDocuments;

    @KeyFieldListReference(listType = ComplementaryTravelDocument.class)
    @OneToMany(mappedBy="cargoContract")
    List<ComplementaryTravelDocument> complementaryTravelDocuments;

    @Version
    @JsonIgnore
    private Integer version;

    public void setMeUp(){
        createdDateTime = new Date();
        if(travelDocuments != null){
            travelDocuments.forEach(d -> {
                        d.setCreatedDateTime(new Date());
                        d.setCargoContract(this);
                    }
            );
        }
        if(complementaryTravelDocuments != null){
            complementaryTravelDocuments.forEach(d -> {
                        d.setCreatedDateTime(new Date());
                        d.setCargoContract(this);
                    }
            );
        }
    }

    public void markDocumentsAsDelivered(){
        if(travelDocuments != null){
            travelDocuments.forEach(d -> d.setDeliveryDateTime(new Date()));
        }
        if(complementaryTravelDocuments != null){
            complementaryTravelDocuments.forEach(d -> d.setDeliveryDateTime(new Date()));
        }
    }
}
