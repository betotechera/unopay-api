package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Person;
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
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;


@Data
@Entity
@Table(name = "partner")
public class Partner implements Serializable {

    public static final long serialVersionUID = 1L;

    public Partner(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Valid
    @ManyToOne
    @JoinColumn(name="person_id")
    private Person person;

    @ManyToOne
    @Valid
    @JoinColumn(name="bank_account_id")
    @JsonView({Views.BankAccount.class})
    private BankAccount bankAccount;

    public void updateModel(Partner hirer) {
        person.update(hirer.getPerson());
        if(this.bankAccount != null && hirer.getBankAccount() != null) {
            this.bankAccount.updateMe(hirer.getBankAccount());
        }
    }
}
