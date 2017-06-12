package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.pamcary.translate.KeyEnumField;
import br.com.unopay.api.pamcary.translate.KeyField;
import br.com.unopay.api.pamcary.translate.WithKeyFields;
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
@Table(name = "travel_document")
public class TravelDocument  implements Serializable {

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
    @KeyField(key = "viagem.documento.qtde")
    @JsonView({Views.Public.class,Views.List.class})
    private Integer quantity;

    @Valid
    @Enumerated(STRING)
    @KeyEnumField
    @KeyField(key = "viagem.documento.sigla")
    @Column(name="type")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private TravelDocumentType type;

    @Column(name="document_number")
    @KeyField(key = "viagem.documento.numero")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private String documentNumber;

    @Valid
    @Enumerated(STRING)
    @Column(name="situation")
    @JsonView({Views.Public.class,Views.List.class})
    private DocumentTravelSituation situation;

    @Valid
    @Enumerated(STRING)
    @Column(name = "caveat")
    @JsonView({Views.Public.class,Views.List.class})
    private DocumentCaveat caveat;

    @Column(name = "created_date_time")
    @JsonView({Views.Public.class,Views.List.class})
    private Date createdDateTime;

    @Column(name = "delivery_date_time")
    @JsonView({Views.Public.class,Views.List.class})
    private Date deliveryDateTime;

    @Valid
    @Enumerated(STRING)
    @Column(name="receipt_situation")
    @JsonView({Views.Public.class,Views.List.class})
    private ReceiptSituation receiptSituation;

    @Valid
    @Enumerated(STRING)
    @Column(name="reason_receipt_situation")
    @JsonView({Views.Public.class,Views.List.class})
    private ReasonReceiptSituation reasonReceiptSituation;

    @ManyToOne
    @JoinColumn(name="cargo_contract_id")
    private CargoContract cargoContract;

    @Version
    @JsonIgnore
    private Integer version;
}
