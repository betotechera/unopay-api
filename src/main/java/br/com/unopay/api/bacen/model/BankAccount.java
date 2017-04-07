package br.com.unopay.api.bacen.model;


import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static br.com.unopay.api.uaa.exception.Errors.*;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "bank_account")
public class BankAccount implements Serializable{

    public static final Long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JoinColumn(name="bank_bacen_code")
    @JsonView({Views.Public.class,Views.List.class})
    private Bank bank;

    @Column(name = "agency")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String agency;

    @Column(name = "agency_dv")
    @JsonView({Views.Public.class,Views.List.class})
    private String agencyDv;

    @Column(name = "account_number")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String accountNumber;

    @JsonView({Views.Public.class,Views.List.class})
    @Column(name = "account_number_dv")
    private String accountNumberDv;

    @Column(name = "account_type")
    @Enumerated(value = EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private BankAccountType type;

    public void updateMe(BankAccount other){
        setBank(other.getBank());
        setAgency(other.getAgency());
        setAgencyDv(other.getAgencyDv());
        setAccountNumber(other.getAccountNumber());
        setAccountNumberDv(other.getAccountNumberDv());
        setType(other.getType());
    }

    public void validate(){
        if(getBank() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(BANK_REQUIRED);
        }
        if(getBank().getBacenCode() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(BANK_CODE_REQUIRED);
        }
        if(getAgency() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(AGENCY_REQUIRED);
        }
        if(getAccountNumber() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(ACCOUNT_NUMBER_REQUIRED);
        }
        if(getType() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(TYPE_REQUIRED);
        }

    }

    public Integer getBacenCode(){
        if(getBank() != null && getBank().getBacenCode() != null){
            return getBank().getBacenCode();
        }
        return null;
    }

}
