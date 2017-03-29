package br.com.unopay.api.bacen.model;


import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "bank")
public class Bank {

    @Id
    @Column(name = "bacen_cod")
    @JsonView({Views.Public.class,Views.List.class})
    private Integer bacenCode;

    @Column
    @JsonView({Views.Public.class,Views.List.class})
    private String name;
}
