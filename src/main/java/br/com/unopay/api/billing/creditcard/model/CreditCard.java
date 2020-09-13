package br.com.unopay.api.billing.creditcard.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Length;

import static br.com.unopay.api.billing.creditcard.model.CardBrand.fromCardNumber;
import static br.com.unopay.api.model.Person.NOT_NUMBER;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_CARD_REFERENCE;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_HOLDER_NAME;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_MONTH;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_NUMBER;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_YEAR;
import static org.joda.time.DateTimeConstants.DECEMBER;
import static org.joda.time.DateTimeConstants.JANUARY;

@Data
@ToString(exclude = {"securityCode", "issuerDocument"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCard implements Serializable {

    private static final int NUMBER_OF_DIGITS = 4;
    private static final int SURPLUS_LIMIT = 100;
    private static final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
    private static final int MIN_LENGTH = 4;
    private static final long serialVersionUID = -1060942228795287069L;
    public static final String EMPTY = "";

    public CreditCard() { }

    public CreditCard(String token) {
        this.token = token;
    }

    @NotNull
    @Pattern(message = "invalid expiration month format", regexp = "^(0?[1-9])|(1[0-2])", groups = {Create.class, Update.class})
    private String expiryMonth;

    @NotNull
    @Pattern(message = "invalid expiration year format", regexp = "^(20[1-9][0-9])", groups = {Create.class, Update.class})
    private String expiryYear;

    @Length(min=3, max=50)
    private String holderName;

    @CreditCardNumber(groups = {Create.class, Update.class}, ignoreNonDigitCharacters=true)
    private String number;

    @Length(min=2, max=4, groups = {Create.class, Update.class})
    private String securityCode;

    private String token;

    @NotNull(groups = Create.PersonCreditCard.class)
    private String issuerDocument;

    public void normalize() {
        if(this.number != null) {
            this.number = this.number.replaceAll(NOT_NUMBER, EMPTY);
        }
    }

    public String lastValidFourDigits() {
        checkNumber();
        return number.substring(number.length() - NUMBER_OF_DIGITS);
    }

    public void checkMe() {
        checkExpiryMonth();
        checkExpiryYear();
        checkNumber();
        checkCardReference();
        checkHolderName();
    }

    public void checkExpiryMonth() {
        if (getExpiryMonth() == null
                || Objects.equals(getExpiryMonth(), EMPTY)
                || !isNumber(getExpiryMonth())
                || !MonthRangeValid()) {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(INVALID_MONTH.withOnlyArgument(getExpiryMonth()));
        }
    }

    public boolean MonthRangeValid(){
        int month = Integer.parseUnsignedInt(expiryMonth);
        return month >= JANUARY && month <= DECEMBER;
    }

    public void checkExpiryYear(){
        if (getExpiryYear() == null
                || Objects.equals(getExpiryYear(), EMPTY)
                || !isNumber(getExpiryYear())
                || !YearRangeValid()){
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(INVALID_YEAR.withOnlyArgument(getExpiryYear()));
        }
    }

    public boolean YearRangeValid(){
        int year = Integer.parseUnsignedInt(expiryYear);
        return year >= CURRENT_YEAR && year <= CURRENT_YEAR + SURPLUS_LIMIT;
    }

    public void checkNumber() {
        normalize();
        if (getNumber() == null
                || Objects.equals(getNumber(), EMPTY)
                || !isNumber(getNumber())
                || getNumber().length() < MIN_LENGTH) {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(INVALID_NUMBER.withOnlyArgument(getNumber()));
        }
    }

    public void checkCardReference() {
        if (getToken() == null
                || Objects.equals(getToken(), EMPTY)) {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(INVALID_CARD_REFERENCE.withOnlyArgument(getToken()));
        }
    }

    public void checkHolderName() {
        if (getHolderName() == null
                || Objects.equals(getHolderName(), EMPTY)) {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(INVALID_HOLDER_NAME.withOnlyArgument(getHolderName()));
        }
    }

    @JsonIgnore
    public CardBrand getCardBrand() {
        normalize();
        return fromCardNumber(getNumber());
    }

    private static boolean isNumber(String number) {
        return number.matches("\\d+");
    }

}
