package br.com.unopay.api.model;


import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;// NOSONAR
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "contact")
public class Contact  implements Serializable {

    public static final long serialVersionUID = 1L;

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
