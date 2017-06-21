package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "physical_person_detail")
public class PhysicalPersonDetail implements Serializable{

    public static final long serialVersionUID = 1L;


    public PhysicalPersonDetail(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name="email")
    @JsonView({Views.Public.class,Views.List.class})
    private String email;

    @Column(name="birth_date")
    @JsonView({Views.Public.class,Views.List.class})
    private Date birthDate;

    @Column
    @Version
    @JsonIgnore
    private Integer version;

    public void updateForHirer(PhysicalPersonDetail detail) {
        this.email = detail.getEmail();
    }
}
