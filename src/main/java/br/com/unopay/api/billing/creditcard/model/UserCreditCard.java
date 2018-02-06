package br.com.unopay.api.billing.creditcard.model;

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
import java.util.Date;
import java.util.Scanner;

import static br.com.unopay.api.uaa.exception.Errors.*;
import static javax.persistence.EnumType.STRING;
import static org.joda.time.DateTimeConstants.DECEMBER;
import static org.joda.time.DateTimeConstants.JANUARY;

@Data
@Entity
@Table(name = "user_credit_card")
public class UserCreditCard {

    private static final int MIN_YEAR = 1000;
    private static final int MAX_YEAR = 9999;

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
    @JsonView({Views.UserCreditCard.Detail.class})
    private String holderName;

    @Column(name = "brand")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.List.class})
    private String brand;

    @Column(name = "last_four_digits")
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

    public void setupMyCreate(){
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
                || !isInteger(getExpirationMonth(), 10)
                || !isMonthRangeValid()) {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(INVALID_MONTH.withOnlyArgument(getExpirationMonth()));
        }
    }

    public boolean isMonthRangeValid(){
        int month = Integer.parseInt(expirationMonth);
        return month >= JANUARY && month <= DECEMBER;
    }

    public void validateYear(){
        if (getExpirationYear() == null
                || getExpirationYear() == ""
                || !isInteger(getExpirationYear(), 10)
                || !isYearRangeValid()){
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(INVALID_YEAR.withOnlyArgument(getExpirationYear()));
        }
    }

    public boolean isYearRangeValid(){
        int year = Integer.parseInt(expirationYear);
        return year >= MIN_YEAR && year <= MAX_YEAR;
    }

    public void defineExpirationDate(){
        this.expirationDate = DateTime.now()
                .withYear(Integer.parseInt(getExpirationYear()))
                .withMonthOfYear(Integer.parseInt(getExpirationMonth()))
                .withDayOfMonth(1)
                .withTime(0, 0, 0, 0)
                .toDate();
    }

    private static boolean isInteger(String s, int radix) {
        Scanner sc = new Scanner(s.trim());
        if(!sc.hasNextInt(radix)) return false;
        sc.nextInt(radix);
        return !sc.hasNext();
    }
}
