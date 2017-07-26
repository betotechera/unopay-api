package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;

import static javax.persistence.EnumType.STRING;

@Data
@Entity
@Table(name = "legal_person_detail")
public class LegalPersonDetail implements Serializable{

    public static final long serialVersionUID = 1L;

    public LegalPersonDetail(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Temporal(TemporalType.DATE)
    @Column(name="creation_date")
    @Past(groups = {Create.class,Update.class})
    private Date creation;

    @Valid
    @Enumerated(STRING)
    @Column(name="activity")
    @JsonView({Views.Public.class,Views.List.class})
    private CompanyActivity activity;

    @Valid
    @Enumerated(STRING)
    @Column(name="type")
    @JsonView({Views.Public.class,Views.List.class})
    private CompanyType type;


    @Column(name="fantasy_name")
    @JsonView({Views.Public.class,Views.List.class})
    @Size(max = 150, groups = {Create.class, Update.class})
    private String fantasyName;

    @Column(name="responsible_name")
    @JsonView({Views.Public.class})
    private String responsibleName;

    @Column(name="responsible_email")
    @JsonView({Views.Public.class})
    private String responsibleEmail;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "responsible_document_type")) ,
            @AttributeOverride(name = "number", column = @Column(name = "responsible_document_number")),
            @AttributeOverride(name = "registryEntity", column = @Column(name = "responsible_registry_entity"))
    })
    @JsonView({Views.Public.class})
    private Document responsibleDocument;

    public void update(LegalPersonDetail legalPersonDetail) {
        this.setResponsibleName(legalPersonDetail.getResponsibleName());
        this.setResponsibleEmail(legalPersonDetail.getResponsibleEmail());
        this.setResponsibleDocument(legalPersonDetail.getResponsibleDocument());
    }

    public void updateForAccreditedNetwork(LegalPersonDetail legalPersonDetail) {
        this.setResponsibleName(legalPersonDetail.getResponsibleName());
        this.setResponsibleEmail(legalPersonDetail.getResponsibleEmail());
    }

    public void updateForInstitution(LegalPersonDetail legalPersonDetail) {
        this.setResponsibleName(legalPersonDetail.getResponsibleName());
        this.setResponsibleEmail(legalPersonDetail.getResponsibleEmail());
        this.setResponsibleDocument(legalPersonDetail.getResponsibleDocument());
    }

    public void updateForIssuer(LegalPersonDetail legalPersonDetail) {
        this.responsibleName = legalPersonDetail.getResponsibleName();
        this.responsibleEmail = legalPersonDetail.getResponsibleEmail();
    }
    public void updateForHirer(LegalPersonDetail legalPersonDetail) {
        this.responsibleEmail = legalPersonDetail.getResponsibleEmail();
    }

    public void setCreation(Date dateTime){
        this.creation = ObjectUtils.clone(dateTime);
    }

    public Date getCreation(){
        return ObjectUtils.clone(this.creation);
    }
}
