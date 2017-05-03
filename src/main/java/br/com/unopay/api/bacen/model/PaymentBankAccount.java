package br.com.unopay.api.bacen.model;

import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

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
import java.io.Serializable;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "payment_bank_account")
public class PaymentBankAccount implements Serializable{

    public static final long serialVersionUID = 1L;

    public PaymentBankAccount(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @SuppressWarnings("squid:S1192")
    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JoinColumn(name="bank_account_id")
    @JsonView({Views.Public.class,Views.List.class})
    private BankAccount bankAccount;

    @Column(name = "authorize_transfer")
    @JsonView({Views.Public.class,Views.List.class})
    private Boolean authorizeTransfer = Boolean.FALSE;

    @Column(name = "deposit_period")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private RecurrencePeriod depositPeriod;

    @Min(0)
    @JsonView({Views.Public.class})
    @Column(name = "post_paid_payment_days")
    private Integer postPaidPaymentDays;

    @Min(0)
    @Column(name = "pre_paid_payment_days")
    @JsonView({Views.Public.class})
    private Integer prePaidPaymentDays;

    @JsonIgnore
    public String getBankAccountId() {
        return bankAccount.getId();
    }
}
