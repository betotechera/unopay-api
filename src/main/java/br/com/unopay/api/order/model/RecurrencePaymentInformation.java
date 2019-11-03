package br.com.unopay.api.order.model;

import br.com.unopay.api.billing.creditcard.model.CardBrand;
import br.com.unopay.api.billing.creditcard.model.GatewaySource;
import br.com.unopay.api.billing.creditcard.model.PaymentMethod;
import br.com.unopay.api.billing.creditcard.model.UserCreditCard;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.model.UserDetail;
import com.fasterxml.jackson.annotation.JsonView;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Embeddable
public class RecurrencePaymentInformation {

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_payment_method")
    @JsonView({Views.Establishment.Detail.class})
    private PaymentMethod paymentMethod;

    @Column(name = "recurrence_credit_card_token")
    @JsonView({Views.Order.Detail.class})
    private String creditCardToken;

    @Column(name = "recurrence_credit_card_month")
    @Pattern(message = "invalid expiration month format", regexp = "^(0?[1-9])|(1[0-2])")
    @JsonView({Views.Order.Detail.class})
    private String creditCardMonth;

    @Column(name = "recurrence_credit_card_year")
    @Pattern(message = "invalid expiration year format", regexp = "^(20[1-9][0-9])")
    @JsonView({Views.Order.Detail.class})
    private String creditCardYear;

    @Length(min=4, max=4)
    @Column(name = "recurrence_credit_card_last_four_digits")
    @JsonView({Views.Order.Detail.class})
    private String creditCardLastFourDigits;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_credit_card_brand")
    @JsonView({Views.Order.Detail.class})
    private CardBrand creditCardBrand;

    @Length(min=3, max=50)
    @Column(name = "recurrence_credit_card_holder_name")
    @JsonView({Views.Order.Detail.class})
    private String creditCardHolderName;


    public UserCreditCard toUserCreditCard(UserDetail user){
        UserCreditCard userCreditCard = new UserCreditCard();
        userCreditCard.setUser(user);
        userCreditCard.setHolderName(this.creditCardHolderName);
        userCreditCard.setBrand(this.creditCardBrand);
        userCreditCard.setLastFourDigits(this.creditCardLastFourDigits);
        userCreditCard.setExpirationMonth(this.creditCardMonth);
        userCreditCard.setExpirationYear(this.creditCardYear);
        userCreditCard.setGatewaySource(GatewaySource.PAYZEN);
        userCreditCard.setGatewayToken(this.creditCardToken);
        return userCreditCard;
    }

    public boolean isValid() {
        return  this.creditCardBrand != null && this.creditCardHolderName != null &&
                this.creditCardLastFourDigits != null && this.creditCardMonth != null &&
                this.creditCardYear != null && this.creditCardToken != null;
    }
}