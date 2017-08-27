package br.com.unopay.api.bacen.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(of = "bacenCode")
@Table(name = "bank")
public class Bank implements Serializable {

    public static final long serialVersionUID = 1L;
    public static final int MAX_CNAB_NAME_SIZE = 30;

    public Bank(){}

    @Id
    @Column(name = "bacen_code")
    private Integer bacenCode;

    @Column(name = "name")
    private String name;

    public String getName(){
        if(this.name.length() > MAX_CNAB_NAME_SIZE) {
            return name.substring(0, MAX_CNAB_NAME_SIZE);
        }
        return this.name;
    }
}
