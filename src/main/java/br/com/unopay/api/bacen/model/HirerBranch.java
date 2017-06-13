package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;


@Data
@Entity
@EqualsAndHashCode
@Table(name = "hirer_branch")
public class HirerBranch implements Serializable {

    public static final long serialVersionUID = 1L;

    public HirerBranch(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @JsonView({Views.Public.class,Views.List.class})
    private String id;

    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JoinColumn(name="person_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Person person;

    @ManyToOne
    @JoinColumn(name="head_office_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class})
    private Hirer headOffice;

    @Valid
    @JoinColumn(name="bank_account_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class})
    @ManyToOne
    private BankAccount bankAccount;

    @Column(name="document_email")
    @JsonView({Views.Public.class,Views.List.class})
    private String documentEmail;

    public void updateMe(HirerBranch hirer) {
        person.update(hirer.getPerson(), (o) -> o.updateForHirer(o));
        this.documentEmail  = hirer.getDocumentEmail();
        this.bankAccount.updateMe(hirer.getBankAccount());
    }
}
