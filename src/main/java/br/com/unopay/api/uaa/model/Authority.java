package br.com.unopay.api.uaa.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "authority")
public class Authority {

    @Id
    private String name;
    private String description;

    public Authority() {}

    public Authority(String description, String name) {
        this.description = description;
        this.name = name;
    }

    @ManyToMany
    @JoinTable(name = "oauth_group_authorities", joinColumns = { @JoinColumn(name = "authority") }, inverseJoinColumns = { @JoinColumn(name = "group_id") })
    private Set<Group> groups;

    @Override
    public String toString() {
        return "Authority{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
