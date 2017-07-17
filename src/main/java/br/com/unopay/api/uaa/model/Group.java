package br.com.unopay.api.uaa.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
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
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.GROUP_NAME_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.LARGE_GROUP_DESCRIPTION;
import static br.com.unopay.api.uaa.exception.Errors.LARGE_GROUP_NAME;
import static br.com.unopay.api.uaa.exception.Errors.SHORT_GROUP_NAME;
import static br.com.unopay.api.uaa.exception.Errors.USER_TYPE_REQUIRED;

@Entity
@Data
@Table(name = "oauth_groups")
@EqualsAndHashCode(exclude = {"members", "authorities", "userType"})
public class Group implements Serializable {

    public static final long serialVersionUID = 1L;

    public static final int MAX_GROUP_NAME = 50;
    public static final int MAX_GROUP_DESCRIPTION = 250;
    public static final int MIN_GROUP_NAME = 3;

    public Group(){}

    @Id
    @JsonView({Views.Public.class,Views.List.class,Views.GroupUserType.class})
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @NotNull(groups = Update.class)
    @Column(name="id")
    private String id;

    @JsonView({Views.Public.class,Views.List.class,Views.GroupUserType.class})
    @NotNull(groups = Create.class)
    @Column(name="group_name", unique = true)
    private String name;

    @JsonView({Views.Public.class,Views.List.class,Views.GroupUserType.class})
    @Column(name="description")
    private String description;

    @JsonIgnore
    @OneToMany
    @JoinTable(name = "oauth_group_members",
            joinColumns = { @JoinColumn(name = "group_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Set<UserDetail> members;


    @OneToMany(fetch = FetchType.EAGER)
    @BatchSize(size = 10)
    @JsonView({Views.Public.class})
    @JoinTable(name = "oauth_group_authorities",
            joinColumns = { @JoinColumn(name = "group_id") },
            inverseJoinColumns = { @JoinColumn(name = "authority") })
    private Set<Authority> authorities;

    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    @ManyToOne
    @JoinColumn(name="user_type")
    private UserType userType;

    @Version
    @JsonIgnore
    Long version;


    public void addToMyAuthorities(Authority authority){
        if(getAuthorities() == null) {
            setAuthorities(new HashSet<>());
        }
        getAuthorities().add(authority);
    }

    public void addToMyMembers(UserDetail user){
        if(getMembers() == null) {
            setMembers(new HashSet<>());
        }
        getMembers().add(user);
    }

    public void updateModel(Group group) {
        this.name = group.getName();
        this.description = group.getDescription();
        this.userType = group.getUserType();
    }

    public void validate(){
        if (getName() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(GROUP_NAME_REQUIRED);
        }

        if (getName().length() > MAX_GROUP_NAME) {
            throw UnovationExceptions.unprocessableEntity().withErrors(LARGE_GROUP_NAME);
        }

        if (getDescription() != null && getDescription().length() > MAX_GROUP_DESCRIPTION) {
            throw UnovationExceptions.unprocessableEntity().withErrors(LARGE_GROUP_DESCRIPTION);
        }

        if(getName().length() < MIN_GROUP_NAME) {
            throw UnovationExceptions.unprocessableEntity().withErrors(SHORT_GROUP_NAME);
        }

        if(getUserType() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(USER_TYPE_REQUIRED);
        }
    }
}
