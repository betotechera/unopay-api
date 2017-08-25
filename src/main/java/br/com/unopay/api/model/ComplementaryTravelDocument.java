package br.com.unopay.api.model;

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
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;

import static javax.persistence.EnumType.STRING;

@Data
@Entity
@KeyBase(key = "viagem.documento.complementar")
@Table(name = "complementary_travel_document")
public class ComplementaryTravelDocument  implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;

    public ComplementaryTravelDocument(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name = "quantity")
    private Integer quantity;

    @Valid
    @Enumerated(STRING)
    @Column(name="type")
    @KeyEnumField(reverseMethodName = "getCode")
    @KeyField(baseField = "sigla", reverseField = "tipo")
    private ComplementaryTravelDocumentType type;

    @Column(name="document_number")
    @KeyField(baseField = "numero")
    private String documentNumber;

    @Valid
    @Enumerated(STRING)
    @Column(name="situation")
    @KeyEnumField(valueOfMethodName = "from")
    private TravelDocumentSituation situation;

    @Valid
    @Enumerated(STRING)
    @Column(name = "caveat")
    @KeyEnumField
    @KeyField(baseField = "ressalva")
    private DocumentCaveat caveat;

    @Column(name = "created_date_time")
    @KeyField(baseField = "data")
    @KeyDate(pattern = "dd/MM/yyyy")
    private Date createdDateTime;

    @Column(name = "delivery_date_time")
    private Date deliveryDateTime;

    @Version
    @JsonIgnore
    private Integer version;

    public void markAsDelivered(){
        situation = TravelDocumentSituation.DIGITIZED;
        deliveryDateTime = new Date();
    }

    public void setCreatedDateTime(Date dateTime){
        this.createdDateTime = ObjectUtils.clone(dateTime);
    }

    public Date getCreatedDateTime(){
        return ObjectUtils.clone(this.createdDateTime);
    }
}
