package br.com.unopay.api.billing.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Length;

@Data
@ToString(exclude = {"number", "hash"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCard implements Serializable {

    private String hash;

    boolean cseEncrypted = false;

    @Pattern(message = "invalid expiration month format", regexp = "\\d{1,2}")
    private String expiryMonth;

    @Pattern(message = "invalid expiration year format", regexp = "\\d{4}")
    private String expiryYear;

    @Length(min=3, max=50)
    private String holderName;

    @CreditCardNumber
    private String number;

    @Length(min=2, max=4)
    private String securityCode;

    private String cardReference;
}
