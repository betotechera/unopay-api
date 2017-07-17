package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
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

import static br.com.unopay.api.model.validation.group.Views.List;
import static br.com.unopay.api.model.validation.group.Views.Public;
import static javax.persistence.EnumType.STRING;

@Data
@Entity
@Table(name = "person")
public class Person implements Serializable{

    public static final long serialVersionUID = 1L;

    public Person(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Public.class,List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @NotNull(groups = {Create.class, Update.class})
    @Column(name="name")
    @Size(min=2, max = 150, groups = {Create.class, Update.class})
    private String name;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @Enumerated(STRING)
    @Column(name="type")
    @JsonView({Public.class,List.class})
    private PersonType type;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @Embedded
    @JsonView({Public.class,List.class})
    private Document document;

    @OneToOne
    @JsonView({Public.class,List.class})
    @JoinColumn(name="legal_person_detail_id")
    private LegalPersonDetail legalPersonDetail;

    @OneToOne
    @JsonView({Public.class,List.class})
    @JoinColumn(name="physical_person_detail_id")
    private PhysicalPersonDetail physicalPersonDetail;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JsonView({Public.class,List.class})
    @JoinColumn(name="address_id")
    private Address address;

    @Column(name="telephone")
    @JsonView({Public.class})
    @Pattern(regexp = "^\\d{10,13}$")
    private String telephone;

    @Column(name="cell_phone")
    @JsonView({Public.class})
    @Pattern(regexp = "^\\d{10,13}$")
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

    @JsonIgnore
    public boolean isPhysical() {
        return PersonType.PHYSICAL.equals(this.type) && physicalPersonDetail != null;
    }
}
