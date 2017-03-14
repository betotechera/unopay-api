package br.com.unopay.api.uaa.model;

import br.com.unopay.api.uaa.model.valistionsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
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

    @Override
    public String toString() {
        return "Authority{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
