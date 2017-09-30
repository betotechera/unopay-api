package br.com.unopay.api.billing.creditcard.model;

import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Length;

import static br.com.unopay.api.model.Person.NOT_NUMBER;

@Data
@ToString(exclude = {"number", "hash"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCard implements Serializable {

    private String hash;

    boolean cseEncrypted = false;

    @NotNull
    @Pattern(message = "invalid expiration month format", regexp = "\\d{1,2}", groups = {Create.class, Update.class})
    private String expiryMonth;

    @NotNull
    @Pattern(message = "invalid expiration year format", regexp = "\\d{4}", groups = {Create.class, Update.class})
    private String expiryYear;

    @Length(min=3, max=50)
    private String holderName;

    @CreditCardNumber(groups = {Create.class, Update.class}, ignoreNonDigitCharacters=true)
    private String number;

    @Length(min=2, max=4, groups = {Create.class, Update.class})
    private String securityCode;

    private String cardReference;

    public void normalize() {
        if(this.number != null) {
            this.number = this.number.replaceAll(NOT_NUMBER, "");
        }
    }
}
