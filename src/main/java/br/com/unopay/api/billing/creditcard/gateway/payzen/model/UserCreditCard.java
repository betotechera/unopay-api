package br.com.unopay.api.billing.creditcard.gateway.payzen.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.model.UserDetail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

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
    @JsonView({Views.UserCreditCard.Detail.class})
    private String brand;

    @Column(name = "last_four_digits")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.Detail.class})
    private String lastFourDigits;

    @Column(name = "expiration_month")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.Detail.class})
    private String expirationMonth;

    @Column(name = "expiration_year")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.Detail.class})
    private String expirationYear;

    @Column(name = "gateway_source")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.Detail.class})
    private String gatewaySource;

    @Column(name = "gateway_token")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.UserCreditCard.Detail.class})
    private String gatewayToken;

    @Column(name = "created_date_time")
    @JsonView({Views.UserCreditCard.Detail.class})
    private Date createdDateTime;

    @JsonIgnore
    @Version
    private Integer version;

}
