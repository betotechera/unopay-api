package br.com.unopay.api.bacen.model;


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

@Data
@Entity
@EqualsAndHashCode
@Table(name = "bank_account")
public class BankAccount {

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JoinColumn(name="bacen_cod")
    @JsonView({Views.Public.class,Views.List.class})
    private Bank bank;

    @Column
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String agency;

    @Column
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String accountNumber;

    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String dvAccountNumber;

    @Column
    @Enumerated(value = EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private BankAccountType type;

}
