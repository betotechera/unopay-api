package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.pamcary.translate.KeyBase;
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
@KeyBase(key = "viagem.documento.complementar")
@Table(name = "complementary_travel_document")
public class ComplementaryTravelDocument  implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;

    public ComplementaryTravelDocument(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name = "quantity")
    @JsonView({Views.Public.class,Views.List.class})
    private Integer quantity;

    @Valid
    @Enumerated(STRING)
    @Column(name="type")
    @JsonView({Views.Public.class,Views.List.class})
    @KeyEnumField
    @KeyField(field = "sigla")
    private ComplementaryTravelDocumentType type;

    @Column(name="document_number")
    @JsonView({Views.Public.class,Views.List.class})
    @KeyField(field = "numero")
    private String documentNumber;

    @Valid
    @Enumerated(STRING)
    @Column(name="situation")
    @KeyEnumField(valueOfMethodName = "from")
    @JsonView({Views.Public.class,Views.List.class})
    private DocumentTravelSituation situation;

    @Valid
    @Enumerated(STRING)
    @Column(name = "caveat")
    @KeyEnumField
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
    @KeyEnumField(valueOfMethodName = "from")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = { Update.class})
    private ReceiptSituation receiptSituation;

    @Valid
    @Enumerated(STRING)
    @Column(name="reason_receipt_situation")
    @KeyEnumField(valueOfMethodName = "from")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Update.class})
    private ReasonReceiptSituation reasonReceiptSituation;

    @Version
    @JsonIgnore
    private Integer version;
}
