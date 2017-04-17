package br.com.unopay.api.uaa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*; // NOSONAR
import java.io.Serializable;
import java.util.Set;

@Entity
@Data
@Table(name = "authority")
@EqualsAndHashCode(exclude = { "groups" })
public class Authority  implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    private String name;
    private String description;

    public Authority() {}

    public Authority(String description, String name) {
        this.description = description;
        this.name = name;
    }

    @JsonIgnore
    @OneToMany
    @JoinTable(name = "oauth_group_authorities",
            joinColumns = { @JoinColumn(name = "authority") },
            inverseJoinColumns = { @JoinColumn(name = "group_id") })
    private Set<Group> groups;

    @Override
    public String toString() {
        return "Authority{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
