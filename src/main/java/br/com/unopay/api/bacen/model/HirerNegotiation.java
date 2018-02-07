package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "hirer_negotiation")
public class HirerNegotiation implements Updatable{

    public HirerNegotiation(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegociation.Detail.class})
    @JoinColumn(name="hirer_id")
    private Hirer hirer;

    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegociation.Detail.class})
    @JoinColumn(name="product_id")
    private Product product;

    @Column(name = "default_credit_value")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegociation.Detail.class})
    private BigDecimal defaultCreditValue;

    @Column(name = "default_member_credit_value")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegociation.Detail.class})
    private BigDecimal defaultMemberCreditValue;

    @Column(name = "payment_day")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegociation.Detail.class})
    private Integer paymentDay;

    @Column(name = "installments")
    @NotNull(groups = {Update.class})
    @JsonView({Views.HirerNegociation.Detail.class})
    private Integer installments;

    @Column(name = "installment_value")
    @NotNull(groups = {Update.class})
    @JsonView({Views.HirerNegociation.Detail.class})
    private BigDecimal installmentValue;

    @Column(name = "installment_value_by_member")
    @NotNull(groups = {Update.class})
    @JsonView({Views.HirerNegociation.Detail.class})
    private BigDecimal installmentValueByMember;

    @Column(name = "credit_recurrence_period")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegociation.Detail.class})
    private RecurrencePeriod creditRecurrencePeriod;

    @Column(name = "auto_renewal")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegociation.Detail.class})
    private Boolean autoRenewal;

    @Column(name = "\"active\"")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegociation.Detail.class})
    private Boolean active;

    @Column(name = "free_installment_quantity")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.HirerNegociation.Detail.class})
    private Integer freeInstallmentQuantity;

    @Column(name = "created_date_time")
    @JsonView({Views.HirerNegociation.Detail.class})
    private Date createdDateTime;

    @Version
    @JsonIgnore
    private Integer version;

    public void setMeUp(){
        if(!withInstallments()){
            installments = product.getPaymentInstallments();
        }
        if(!withInstallmentValue()){
            installmentValue = product.getInstallmentValue();
        }
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
}
