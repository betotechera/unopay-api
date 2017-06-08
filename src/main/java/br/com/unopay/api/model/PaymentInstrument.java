package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "payment_instrument")
public class PaymentInstrument implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;

    public PaymentInstrument(){}

    @Id
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @JsonView({Views.Public.class,Views.List.class})
    @GeneratedValue(generator="system-uuid")
    private String id;

    @Column(name = "type")
    @NotNull(groups = {Create.class, Update.class})
    @Enumerated(value = EnumType.STRING)
    private PaymentInstrumentType type;

    @Column(name = "payment_number")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String number;

    @ManyToOne
    @JoinColumn(name="product_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Product product;

    @ManyToOne
    @JoinColumn(name="contractor_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Contractor contractor;

    @Column(name = "created_date")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private Date createdDate;

    @Column(name = "expiration_date")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Date expirationDate;

    @Column(name = "password")
    @JsonView({Views.Public.class,Views.List.class})
    private String password;

    @Column(name = "situation")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    @Enumerated(value = EnumType.STRING)
    private PaymentInstrumentSituation situation;

    @Column(name = "external_number_id")
    @JsonView({Views.Public.class,Views.List.class})
    private String externalNumberId;

    @Version
    @JsonIgnore
    private Integer version;

    public void setMeUp(){
        createdDate = new Date();
    }

    @JsonIgnore
    public String getPassword(){
        return this.password;
    }

    public void validate(){
        if(createdDate != null && expirationDate != null && createdDate.after(expirationDate)){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.EXPIRATION_IS_BEFORE_CREATION);
        }
    }

    public boolean hasPassword(){
        return !StringUtils.isEmpty(password);
    }

    public String contractorId(){
        if(getContractor() != null){
            return getContractor().getId();
        }
        return null;
    }

}
