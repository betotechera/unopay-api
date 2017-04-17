package br.com.unopay.api.bacen.model;

import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static java.lang.Boolean.FALSE;
import static javax.persistence.EnumType.STRING;

@Data
@Embeddable
public class Checkout implements Serializable {

    public static final long serialVersionUID = 1L;

    @Enumerated(STRING)
    @Column(name="movement_period")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class, Update.class})
    private RecurrencePeriod period;

    @Column(name="authorize_transfer")
    @JsonView({Views.Public.class})
    private Boolean authorizeTransfer = FALSE;

    @JsonView({Views.Public.class})
    @Column(name="minimum_deposit_value")
    private Double minimumDepositValue;

    @Min(0)
    @JsonView({Views.Public.class})
    @Column(name="closing_payment_days")
    private Integer closingPaymentDays;

    public Checkout(){}

    public void updateModel(Checkout checkout) {
        this.period = this.getPeriod();
        this.authorizeTransfer = checkout.getAuthorizeTransfer();
    }

    public void validate() {
        if(minimumDepositValue != null && minimumDepositValue < 0) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.INVALID_MINIMUM_DEPOSIT_VALUE);
        }
    }
}
