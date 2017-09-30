package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

@Data
@Entity
@EqualsAndHashCode(of = {"id", "number"})
@Table(name = "payment_instrument")
public class PaymentInstrument implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;

    public PaymentInstrument(){}

    public PaymentInstrument(Contractor contractor, Product product){
        this.contractor = contractor;
        this.product = product;
        this.type = PaymentInstrumentType.DIGITAL_WALLET;
        this.situation = PaymentInstrumentSituation.ACTIVE;
        this.expirationDate = new DateTime().plusYears(5).withMillisOfDay(0).toDate();
    }

    @Id
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @Column(name = "type")
    @NotNull(groups = {Create.class, Update.class})
    @Enumerated(value = EnumType.STRING)
    @JsonView({Views.ContractorInstrumentCredit.List.class,Views.PaymentInstrument.List.class})
    private PaymentInstrumentType type;

    @Column(name = "payment_number")
    private String number;

    @ManyToOne
    @JoinColumn(name="product_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.ContractorInstrumentCredit.List.class, Views.PaymentInstrument.List.class})
    private Product product;

    @ManyToOne
    @JoinColumn(name="contractor_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PaymentInstrument.List.class})
    private Contractor contractor;

    @Column(name = "created_date")
    @JsonView({Views.PaymentInstrument.Detail.class})
    private Date createdDate;

    @Column(name = "expiration_date")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PaymentInstrument.Detail.class})
    private Date expirationDate;

    @Column(name = "password")
    @JsonView({Views.PaymentInstrument.Private.class})
    private String password;

    @Column(name = "situation")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.PaymentInstrument.List.class})
    @Enumerated(value = EnumType.STRING)
    private PaymentInstrumentSituation situation;

    @Column(name = "external_number_id")
    @JsonView({Views.PaymentInstrument.Detail.class})
    private String externalNumberId;

    @Column(name = "gateway_token")
    @JsonView({Views.PaymentInstrument.Detail.class})
    private String gatewayToken;

    @Version
    @JsonIgnore
    private Integer version;

    @Transient
    @JsonProperty
    private boolean resetPassword;

    @Transient
    private boolean hasPassword;


    public void setMeUp(String number){
        createdDate = new Date();
        this.number = number;
    }

    public void validate(){
        if(createdDate != null && expirationDate != null && createdDate.after(expirationDate)){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.EXPIRATION_IS_BEFORE_CREATION);
        }
    }

    @JsonProperty
    public boolean hasPassword(){
        return !StringUtils.isEmpty(password);
    }

    public String contractorId(){
        if(getContractor() != null){
            return getContractor().getId();
        }
        return null;
    }

    public void setCreatedDate(Date dateTime){
        this.createdDate = ObjectUtils.clone(dateTime);
    }

    public Date getCreatedDate(){
        return ObjectUtils.clone(this.createdDate);
    }

    public void setExpirationDate(Date dateTime){
        this.expirationDate = ObjectUtils.clone(dateTime);
    }

    public Date getExpirationDate(){
        return ObjectUtils.clone(this.expirationDate);
    }

    public String issuerBin() {
        return product.issuerBin();
    }

    public boolean is(PaymentInstrumentType type){
        if(this.type != null){
            return this.type.equals(type);
        }
        return false;
    }
}
