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
    @Column
    @JsonView({Views.Public.class,Views.List.class})
    private Integer bacenCod;

    @Column
    @JsonView({Views.Public.class,Views.List.class})
    private String name;
}
