package br.com.unopay.api.billing.creditcard.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import static br.com.unopay.api.uaa.exception.Errors.INVALID_EXPIRATION_DATE;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_MONTH;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_YEAR;
import static javax.persistence.EnumType.STRING;
import static org.joda.time.DateTimeConstants.DECEMBER;
import static org.joda.time.DateTimeConstants.JANUARY;

@Data
@Entity
@Table(name = "person_credit_card")
public class PersonCreditCard implements Serializable, Updatable {

    private static final long serialVersionUID = 5937994323928296733L;
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
    @JoinColumn(name="person_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PersonCreditCard.Detail.class})
    private Person person;

    @Column(name = "holder_name")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PersonCreditCard.List.class})
    private String holderName;

    @Column(name = "brand")
    @Enumerated(STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PersonCreditCard.List.class})
    private CardBrand brand;

    @Column(name = "last_four_digits")
    @Pattern(message = "Must have 4 digits", regexp = "\\d{4}")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PersonCreditCard.List.class})
    private String lastFourDigits;

    @Transient
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PersonCreditCard.Detail.class})
    private String expirationMonth;

    @Transient
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PersonCreditCard.Detail.class})
    private String expirationYear;

    @Column(name = "expiration_date")
    @JsonView({Views.PersonCreditCard.Detail.class})
    private Date expirationDate;

    @Column(name = "gateway_source")
    @Enumerated(STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PersonCreditCard.Detail.class})
    private GatewaySource gatewaySource;

    @Column(name = "gateway_token")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PersonCreditCard.List.class})
    private String gatewayToken;

    @Column(name = "created_date_time")
    @JsonView({Views.PersonCreditCard.List.class})
    private Date createdDateTime;

    @JsonIgnore
    @Version
    private Integer version;

    public PersonCreditCard() {}

    public PersonCreditCard(Person person, CreditCard creditCard) {
        creditCard.checkMe();
        this.person = person;
        this.holderName = creditCard.getHolderName();
        this.brand = CardBrand.fromCardNumber(creditCard.getNumber());
        this.lastFourDigits = creditCard.lastValidFourDigits();
        this.expirationMonth = creditCard.getExpiryMonth();
        this.expirationYear = creditCard.getExpiryYear();
        this.gatewaySource = GatewaySource.PAYZEN;
        this.gatewayToken = creditCard.getToken();
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
                || "".equals(getExpirationMonth())
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
                || "".equals(getExpirationYear())
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

    public PersonCreditCard defineMonthAndYearBasedOnExpirationDate(){
        defineMonthBasedOnExpirationDate();
        defineYearBasedOnExpirationDate();
        return this;
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

    public String personId(){
        if (getPerson() != null){
            return getPerson().getId();
        }
        return null;
    }

    private static boolean isNumber(String number) {
        return number.matches("\\d+");
    }
}
