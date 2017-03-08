package br.com.unopay.api.uaa.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

    @Override
    public String toString() {
        return "Authority{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
