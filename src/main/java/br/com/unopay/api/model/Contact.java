package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "contact")
public class Contact  implements Serializable {

    public static final long serialVersionUID = 1L;

    public Contact(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name="name")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class, Update.class})
    private String name;

    @Column(name="mail")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class, Update.class})
    private String mail;

    @Column(name = "cell_phone")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class, Update.class})
    private String cellPhone;

    @Column(name = "phone")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class, Update.class})
    private String phone;
}
