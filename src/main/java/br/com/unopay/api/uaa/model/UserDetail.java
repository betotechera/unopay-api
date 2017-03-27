package br.com.unopay.api.uaa.model;

import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.PasswordRequired;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "oauth_user_details")
@Data
@EqualsAndHashCode(exclude = { "groups" })
public class UserDetail implements Serializable {

    @Id
    @JsonView({Views.Public.class,Views.List.class})
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @NotNull(groups = Update.class)
    @Column(name="id")
    private String id;

    @NotNull(groups = Create.class)
    @Column(name="email", unique = true)
    @JsonView({Views.Public.class,Views.List.class})
    @Size(min=5, max = 50, groups = {Create.class, Update.class})
    private String email;


    @Column(name="name")
    @NotNull(groups = Create.class)
    @JsonView({Views.Public.class,Views.List.class})
    @Size(min=2, max = 50, groups = {Create.class, Update.class})
    private String name;

    @ManyToOne
    @JoinColumn(name="type")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private UserType type;

    @ManyToOne
    @JoinColumn(name="payment_rule_group_id")
    @JsonView({Views.Public.class})
    private PaymentRuleGroup paymentRuleGroup;

    @JsonView(Views.Internal.class)
    @NotNull(groups = PasswordRequired.class)
    @Column(name="password")
    @Size(min=5, max = 50, groups = {PasswordRequired.class })
    private String password;

    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.EAGER)
    @JsonView({Views.Public.class,Views.List.class})
    @JoinTable(name = "oauth_group_members", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "group_id") })
    private Set<Group> groups;

    @Version
    @JsonIgnore
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

    public List<String> getAuthoritiesNames(List<Group> groups) {
        return getAuthorities(groups).stream().map(Authority::getName).collect(Collectors.toList());
    }

    public void addToMyGroups(Group group){
        if(getGroups() == null) {
            setGroups(new HashSet<>());
        }
        getGroups().add(group);
    }

    public void addToMyGroups(List<Group> groups){
        if(groups == null) {
            return;
        }
        groups.forEach(this::addToMyGroups);
    }

    @JsonView(Views.Public.class)
    public List<Authority> getGroupsAuthorities() {
        if(groups == null) {
            return Collections.emptyList();
        }
        return groups.stream().map(Group::getAuthorities).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public void updateModel(UserDetail user) {

        if (user.getEmail() != null) {
            this.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            this.setName(user.getName());
        }
        if (user.getType() != null) {
            this.setType(user.getType());
        }

    }
}
