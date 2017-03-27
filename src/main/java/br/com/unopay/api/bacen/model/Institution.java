package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Data
@Entity
@EqualsAndHashCode
@Table(name = "institution")
public class Institution {

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @NotNull
    @ManyToOne
    @JoinColumn(name="payment_rule_group_id")
    @JsonView({Views.Public.class})
    private PaymentRuleGroup paymentRuleGroup;

    @NotNull
    @ManyToOne
    @JoinColumn(name="person_id")
    @JsonView({Views.Public.class})
    private Person person;

}
