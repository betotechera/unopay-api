package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Document;
import br.com.unopay.api.model.DocumentType;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.PersonType;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.pamcary.translate.KeyBase;
import br.com.unopay.api.pamcary.translate.KeyField;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;


@Data
@Entity
@EqualsAndHashCode
@KeyBase(key = "viagem.favorecido")
@Table(name = "contractor")
public class Contractor implements Serializable {

    public static final long serialVersionUID = 1L;

    public Contractor(){}

    @Id
    @Column(name="id")
    @NotNull(groups = {Reference.class})
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
    @JsonView({Views.Public.class,Views.List.class})
    private String rntrc;

    @Transient
    @KeyField(baseField = "documento.numero", methodResolver = "getDocumentNumber")
    private String documentNumber;

    @Transient
    @KeyField(baseField = "documento.tipo", methodResolver = "getDocumentType")
    private String documentType;

    @Column
    @Version
    @JsonIgnore
    private Integer version;

    public void validate(){
        if(person.isLegal() && this.rntrc ==null){
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(Errors.RNTRC_REQUIRED_FOR_LEGAL_PERSON);

        }

    }

    public void updateModel(Contractor hirer) {
        person.update(hirer.getPerson());
        this.rntrc  = hirer.getRntrc();
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
