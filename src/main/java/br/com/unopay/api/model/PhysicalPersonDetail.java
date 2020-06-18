package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import static javax.persistence.EnumType.STRING;

@Data
@Entity
@Table(name = "physical_person_detail")
public class PhysicalPersonDetail implements Serializable, Updatable{

    public static final long serialVersionUID = 1L;


    public PhysicalPersonDetail(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name="email")
    @JsonView({Views.Person.class, Views.Person.Detail.class})
    private String email;

    @Column(name="birth_date")
    @NotNull(groups = {Create.class, Update.class})
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    @JsonView({Views.Person.class, Views.Person.Detail.class})
    private Date birthDate;

    @Enumerated(STRING)
    @NotNull(groups = {Create.class, Update.class})
    @Column(name="gender")
    @JsonView({Views.Person.class, Views.Person.Detail.class})
    private Gender gender;

    public void updateForHirer(PhysicalPersonDetail detail) {
        this.email = detail.getEmail();
    }

    public void setBirthDate(Date dateTime){
        this.birthDate = ObjectUtils.clone(dateTime);
    }

    public Date getBirthDate(){
        return ObjectUtils.clone(this.birthDate);
    }

    public String getEmail(){
        return this.email;
    }
}
