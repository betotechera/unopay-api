package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@Entity
@EqualsAndHashCode
@Table(name = "hirer")
public class Hirer implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToOne
    @JoinColumn(name="person_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Person person;

    @Valid
    @ManyToOne
    @JoinColumn(name="bank_account_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class})
    private BankAccount bankAccount;

    @Column(name="document_email")
    @JsonView({Views.Public.class,Views.List.class})
    private String documentEmail;

    public void updateModel(Hirer hirer) {
        if(person.isLegal())
            person.update(hirer.getPerson(), (o) -> o.updateForHirer(o));
        else
            person.updatePhysical(hirer.getPerson(), (o) -> o.updateForHirer(o));

        this.documentEmail  = hirer.getDocumentEmail();
        this.bankAccount.updateMe(hirer.getBankAccount());
    }
}
