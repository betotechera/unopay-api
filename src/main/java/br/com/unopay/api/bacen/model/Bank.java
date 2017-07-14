package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "bank")
public class Bank implements Serializable {

    public static final long serialVersionUID = 1L;

    public Bank(){}

    @Id
    @Column(name = "bacen_code")
    @JsonView({Views.Public.class, Views.List.class})
    private Integer bacenCode;

    @Column(name = "name")
    @JsonView({Views.Public.class, Views.List.class})
    private String name;

    public String getName(){
        if(this.name.length() > 30) {
            return name.substring(0, 30);
        }
        return this.name;
    }
}
