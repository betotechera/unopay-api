package br.com.unopay.api.billing.creditcard.model;

import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static br.com.unopay.api.uaa.exception.Errors.*;
import static javax.persistence.EnumType.STRING;
import static org.joda.time.DateTimeConstants.DECEMBER;
import static org.joda.time.DateTimeConstants.JANUARY;

@Data
@Entity
@Table(name = "user_credit_card")
public class UserCreditCard implements Serializable, Updatable {

    private static final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
    private static final int MONTH_OFFSET = 1;
    private static final int YEAR_OFFSET = 1900;
    private static final int SURPLUS_LIMIT = 100;
    public static final String EMPTY = "";

    @Id
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @ManyToOne
    @JoinColumn(name="user_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.Detail.class})
    private UserDetail user;

    @Column(name = "holder_name")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.List.class})
    private String holderName;

    @Column(name = "brand")
    @Enumerated(STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.List.class})
    private CardBrand brand;

    @Column(name = "last_four_digits")
    @Pattern(message = "Must have 4 digits", regexp = "\\d{4}")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.List.class})
    private String lastFourDigits;

    @Transient
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.Detail.class})
    private String expirationMonth;

    @Transient
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.Detail.class})
    private String expirationYear;

    @Column(name = "expiration_date")
    @JsonView({Views.UserCreditCard.Detail.class})
    private Date expirationDate;

    @Column(name = "gateway_source")
    @Enumerated(STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.Detail.class})
    private GatewaySource gatewaySource;

    @Column(name = "gateway_token")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.Detail.class})
    private String gatewayToken;

    @Column(name = "created_date_time")
    @JsonView({Views.UserCreditCard.List.class})
    private Date createdDateTime;

    @JsonIgnore
    @Version
    private Integer version;

    public UserCreditCard() {}

    public UserCreditCard(UserDetail userDetail, CreditCard creditCard) {
        creditCard.checkMe();
        user = userDetail;
        holderName = creditCard.getHolderName();
        brand = CardBrand.fromCardNumber(creditCard.getNumber());
        lastFourDigits = creditCard.lastFourDigits();
        expirationMonth = creditCard.getExpiryMonth();
        expirationYear = creditCard.getExpiryYear();
        gatewaySource = GatewaySource.PAYZEN;
        gatewayToken = creditCard.getCardReference();
    }

    public void setupMyCreate(){
        validateMe();
        defineExpirationDate();
        setCreatedDateTime(new Date());
    }

    public void validateMe(){
        validateMonth();
        validateYear();
    }

    public void validateMonth(){
        if (getExpirationMonth() == null
                || getExpirationMonth() == ""
                || !isNumber(getExpirationMonth())
                || !MonthRangeValid()) {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(INVALID_MONTH.withOnlyArgument(getExpirationMonth()));
        }
    }

    public boolean MonthRangeValid(){
        int month = Integer.parseUnsignedInt(expirationMonth);
        return month >= JANUARY && month <= DECEMBER;
    }

    public void defineMonthBasedOnExpirationDate(){
        validateContainsExpirationDate();
        expirationMonth = String.valueOf(expirationDate.getMonth() + MONTH_OFFSET);
    }

    public void validateYear(){
        if (getExpirationYear() == null
                || getExpirationYear() == ""
                || !isNumber(getExpirationYear())
                || !YearRangeValid()){
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(INVALID_YEAR.withOnlyArgument(getExpirationYear()));
        }
    }

    public boolean YearRangeValid(){
        int year = Integer.parseUnsignedInt(expirationYear);
        return year >= CURRENT_YEAR && year <= CURRENT_YEAR + SURPLUS_LIMIT;
    }

    public void defineYearBasedOnExpirationDate(){
        validateContainsExpirationDate();
        expirationYear = String.valueOf(expirationDate.getYear() + YEAR_OFFSET);
    }

    public void defineMonthAndYearBasedOnExpirationDate(){
        defineMonthBasedOnExpirationDate();
        defineYearBasedOnExpirationDate();
    }

    public void defineExpirationDate(){
        validateMonth();
        validateYear();
        this.expirationDate = DateTime.now()
                .withYear(Integer.parseUnsignedInt(getExpirationYear()))
                .withMonthOfYear(Integer.parseUnsignedInt(getExpirationMonth()))
                .withDayOfMonth(1)
                .withTime(0, 0, 0, 0)
                .toDate();
    }

    public void validateContainsExpirationDate(){
        if (getExpirationDate() == null
                || getExpirationDate().toString() == "") {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(INVALID_EXPIRATION_DATE.withOnlyArgument(getExpirationDate()));
        }
    }

    public String userId(){
        if (getUser() != null){
            return getUser().getId();
        }
        return null;
    }

    private boolean hasContractorPersonName() {
        return getUser() != null
                && getUser().getContractor() != null
                && getUser().getContractor().getPerson() != null
                && !Objects.equals(getUser().getContractor().getPerson().getName(), EMPTY);
    }

    @JsonView({Views.UserCreditCard.List.class})
    public String contractorPersonName() {
        if (hasContractorPersonName()) {
            return getUser().getContractor().getPerson().getName();
        }
        return EMPTY;
    }

    private static boolean isNumber(String number) {
        return number.matches("\\d+");
    }
}
