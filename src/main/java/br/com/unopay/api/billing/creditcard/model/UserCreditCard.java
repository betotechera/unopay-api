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

import static br.com.unopay.api.uaa.exception.Errors.*;

@Data
@Entity
@Table(name = "user_credit_card")
public class UserCreditCard {

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
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.Detail.class})
    private String gatewaySource;

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
    }

    public void validateMe(){
        validateMonth();
        validateYear();
    }

    public void validateMonth(){
        if (getExpirationMonth() != null){
            int month = Integer.parseInt(getExpirationMonth());
            if (month < 1 || month > 12){
                throw UnovationExceptions.unprocessableEntity()
                        .withErrors(INVALID_MONTH.withOnlyArgument(month));
            }
            else {
                return;
            }
        }
        else {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(BLANK_MONTH_VALUE);
        }
    }

    public void validateYear(){
        if (getExpirationYear() != null){
            int year = Integer.parseInt(getExpirationYear());
            if (year < 1000 || year > 9999){
                throw UnovationExceptions.unprocessableEntity()
                        .withErrors(INVALID_YEAR.withOnlyArgument(year));
            }
            else {
                return;
            }
        }
        else {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(BLANK_YEAR_VALUE);
        }
    }

    public void defineExpirationDate(){
        this.expirationDate = DateTime.now()
                .withYear(Integer.parseInt(getExpirationYear()))
                .withMonthOfYear(Integer.parseInt(getExpirationMonth()))
                .withDayOfMonth(1)
                .withTime(0, 0, 0, 0)
                .toDate();
    }

}
