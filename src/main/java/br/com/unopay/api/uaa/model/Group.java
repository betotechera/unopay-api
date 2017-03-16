package br.com.unopay.api.uaa.model;


import br.com.unopay.api.uaa.model.valistionsgroups.Create;
import br.com.unopay.api.uaa.model.valistionsgroups.Update;
import br.com.unopay.api.uaa.model.valistionsgroups.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
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

    @JsonIgnore
    @OneToMany
    @JoinTable(name = "oauth_group_members", joinColumns = { @JoinColumn(name = "group_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Set<UserDetail> members;


    @JsonIgnore
    @OneToMany
    @JoinTable(name = "oauth_group_authorities", joinColumns = { @JoinColumn(name = "group_id") }, inverseJoinColumns = { @JoinColumn(name = "authority") })
    private Set<Authority> authorities;

    public void addToMyAuthorities(Authority authority){
        if( getAuthorities() == null) setAuthorities(new HashSet<>());
        getAuthorities().add(authority);
    }

    public void addToMyMembers(UserDetail user){
        if( getMembers() == null) setMembers(new HashSet<>());
        getMembers().add(user);
    }

}
