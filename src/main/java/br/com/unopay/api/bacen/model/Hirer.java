package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;


@Data
@Entity
@Table(name = "hirer")
public class Hirer implements Serializable {

    public static final long serialVersionUID = 1L;

    public Hirer(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Valid
    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JoinColumn(name="person_id")
    private Person person;

    @Valid
    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.BankAccount.class})
    @JoinColumn(name="bank_account_id")
    private BankAccount bankAccount;

    @Column(name="document_email")
    private String documentEmail;

    @Column(name = "financier_mail")
    @NotNull(groups = {Create.class, Update.class})
    private String financierMail;

    @Column(name = "credit_recurrence_period")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    private RecurrencePeriod creditRecurrencePeriod;

    public void updateModel(Hirer hirer) {
        if(person.isLegal()) {
            person.update(hirer.getPerson(), (o) -> o.updateForHirer(o));
        }
        else {
            person.updatePhysical(hirer.getPerson(), (o) -> o.updateForHirer(o));
        }

        this.documentEmail  = hirer.getDocumentEmail();
        this.bankAccount.updateMe(hirer.getBankAccount());
    }

    @JsonIgnore
    public String getDocumentNumber() {
        if(getPerson() != null && getPerson().getDocument() != null) {
            return person.getDocument().getNumber();
        }
        return null;
    }
}
