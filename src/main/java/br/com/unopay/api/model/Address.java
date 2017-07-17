package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.regex.Matcher;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import static javax.persistence.EnumType.STRING;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "address")
public class Address implements Serializable {

    public static final long serialVersionUID = 1L;

    public Address(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name="zip_code")
    @JsonView({Views.Public.class,Views.List.class})
    @Pattern(regexp = "\\d{8}", message = "invalid zipCode!")
    private String zipCode;

    @Column(name="street_name")
    @JsonView({Views.Public.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String streetName;

    @Column(name="street_number")
    @JsonView({Views.Public.class})
    @Size(max = 30, groups = {Create.class, Update.class})
    private String number;

    @Column(name="complement")
    @JsonView({Views.Public.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String complement;

    @Column(name="district")
    @JsonView({Views.Public.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String district;

    @Column(name="city")
    @JsonView({Views.Public.class,Views.List.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String city;

    @Enumerated(STRING)
    @Column(name="state")
    @JsonView({Views.Public.class,Views.List.class})
    private State state;

    @Column(name="latitude")
    @JsonView({Views.Public.class})
    private Double latitude;

    @Column(name="longitude")
    @JsonView({Views.Public.class})
    private Double longitude;

    public String firstZipCode(){
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d{5})(\\d{3})");
        Matcher matcher = pattern.matcher(zipCode);
        matcher.find();
        return matcher.group(1);
    }

    public String lastZipeCode(){
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d{5})(\\d{3})");
        Matcher matcher = pattern.matcher(zipCode);
        matcher.find();
        return matcher.group(2);
    }

}
