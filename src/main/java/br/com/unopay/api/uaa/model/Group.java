package br.com.unopay.api.uaa.model;


import br.com.unopay.api.uaa.model.valistionsgroups.Create;
import br.com.unopay.api.uaa.model.valistionsgroups.Update;
import br.com.unopay.api.uaa.model.valistionsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "oauth_groups")
public class Group implements Serializable{

    @JsonView(Views.Public.class)
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @NotNull(groups = Update.class)
    @Column(name="id")
    private String id;

    @JsonView(Views.Public.class)
    @NotNull(groups = Create.class)
    @Column(name="group_name", unique = true)
    private String name;

    @JsonView(Views.Public.class)
    @Column(name="description")
    private String description;
}
