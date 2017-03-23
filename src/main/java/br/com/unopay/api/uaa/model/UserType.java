package br.com.unopay.api.uaa.model;


import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_type")
@Data
@EqualsAndHashCode()
public class UserType {

    @Id
    @JsonView({Views.Public.class,Views.List.class})
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @NotNull(groups = Update.class)
    @Column(name="id")
    private String id;

    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = Create.class)
    @Column(name="name", unique = true)
    private String name;

    @JsonView({Views.Public.class,Views.List.class})
    @Column(name="description")
    private String description;
}
