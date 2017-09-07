package br.com.unopay.api.billing.remittance.model;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.model.State;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import static javax.persistence.EnumType.STRING;

@Data
@Entity
@Table(name = "remittance_payee")
public class RemittancePayee implements Serializable {

    public static final long serialVersionUID = 1L;

    public RemittancePayee() {}

    public RemittancePayee(Establishment establishment, Integer payerBankCode, BigDecimal receivable) {
        this.documentNumber = establishment.documentNumber();
        this.bankCode = establishment.getBankAccount().getBacenCode();
        this.agency = establishment.getBankAccount().getAgency();
        this.agencyDigit = establishment.getBankAccount().getAgencyDigit();
        this.accountNumber = establishment.getBankAccount().getAccountNumber();
        this.accountNumberDigit = establishment.getBankAccount().getAccountNumberDigit();
        this.streetName = establishment.getPerson().getAddress().getStreetName();
        this.number = establishment.getPerson().getAddress().getNumber();
        this.complement = establishment.getPerson().getAddress().getComplement();
        this.district = establishment.getPerson().getAddress().getDistrict();
        this.city = establishment.getPerson().getAddress().getCity();
        this.state = establishment.getPerson().getAddress().getState();
        this.zipCode = establishment.getPerson().getAddress().getZipCode();
        this.payerBankCode = payerBankCode;
        this.name = establishment.getPerson().getName();
        this.receivable = receivable;
    }

    public RemittancePayee(Hirer hirer, Integer payerBankCode, BigDecimal receivable) {
        this.documentNumber = hirer.getDocumentNumber();
        this.bankCode = hirer.getBankAccount().getBacenCode();
        this.agency = hirer.getBankAccount().getAgency();
        this.agencyDigit = hirer.getBankAccount().getAgencyDigit();
        this.accountNumber = hirer.getBankAccount().getAccountNumber();
        this.accountNumberDigit = hirer.getBankAccount().getAccountNumberDigit();
        this.streetName = hirer.getPerson().getAddress().getStreetName();
        this.number = hirer.getPerson().getAddress().getNumber();
        this.complement = hirer.getPerson().getAddress().getComplement();
        this.district = hirer.getPerson().getAddress().getDistrict();
        this.city = hirer.getPerson().getAddress().getCity();
        this.state = hirer.getPerson().getAddress().getState();
        this.zipCode = hirer.getPerson().getAddress().getZipCode();
        this.payerBankCode = payerBankCode;
        this.name = hirer.getPerson().getName();
        this.receivable = receivable;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @JsonView({Views.PaymentRemittance.Payee.class})
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "bank_code")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PaymentRemittance.Payee.class})
    private Integer bankCode;

    @Column(name = "payer_bank_code")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PaymentRemittance.Payee.class})
    private Integer payerBankCode;

    @Column(name = "document_number")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PaymentRemittance.Payee.class})
    private String documentNumber;

    @Column(name = "agency")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PaymentRemittance.Payee.class})
    private String agency;

    @Column(name = "agency_digit")
    @JsonView({Views.PaymentRemittance.Payee.class})
    private String agencyDigit;

    @Column(name = "account_number")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PaymentRemittance.Payee.class})
    private String accountNumber;

    @JsonView({Views.PaymentRemittance.Payee.class})
    @Column(name = "account_number_digit")
    private String accountNumberDigit;

    @Column(name = "name")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PaymentRemittance.Payee.class})
    private String name;

    @Column(name = "zip_code")
    @JsonView({Views.PaymentRemittance.Payee.class})
    @Pattern(regexp = "\\d{8}", message = "invalid zipCode!")
    private String zipCode;

    @Column(name = "street_name")
    @JsonView({Views.PaymentRemittance.Payee.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String streetName;

    @Column(name = "street_number")
    @JsonView({Views.PaymentRemittance.Payee.class})
    @Size(max = 30, groups = {Create.class, Update.class})
    private String number;

    @Column(name = "complement")
    @JsonView({Views.PaymentRemittance.Payee.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String complement;

    @Column(name = "district")
    @JsonView({Views.PaymentRemittance.Payee.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String district;

    @Column(name = "city")
    @JsonView({Views.PaymentRemittance.Payee.class})
    @Size(max = 250, groups = {Create.class, Update.class})
    private String city;

    @Enumerated(STRING)
    @Column(name = "state")
    @JsonView({Views.PaymentRemittance.Payee.class})
    private State state;

    @Column(name = "receivable")
    @JsonView({Views.PaymentRemittance.Payee.class})
    @NotNull(groups = {Create.class, Update.class})
    @Min(value = 1, groups = {Create.class, Update.class})
    private BigDecimal receivable;

    public String agentDvFirstDigit(){
        return StringUtils.left(agencyDigit, 1);
    }

    public String agentDvLastDigit(){
        return StringUtils.right(agencyDigit, 1);
    }

    public String accountDvFirstDigit(){
        return StringUtils.left(accountNumberDigit, 1);
    }

    public String accountDvLastDigit(){
        return StringUtils.right(accountNumberDigit, 1);
    }

    public String firstZipCode(){
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d{5})(\\d{3})");
        Matcher matcher = pattern.matcher(zipCode);
        matcher.find();
        return matcher.group(1);
    }

    public String lastZipeCode(){
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d{5})(\\d{3})");
        Matcher matcher = pattern.matcher(zipCode);
        matcher.find();
        return matcher.group(2);
    }

}
