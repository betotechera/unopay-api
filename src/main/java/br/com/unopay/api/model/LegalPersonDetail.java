package br.com.unopay.api.model;

import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

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

    @Column(name="fantasy_name")
    @JsonView({Views.Public.class,Views.List.class})
    private String fantasyName;

    @Column(name="responsible_name")
    @JsonView({Views.Public.class,Views.List.class})
    private String responsibleName;

    @Column(name="responsible_email")
    @JsonView({Views.Public.class,Views.List.class})
    private String responsibleEmail;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "documentType", column = @Column(name = "responsible_document_type")) ,
            @AttributeOverride(name = "documentNumber", column = @Column(name = "responsible_document_number")),
            @AttributeOverride(name = "registryEntity", column = @Column(name = "responsible_registry_entity"))
    })
    private Document responsibleDocument;

}
