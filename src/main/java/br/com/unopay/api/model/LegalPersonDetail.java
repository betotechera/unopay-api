package br.com.unopay.api.model;

import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

import static javax.persistence.EnumType.STRING;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "legal_person_detail")
public class LegalPersonDetail {

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Temporal(TemporalType.DATE)
    @Column(name="creation_date")
    private Date creation;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @Enumerated(STRING)
    @Column(name="activity")
    @JsonView({Views.Public.class,Views.List.class})
    private CompanyActivity activity;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @Enumerated(STRING)
    @Column(name="type")
    @JsonView({Views.Public.class,Views.List.class})
    private CompanyType type;


    @Column(name="fantasy_name")
    @JsonView({Views.Public.class,Views.List.class})
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

    public void updateModel(LegalPersonDetail legalPersonDetail) {
        this.creation = legalPersonDetail.getCreation();
        this.activity = legalPersonDetail.getActivity();
        this.fantasyName = legalPersonDetail.getFantasyName();
        this.type = legalPersonDetail.getType();
        this.responsibleName = legalPersonDetail.getResponsibleName();
        this.responsibleEmail = legalPersonDetail.getResponsibleEmail();
        this.responsibleDocument = legalPersonDetail.getResponsibleDocument();
    }
}
