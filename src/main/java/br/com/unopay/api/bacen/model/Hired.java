package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*; // NOSONAR
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@Entity
@EqualsAndHashCode
@Table(name = "hired")
public class Hired implements Serializable {

    public static final long serialVersionUID = 1L;

    public Hired(){}

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

    public void updateModel(Hired hirer) {
        person.update(hirer.getPerson());
        this.rntrc  = hirer.getRntrc();
        this.bankAccount.updateMe(hirer.getBankAccount());
    }
}
