package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.PersonType;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


@Data
@Entity
@EqualsAndHashCode
@Table(name = "contractor")
public class Contractor implements Serializable {

    public static final long serialVersionUID = 1L;

    public Contractor(){}

    @Id
    @Column(name="id")
    @JsonView({Views.Public.class,Views.List.class})
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @JoinColumn(name="person_id")
    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Person person;

    @ManyToOne
    @Valid
    @JoinColumn(name="bank_account_id")
    @JsonView({Views.Public.class,Views.List.class})
    private BankAccount bankAccount;

    @Column(name="rntrc")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String rntrc;

    public void updateModel(Contractor hirer) {
        person.update(hirer.getPerson());
        this.rntrc  = hirer.getRntrc();
        this.bankAccount.updateMe(hirer.getBankAccount());
    }


    public String getDocumentNumber(){
        if(getPerson() != null && getPerson().getDocument() != null){
            return getPerson().getDocument().getNumber();
        }
        return null;
    }

    public void setBirthDate(Date birthDate){
        if(getPerson() != null && getPerson().getPhysicalPersonDetail() != null){
            getPerson().getPhysicalPersonDetail().setBirthDate(birthDate);
        }
    }

    public Date getBirthDate(){
        if(getPerson() != null && getPerson().getPhysicalPersonDetail() != null){
            return getPerson().getPhysicalPersonDetail().getBirthDate();
        }
        return null;
    }

    public boolean physicalPerson(){
        return PersonType.PHYSICAL.equals(getPerson().getType());
    }

}
