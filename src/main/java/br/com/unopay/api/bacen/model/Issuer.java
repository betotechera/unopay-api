package br.com.unopay.api.bacen.model;


import br.com.unopay.api.model.Person;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(exclude = "paymentRuleGroup")
@Table(name = "issuer")
public class Issuer {

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @ManyToOne
    @JoinColumn(name="person_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Person person;


    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.EAGER)
    @JsonView({Views.Public.class,Views.List.class})
    @JoinTable(name = "payment_rule_group_issuer", joinColumns = { @JoinColumn(name = "issuer_id") }, inverseJoinColumns = { @JoinColumn(name = "payment_rule_group_id") })
    private List<PaymentRuleGroup> paymentRuleGroup;


    @Column
    @NotNull(groups = {Create.class, Update.class})
    private Double tax;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JoinColumn(name="payment_account_id")
    @JsonView({Views.Public.class,Views.List.class})
    private BankAccount paymentAccount;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JoinColumn(name="movement_account_id")
    @JsonView({Views.Public.class,Views.List.class})
    private BankAccount movementAccount;
}
