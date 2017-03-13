package br.com.unopay.api.uaa.model;

import br.com.unopay.api.uaa.model.valistionsgroups.Create;
import br.com.unopay.api.uaa.model.valistionsgroups.Update;
import br.com.unopay.api.uaa.model.valistionsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Version
    Long version;

    @JsonView(Views.Public.class)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "oauth_user_authorities",
            joinColumns=@JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    @Column(name="authority")
    private Set<String> authorities;


    @JsonView(Views.Public.class)
    @ManyToMany
    @JoinTable(name = "oauth_group_members", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "group_id") })
    @Column(name="authority")
    private Set<Group> groups;

    public UserDetail() {}

    public UserDetail(String id, String email, String password, Set<String> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }


    public Collection<? extends GrantedAuthority> toGrantedAuthorities() {
        if (authorities == null) {
            return Collections.emptyList();
        }
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}
