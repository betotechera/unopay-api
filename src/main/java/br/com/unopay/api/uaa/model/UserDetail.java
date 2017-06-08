package br.com.unopay.api.uaa.model;

import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.Partner;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.PasswordRequired;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "oauth_user_details")
@Data
@EqualsAndHashCode(exclude = { "groups" })
public class UserDetail implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @NotNull(groups = Update.class)
    @JsonView({Views.Public.class,Views.List.class})
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
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
    @JoinColumn(name="institution_id")
    @JsonView({Views.Public.class})
    private Institution institution;

    @ManyToOne
    @JoinColumn(name="accredited_network_id")
    @JsonView({Views.Public.class})
    private AccreditedNetwork accreditedNetwork;

    @ManyToOne
    @JoinColumn(name="establishment_id")
    @JsonView({Views.Public.class})
    private Establishment establishment;

    @ManyToOne
    @JoinColumn(name="issuer_id")
    @JsonView({Views.Public.class})
    private Issuer issuer;

    @ManyToOne
    @JoinColumn(name="hirer_id")
    @JsonView({Views.Public.class})
    private Hirer hirer;

    @ManyToOne
    @JoinColumn(name="contractor_id")
    @JsonView({Views.Public.class})
    private Contractor contractor;

    @ManyToOne
    @JoinColumn(name="partner_id")
    @JsonView({Views.Public.class})
    private Partner partner;


    @JsonView(Views.Internal.class)
    @NotNull(groups = PasswordRequired.class)
    @Column(name="password")
    @Size(min=5, max = 50, groups = {PasswordRequired.class })
    private String password;

    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.EAGER)
    @JsonView({Views.Public.class,Views.List.class})
    @JoinTable(name = "oauth_group_members",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "group_id") })
    private Set<Group> groups;

    @Version
    @JsonIgnore
    Long version;

    public UserDetail() {
        //for serialization only
    }

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
                .filter(Objects::nonNull)
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
        return groups.stream().map(Group::getAuthorities)
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public void updateModel(UserDetail user) {
        if(user.getEmail() !=null)
            this.setEmail(user.getEmail());
        if(user.getName() !=null)
            this.setName(user.getName());
        if(user.getType() !=null)
            this.setType(user.getType());
    }

    @JsonIgnore
    public boolean isEstablishmentType(){
        return  establishment != null;
    }

    public String establishmentId(){
        if(isEstablishmentType()){
            return getEstablishment().getId();
        }
        return null;
    }


    public Optional<Establishment> myEstablishment() {
        if (isEstablishmentType()) {
            return Optional.ofNullable(getEstablishment());
        }
        return Optional.empty();
    }
}
