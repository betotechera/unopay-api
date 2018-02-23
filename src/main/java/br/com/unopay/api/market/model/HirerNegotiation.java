package br.com.unopay.api.market.model;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@EqualsAndHashCode(exclude = {"billings", "product"})
@ToString(exclude = {"billings", "product"})
@Table(name = "hirer_negotiation")
public class HirerNegotiation implements Updatable, Serializable{

    private static final long serialVersionUID = 3824002733097296428L;

    public HirerNegotiation(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegotiation.List.class})
    @JoinColumn(name="hirer_id")
    private Hirer hirer;

    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegotiation.List.class})
    @JoinColumn(name="product_id")
    private Product product;

    @Column(name = "default_credit_value")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegotiation.Detail.class})
    private BigDecimal defaultCreditValue;

    @Column(name = "default_member_credit_value")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegotiation.Detail.class})
    private BigDecimal defaultMemberCreditValue;

    @Column(name = "payment_day")
    @NotNull(groups = {Create.class, Update.class})
    @Max(value = 28)
    @JsonView({Views.HirerNegotiation.Detail.class})
    private Integer paymentDay;

    @Column(name = "installments")
    @NotNull(groups = {Update.class})
    @JsonView({Views.HirerNegotiation.Detail.class})
    private Integer installments;

    @Column(name = "installment_value")
    @NotNull(groups = {Update.class})
    @JsonView({Views.HirerNegotiation.Detail.class})
    private BigDecimal installmentValue;

    @Column(name = "installment_value_by_member")
    @NotNull(groups = {Update.class})
    @JsonView({Views.HirerNegotiation.Detail.class})
    private BigDecimal installmentValueByMember;

    @Column(name = "auto_renewal")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegotiation.Detail.class})
    private Boolean autoRenewal;

    @Column(name = "\"active\"")
    @NotNull(groups = {Update.class})
    @JsonView({Views.HirerNegotiation.Detail.class})
    private Boolean active;

    @Column(name = "free_installment_quantity")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegotiation.Detail.class})
    private Integer freeInstallmentQuantity;

    @Column(name = "effective_date")
    @Temporal(TemporalType.DATE)
    @JsonView({Views.HirerNegotiation.List.class})
    private Date effectiveDate;

    @Column(name = "billing_with_credits")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegotiation.Detail.class})
    private Boolean billingWithCredits;

    @Column(name = "created_date_time")
    @JsonView({Views.HirerNegotiation.List.class})
    private Date createdDateTime;

    @JsonIgnore
    @OneToMany(mappedBy = "hirerNegotiation")
    private Set<NegotiationBilling> billings;

    @Version
    @JsonIgnore
    private Integer version;

    public void validateForCreate(){
        if(effectiveDate == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.EFFECTIVE_DATE_REQUIRED);
        }
        if(effectiveDate.before(new Date())){
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(Errors.EFFECTIVE_DATE_IS_BEFORE_CREATION.withOnlyArgument(effectiveDate));
        }
    }

    public void validateForUpdate(){
        if(effectiveDate != null && effectiveDate.before(new Date())){
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(Errors.EFFECTIVE_DATE_IS_BEFORE_CREATION.withOnlyArgument(effectiveDate));
        }
    }

    public void setMeUp(){
        if(!withInstallments()){
            this.installments = product.getPaymentInstallments();
        }
        if(!withInstallmentValue()){
            this.installmentValue = product.getInstallmentValue();
        }
        this.createdDateTime = new Date();
        this.active = Boolean.FALSE;
    }

    public String productId(){
        if(getProduct() != null){
            return getProduct().getId();
        }
        return null;
    }

    public String hirerId(){
        if(getHirer() != null){
            return getHirer().getId();
        }
        return null;
    }

    private boolean withInstallmentValue() {
        return installmentValue != null;
    }


    public boolean withInstallments() {
        return installments != null;
    }

    public boolean withFreeInstallments() {
        return freeInstallmentQuantity != null && freeInstallmentQuantity != 0;
    }
}
