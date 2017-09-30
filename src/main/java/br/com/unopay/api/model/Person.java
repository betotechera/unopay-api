package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.function.Consumer;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import static javax.persistence.EnumType.STRING;

@Data
@Entity
@Table(name = "person")
public class Person implements Serializable{

    public static final long serialVersionUID = 1L;
    public static final String NOT_NUMBER = "[^\\d]";

    public Person(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @NotNull(groups = {Create.class, Update.class})
    @Column(name="name")
    @Size(min=2, max = 150, groups = {Create.class, Update.class})
    private String name;

    @Column(name="short_name")
    @NotNull(groups = {Create.class, Update.class})
    @Size(min=2,max = 50, groups = {Create.class, Update.class})
    private String shortName;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @Enumerated(STRING)
    @Column(name="type")
    private PersonType type;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @Embedded
    private Document document;

    @Valid
    @OneToOne
    @JsonView({Views.Person.class, Views.Person.Detail.class})
    @JoinColumn(name="legal_person_detail_id")
    private LegalPersonDetail legalPersonDetail;

    @Valid
    @OneToOne
    @JsonView({Views.Person.class, Views.Person.Detail.class})
    @JoinColumn(name="physical_person_detail_id")
    private PhysicalPersonDetail physicalPersonDetail;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JsonView({Views.Address.class,Views.AddressList.class})
    @JoinColumn(name="address_id")
    private Address address;

    @Column(name="telephone")
    @JsonView({Views.Person.class, Views.Person.Detail.class})
    @Pattern(regexp = "^[-() 0-9]+$", groups = {Create.class, Update.class})
    private String telephone;

    @Column(name="cell_phone")
    @JsonView({Views.Person.class, Views.Person.Detail.class})
    @Pattern(regexp = "^[-() 0-9]+$", groups = {Create.class, Update.class})
    private String cellPhone;

    public void validate() {

        if(!this.document.getType().isValidDocumentFor(this.type)) {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(Errors.INVALID_DOCUMENT_TYPE_FOR_USER);
        }

    }

    @JsonIgnore
    public boolean isLegal() {
        return PersonType.LEGAL.equals(this.type) && this.legalPersonDetail != null;
    }

    public void updatePhysical(Person person, Consumer<PhysicalPersonDetail> consumer) {
        consumer.accept(person.getPhysicalPersonDetail());
        update(person);
    }

    public void update(Person person) {
        this.setName(person.getName());
        this.setAddress(person.getAddress());
        this.setTelephone(person.getTelephone());
        this.setCellPhone(person.getCellPhone());
    }

    public void update(Person person, Consumer<LegalPersonDetail> consumer) {
        consumer.accept(person.getLegalPersonDetail());
        update(person);
    }

    public String documentNumber(){
        return getDocument().getNumber();
    }

    @JsonIgnore
    public boolean isPhysical() {
        return PersonType.PHYSICAL.equals(this.type) && physicalPersonDetail != null;
    }

    public void normalize() {
        if(this.cellPhone != null) {
            this.cellPhone = this.cellPhone.replaceAll(NOT_NUMBER, "");
        }
        if(this.telephone != null) {
            this.telephone = this.telephone.replaceAll(NOT_NUMBER, "");
        }
        if(this.documentNumber() != null){
            String documentNumberOnly = this.documentNumber().replaceAll(NOT_NUMBER, "");
            this.getDocument().setNumber(documentNumberOnly);
        }
        if(this.getAddress() != null && this.getAddress().getZipCode() != null){
            String zipCodeNumberOnly = this.getAddress().getZipCode().replaceAll(NOT_NUMBER, "");
            this.getAddress().setZipCode(zipCodeNumberOnly);
        }
    }
}
