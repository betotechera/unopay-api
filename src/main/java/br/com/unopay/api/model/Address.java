package br.com.unopay.api.model;

import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;// NOSONAR
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.io.Serializable;

import static javax.persistence.EnumType.STRING;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "address")
public class Address implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name="zip_code")
    @JsonView({Views.Public.class})
    @Pattern(regexp = "\\d{8}", message = "invalid zipCode!")
    private String zipCode;

    @Column(name="street_name")
    @JsonView({Views.Public.class})
    @Size(min=2, max = 250, groups = {Create.class, Update.class})
    private String streetName;


    @Column(name="street_number")
    @JsonView({Views.Public.class})
    @Size(min=2, max = 250, groups = {Create.class, Update.class})
    private String number;

    @Column(name="complement")
    @JsonView({Views.Public.class})
    @Size(min=2, max = 250, groups = {Create.class, Update.class})
    private String complement;

    @Column(name="district")
    @JsonView({Views.Public.class})
    @Size(min=2, max = 250, groups = {Create.class, Update.class})
    private String district;

    @Column(name="city")
    @JsonView({Views.Public.class})
    @Size(min=2, max = 250, groups = {Create.class, Update.class})
    private String city;

    @Enumerated(STRING)
    @Column(name="state")
    @JsonView({Views.Public.class})
    private State state;

    @Column(name="latitude")
    @JsonView({Views.Public.class})
    private Double latitude;

    @Column(name="longitude")
    @JsonView({Views.Public.class})
    private Double longitude;

}
