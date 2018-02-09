package br.com.unopay.api.market.model;

import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.order.model.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.model.ContractInstallment.ONE_INSTALLMENT;

@Data
@Entity
@Table(name = "negotiation_billing")
public class NegotiationBilling {

    public NegotiationBilling(){}

    public NegotiationBilling(HirerNegotiation negotiation, Integer installmentNumber){
        this.billingWithCredits = negotiation.getBillingWithCredits();
        this.defaultCreditValue = negotiation.getDefaultCreditValue();
        this.defaultMemberCreditValue = negotiation.getDefaultMemberCreditValue();
        this.freeInstallmentQuantity = negotiation.getFreeInstallmentQuantity();
        this.installments = negotiation.getInstallments();
        this.installmentValueByMember = negotiation.getInstallmentValueByMember();
        this.hirerNegotiation = negotiation;
        this.installmentValue = negotiation.getInstallmentValue();
        this.status = PaymentStatus.WAITING_PAYMENT;
        this.billingWithCredits = Boolean.TRUE;
        this.installmentNumber = installmentNumber;
        this.createdDateTime = new Date();
    }

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToOne
    @JsonView({Views.NegotiationBilling.Detail.class})
    @JoinColumn(name="hirer_negotiation_id")
    private HirerNegotiation hirerNegotiation;

    @Column(name = "installment_number")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private Integer installmentNumber;

    @Column(name = "installment_expiration")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private Date installmentExpiration;

    @Column(name = "installments")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private Integer installments;

    @Column(name = "installment_value")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private BigDecimal installmentValue;

    @Column(name = "installment_value_by_member")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private BigDecimal installmentValueByMember;

    @Column(name = "free_installment_quantity")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private Integer freeInstallmentQuantity;

    @Column(name = "default_credit_value")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private BigDecimal defaultCreditValue;

    @Column(name = "default_member_credit_value")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private BigDecimal defaultMemberCreditValue;

    @Column(name = "billing_with_credits")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private Boolean billingWithCredits;

    @Column(name = "value")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private BigDecimal value = BigDecimal.ZERO;

    @Column(name = "status")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private PaymentStatus status;

    @Column(name = "created_date_time")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private Date createdDateTime;

    @Version
    @JsonIgnore
    private Integer version;

    public void addValue(BigDecimal value) {
        this.value = this.value.add(value);
    }

    public Integer nextInstallmentNumber(){
        return this.installmentNumber + ONE_INSTALLMENT;
    }

    public Boolean withFreeInstallment() {
        return this.installmentNumber <= this.freeInstallmentQuantity;
    }
}
