package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Document;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.PersonType;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.pamcary.translate.KeyBase;
import br.com.unopay.api.pamcary.translate.KeyField;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Date;
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
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;


@Data
@Entity
@KeyBase(key = "viagem.favorecido")
@Table(name = "contractor")
public class Contractor implements Serializable {

    public static final long serialVersionUID = 1L;

    public Contractor(){}

    @Id
    @Column(name="id")
    @NotNull(groups = {Reference.class})
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @JoinColumn(name="person_id")
    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    private Person person;

    @ManyToOne
    @Valid
    @JoinColumn(name="bank_account_id")
    @JsonView({Views.BankAccount.class})
    private BankAccount bankAccount;

    @Transient
    @KeyField(baseField = "documento.numero", methodResolver = "getDocumentNumber")
    private String documentNumber;

    @Transient
    @KeyField(baseField = "documento.tipo", methodResolver = "getDocumentType")
    private String documentType;

    public void updateModel(Contractor hirer) {
        person.update(hirer.getPerson());
        this.bankAccount.updateMe(hirer.getBankAccount());
    }


    public String getDocumentNumber(){
        if(getDocument() != null){
            return getDocument().getNumber();
        }
        return null;
    }

    public String getDocumentType(){
        if(getDocument() != null){
            return getDocument().getType().getCode();
        }
        return null;
    }

    private Document getDocument(){
        if(getPerson() != null && getPerson().getDocument() != null){
            return getPerson().getDocument();
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
