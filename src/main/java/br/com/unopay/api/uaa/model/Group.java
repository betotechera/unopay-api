package br.com.unopay.api.uaa.model;


import br.com.unopay.api.uaa.model.valistionsgroups.Create;
import br.com.unopay.api.uaa.model.valistionsgroups.Update;
import br.com.unopay.api.uaa.model.valistionsgroups.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "oauth_groups")
@EqualsAndHashCode(exclude = {"members", "authorities"})
public class Group implements Serializable{

    @Id
    @JsonView({Views.Public.class,Views.List.class})
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @NotNull(groups = Update.class)
    @Column(name="id")
    private String id;

    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = Create.class)
    @Column(name="group_name", unique = true)
    private String name;

    @JsonView({Views.Public.class,Views.List.class})
    @Column(name="description")
    private String description;

    @JsonIgnore
    @OneToMany
    @JoinTable(name = "oauth_group_members", joinColumns = { @JoinColumn(name = "group_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Set<UserDetail> members;


    @OneToMany(fetch = FetchType.EAGER)
    @BatchSize(size = 10)
    @JsonView({Views.Public.class})
    @JoinTable(name = "oauth_group_authorities", joinColumns = { @JoinColumn(name = "group_id") }, inverseJoinColumns = { @JoinColumn(name = "authority") })
    private Set<Authority> authorities;

    @Version
    @JsonIgnore
    Long version;


    public void addToMyAuthorities(Authority authority){
        if( getAuthorities() == null) setAuthorities(new HashSet<>());
        getAuthorities().add(authority);
    }

    public void addToMyMembers(UserDetail user){
        if( getMembers() == null) setMembers(new HashSet<>());
        getMembers().add(user);
    }

    public void updateModel(Group group) {
        this.name = group.getName();
        this.description = group.getDescription();
    }
}
