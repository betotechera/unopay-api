package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;


@Data
@Entity
@Table(name = "partner")
@ToString(exclude = {"products"})
@EqualsAndHashCode(exclude = {"products"})
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

    @ManyToMany(fetch = FetchType.EAGER)
    @BatchSize(size = 10)
    @JsonView({Views.Partner.Detail.class})
    @JoinTable(name = "partner_product",
            joinColumns = { @JoinColumn(name = "partner_id") },
            inverseJoinColumns = { @JoinColumn(name = "product_id") })
    private Set<Product> products;


    public void updateModel(Partner partner) {
        person.update(partner.getPerson());
        if(this.bankAccount != null && partner.getBankAccount() != null) {
            this.bankAccount.updateMe(partner.getBankAccount());
        }
        if(partner.hasProducts()){
            this.products = partner.getProducts();
        }
    }

    public boolean hasProducts() {
        return this.products != null && !this.products.isEmpty();
    }

    public String documentNumber(){
        if(getPerson() != null && getPerson().getDocument() != null){
            return getPerson().getDocument().getNumber();
        }
        return null;
    }
}
