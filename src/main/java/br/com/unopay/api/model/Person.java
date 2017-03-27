package br.com.unopay.api.model;

import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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

    @Column(name="name")
    @JsonView({Views.Public.class,Views.List.class})
    @Size(min=2, max = 50, groups = {Create.class, Update.class})
    private String name;

    @Enumerated(STRING)
    @Column(name="type")
    @JsonView({Views.Public.class,Views.List.class})
    private PersonType type;

    @Embedded
    private Document document;

    @OneToOne
    @JsonView({Views.Public.class})
    @JoinColumn(name="legal_person_details_id")
    private LegalPersonDetail legalPersonDetails;

    @OneToOne
    @JsonView({Views.Public.class})
    @JoinColumn(name="physical_person_details_id")
    private PhysicalPersonDetail physicalPersonDetail;

    @OneToOne
    @JsonView({Views.Public.class})
    @JoinColumn(name="address_id")
    private Address address;

    @Column(name="telephone")
    @Pattern(regexp = "^\\d{10,13}$")
    private String telephone;

}
