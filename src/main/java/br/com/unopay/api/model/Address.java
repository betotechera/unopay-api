package br.com.unopay.api.model;

import br.com.unopay.api.address.model.AddressSearch;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import static javax.persistence.EnumType.STRING;
import static org.apache.commons.lang3.EnumUtils.getEnum;

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
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Address.class})
    @Pattern(regexp = "\\d{8}", message = "invalid zipCode!")
    private String zipCode;

    @Column(name="street_name")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Address.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String streetName;

    @Column(name="street_number")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Address.class})
    @Size(max = 30, groups = {Create.class, Update.class})
    private String number;

    @Column(name="complement")
    @JsonView({Views.Address.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String complement;

    @Column(name="district")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Address.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String district;

    @Column(name="city")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Address.class,Views.AddressList.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String city;

    @Enumerated(STRING)
    @Column(name="state")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Address.class,Views.AddressList.class})
    private State state;

    @Column(name="latitude")
    @JsonView({Views.Address.class})
    private Double latitude;

    @Column(name="longitude")
    @JsonView({Views.Address.class})
    private Double longitude;

    public Address(AddressSearch addressSearch) {
        this.zipCode = addressSearch.getCep();
        this.city = addressSearch.getCidade();
        this.streetName = addressSearch.getLogradouro();
        this.district = addressSearch.getBairro();
        this.state = getEnum(State.class, addressSearch.getEstado());
    }

    public Address(String zipCode) {
        this.zipCode = zipCode;
    }
}
