package br.com.unopay.api.model;

import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static javax.persistence.EnumType.STRING;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "person")
public class Person {

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @Column(name="name")
    @JsonView({Views.Public.class,Views.List.class})
    @Size(min=2, max = 50, groups = {Create.class, Update.class})
    private String name;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @Enumerated(STRING)
    @Column(name="type")
    @JsonView({Views.Public.class,Views.List.class})
    private PersonType type;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @Embedded
    private Document document;

    @OneToOne
    @JsonView({Views.Public.class,Views.List.class})
    @JoinColumn(name="legal_person_detail_id")
    private LegalPersonDetail legalPersonDetail;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JsonView({Views.Public.class})
    @JoinColumn(name="address_id")
    private Address address;

    @Column(name="telephone")
    @JsonView({Views.Public.class})

    @Pattern(regexp = "^\\d{10,13}$")
    private String telephone;

    public void validate() {

        if(!this.document.getType().isValidDocumentFor(this.type))
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.INVALID_DOCUMENT_TYPE_FOR_USER);

        if(PersonType.LEGAL.equals(this.type) && this.legalPersonDetail == null)
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.LEGAL_PERSON_DETAIL_IS_REQUIRED_FOR_LEGAL_PERSON);
    }

    @JsonIgnore
    public boolean isLegal() {
        return PersonType.LEGAL.equals(this.type);
    }

    public void updateForInstitution(Person person) {
        this.setName(person.getName());
        this.legalPersonDetail.updateForInstitution(person.getLegalPersonDetail());
        this.setAddress(person.getAddress());
        this.setTelephone(person.getTelephone());
    }

    public void updateForAccreditedNetwork(Person person) {
        this.setName(person.getName());
        this.setTelephone(person.getTelephone());
        this.legalPersonDetail.updateForAccreditedNetwork(person.getLegalPersonDetail());
        this.setAddress(person.getAddress());

    }
}
