package br.com.unopay.api.market.model;

import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "negotiation_billing_detail")
public class NegotiationBillingDetail implements Serializable{

    private static final long serialVersionUID = 4560319743100939164L;

    public NegotiationBillingDetail(){}

    public NegotiationBillingDetail(Contract contract){
        this.contract = contract;
        this.freeInstallment = Boolean.FALSE;
        this.createdDateTime = new Date();
    }

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.NegotiationBilling.Detail.class})
    @JoinColumn(name="negotiation_billing_id")
    private NegotiationBilling negotiationBilling;

    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.NegotiationBilling.Detail.class})
    @JoinColumn(name="contract_id")
    private Contract contract;

    @Column(name = "installment_value")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private BigDecimal installmentValue;

    @Column(name = "installment_value_by_member")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private BigDecimal installmentValueByMember;

    @Column(name = "free_installment")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private Boolean freeInstallment;

    @Column(name = "credit_value")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private BigDecimal creditValue;

    @Column(name = "member_credit_value")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private BigDecimal memberCreditValue;

    @Column(name = "member_total")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private Integer memberTotal = 1;

    @Column(name = "value")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private BigDecimal value;

    @Column(name = "created_date_time")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private Date createdDateTime;

    @Version
    @JsonIgnore
    private Integer version;

    public NegotiationBillingDetail defineBillingInformation(NegotiationBilling billing) {
        this.creditValue = billing.getDefaultCreditValue();
        this.memberCreditValue = billing.getDefaultMemberCreditValue();
        this.installmentValue = billing.getInstallmentValue();
        this.installmentValueByMember = billing.getInstallmentValueByMember();
        this.negotiationBilling = billing;
        this.freeInstallment = billing.withFreeInstallment();
        defineValue(billing.getBillingWithCredits());
        return this;
    }

    private NegotiationBillingDetail defineValue(Boolean billingWithCredit) {
        if(freeInstallment){
            BigDecimal membersTotalValue = memberCreditValue.multiply(new BigDecimal(this.memberTotal));
            this.value = this.creditValue.add(membersTotalValue);
            return this;
        }
        if(!billingWithCredit){
            BigDecimal membersTotalValue = installmentValueByMember.multiply(new BigDecimal(this.memberTotal));
            this.value = this.installmentValue.add(membersTotalValue);
            return this;
        }
        BigDecimal memberSum = this.memberCreditValue.add(this.installmentValueByMember);
        BigDecimal membersTotalValue = memberSum.multiply(new BigDecimal(this.memberTotal));
        this.value = this.creditValue.add(this.installmentValue).add(membersTotalValue);
        return this;
    }

    public BigDecimal creditValue(){
        BigDecimal membersTotalValue = memberCreditValue.multiply(new BigDecimal(this.memberTotal));
        return this.creditValue.add(membersTotalValue);
    }

    public String contractId() {
        return getContract() != null ? getContract().getId() : null;
    }
}
