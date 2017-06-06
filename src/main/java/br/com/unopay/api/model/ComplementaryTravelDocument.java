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
@Table(name = "complementary_travel_document")
public class ComplementaryTravelDocument  implements Serializable {

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
    private ComplementaryTravelDocumentType type;

    @Column(name="document_number")
    @JsonView({Views.Public.class,Views.List.class})
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
    @NotNull(groups = {Create.class, Update.class})
    private ReceiptSituation receiptSituation;

    @Valid
    @Enumerated(STRING)
    @Column(name="reason_receipt_situation")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private ReasonReceiptSituation reasonReceiptSituation;

    @Version
    @JsonIgnore
    private Integer version;
}
