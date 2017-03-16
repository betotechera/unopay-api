package br.com.unopay.api.uaa.model;

import br.com.unopay.api.uaa.model.valistionsgroups.Create;
import br.com.unopay.api.uaa.model.valistionsgroups.Update;
import br.com.unopay.api.uaa.model.valistionsgroups.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "oauth_user_details")
@Data
public class UserDetail implements Serializable {

    @JsonView(Views.Public.class)
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @NotNull(groups = Update.class)
    @Column(name="id")
    private String id;

    @JsonView(Views.Public.class)
    @NotNull(groups = Create.class)
    @Column(name="email", unique = true)
    private String email;

    @JsonView(Views.Internal.class)
    @NotNull(groups = Create.class)
    @Column(name="password")
    private String password;

    @JsonIgnore
    @OneToMany
    @JoinTable(name = "oauth_group_members", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "group_id") })
    private Set<Group> groups;


    @Version
    Long version;


    public UserDetail() {}

    public UserDetail(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }


    public List<SimpleGrantedAuthority> toGrantedAuthorities(List<Group> groups) {
        return getAuthoritiesNames(groups).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public List<Authority> getAuthorities(List<Group> groups) {
        if (groups == null) {
            return Collections.emptyList();
        }
        return groups.stream()
                .filter(g -> g.getAuthorities() != null)
                .map(Group::getAuthorities)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public List<String> getAuthoritiesNames(List<Group> groups) {
        return getAuthorities(groups).stream().map(Authority::getName).collect(Collectors.toList());
    }

    public void addToMyGroups(Group group){
        if( getGroups() == null) setGroups(new HashSet<>());
        getGroups().add(group);
    }
}
