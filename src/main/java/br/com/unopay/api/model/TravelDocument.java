package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.pamcary.translate.KeyBase;
import br.com.unopay.api.pamcary.translate.KeyDate;
import br.com.unopay.api.pamcary.translate.KeyEnumField;
import br.com.unopay.api.pamcary.translate.KeyField;
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
@KeyBase(key = "viagem.documento")
@Table(name = "travel_document")
public class TravelDocument  implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;

    public TravelDocument(){}

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

    @Column(name = "quantity")
    @JsonView({Views.Public.class,Views.List.class})
    private Integer quantity;

    @Valid
    @Enumerated(STRING)
    @KeyEnumField(reverseMethodName = "getCode")
    @KeyField(baseField = "sigla", reverseField = "tipo")
    @Column(name="type")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private TravelDocumentType type;

    @Column(name="document_number")
    @KeyField(baseField = "numero")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private String documentNumber;

    @Valid
    @Enumerated(STRING)
    @Column(name="situation")
    @KeyEnumField(valueOfMethodName = "from")
    @JsonView({Views.Public.class,Views.List.class})
    private TravelDocumentSituation situation;

    @Valid
    @Enumerated(STRING)
    @Column(name = "caveat")
    @KeyEnumField
    @KeyField(baseField = "ressalva")
    @JsonView({Views.Public.class,Views.List.class})
    private DocumentCaveat caveat;

    @Column(name = "created_date_time")
    @JsonView({Views.Public.class,Views.List.class})
    @KeyField(baseField = "data")
    @KeyDate(pattern = "dd/MM/yyyy")
    private Date createdDateTime;

    @Column(name = "delivery_date_time")
    @JsonView({Views.Public.class,Views.List.class})
    private Date deliveryDateTime;

    @Column(name = "cargo_weight")
    @KeyField(baseField = "peso")
    @JsonView({Views.Public.class,Views.List.class})
    private Double cargoWeight;

    @Column(name = "damaged_items")
    @KeyField(baseField = "itensavariados")
    @JsonView({Views.Public.class,Views.List.class})
    private Integer damagedItems;

    @Column
    @Version
    @JsonIgnore
    private Integer version;

    public void markAsDelivered(){
        situation = TravelDocumentSituation.DIGITIZED;
        deliveryDateTime = new Date();
    }

    public boolean negativeDamageItems(){
        return getDamagedItems() == null || getDamagedItems() < 0;
    }
    public boolean negativeCargoWeight(){
        return getCargoWeight() == null || getCargoWeight() <= 0;
    }
}
