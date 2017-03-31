package br.com.unopay.api.bacen.model;

import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

import static javax.persistence.EnumType.STRING;

@Data
@Embeddable
public class PaymentMethod implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Enumerated(STRING)
    @Column(name="movement_period")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class, Update.class})
    private Period movementPeriod;

    @Column(name="authorize_transfer")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class, Update.class})
    private Boolean authorizeTransfer;

    @JsonView({Views.Public.class})
    @Column(name="minimum_deposit_value")
    private Double minimumDepositValue;

    @Min(0)
    @JsonView({Views.Public.class})
    @Column(name="closing_payment_days")
    private Integer closingPaymentDays;

    public void updateModel(PaymentMethod paymentMethod) {
        this.movementPeriod = paymentMethod.getMovementPeriod();
        this.authorizeTransfer = paymentMethod.getAuthorizeTransfer();
    }
}
