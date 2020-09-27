package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "payment_bank_account")
public class PaymentBankAccount implements Serializable{

    public static final long serialVersionUID = 1L;

    public PaymentBankAccount(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JoinColumn(name="bank_account_id")
    @JsonView({Views.BankAccount.class})
    private BankAccount bankAccount;

    @Column(name = "authorize_transfer")
    @JsonView({Views.BankAccount.class})
    private Boolean authorizeTransfer = Boolean.FALSE;

    @Column(name = "deposit_period")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.BankAccount.class, Views.Issuer.Detail.class})
    private RecurrencePeriod depositPeriod;

    @Min(0)
    @NotNull
    @JsonView({Views.BankAccount.class})
    @Column(name = "post_paid_payment_days")
    private Integer postPaidPaymentDays;

    @Min(0)
    @NotNull
    @Column(name = "pre_paid_payment_days")
    @JsonView({Views.BankAccount.class})
    private Integer prePaidPaymentDays;

    @Size(max = 20)
    @Column(name = "bank_agreement_number_credit")
    @JsonView({Views.BankAccount.class})
    @NotNull(groups = {Create.class, Update.class})
    private String bankAgreementNumberForCredit;

    @Size(max = 7)
    @Column(name = "bank_agreement_number_debit")
    @JsonView({Views.BankAccount.class})
    @NotNull(groups = {Create.class, Update.class})
    private String bankAgreementNumberForDebit;

    @Column(name = "station")
    @JsonView({Views.BankAccount.class})
    @Size(max = 4)
    @NotNull(groups = {Create.class, Update.class})
    private String station;

    @Size(max = 3)
    @Column(name = "wallet_number")
    @JsonView({Views.BankAccount.class})
    @NotNull(groups = {Create.class, Update.class})
    private String walletNumber;

    @JsonIgnore
    public String getBankAccountId() {
        return bankAccount.getId();
    }

    public Integer backBacenCode(){
        return 33;
    }
}
