package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.viacep.model.CEP;
import com.fasterxml.jackson.annotation.JsonView;
import static javax.persistence.EnumType.STRING;
import lombok.Data;
import static org.apache.commons.lang3.EnumUtils.getEnum;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Entity
@Table(name = "address")
public class Address implements Serializable {

    public static final long serialVersionUID = 1L;

    public Address(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name="zip_code")
    @JsonView({Views.Address.class})
    @Pattern(regexp = "\\d{8}", message = "invalid zipCode!")
    private String zipCode;

    @Column(name="street_name")
    @JsonView({Views.Address.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String streetName;

    @Column(name="street_number")
    @JsonView({Views.Address.class})
    @Size(max = 30, groups = {Create.class, Update.class})
    private String number;

    @Column(name="complement")
    @JsonView({Views.Address.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String complement;

    @Column(name="district")
    @JsonView({Views.Address.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String district;

    @Column(name="city")
    @JsonView({Views.Address.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String city;

    @Enumerated(STRING)
    @Column(name="state")
    @JsonView({Views.Address.class})
    private State state;

    @Column(name="latitude")
    @JsonView({Views.Address.class})
    private Double latitude;

    @Column(name="longitude")
    @JsonView({Views.Address.class})
    private Double longitude;

    public Address(CEP cep) {
        this.zipCode = cep.unformattedCep();
        this.city = cep.getLocalidade();
        this.streetName = cep.getLogradouro();
        this.district = cep.getBairro();
        this.state = getEnum(State.class,cep.getUf());
    }

    public Address(String zipCode) {
        this.zipCode = zipCode;
    }
}
