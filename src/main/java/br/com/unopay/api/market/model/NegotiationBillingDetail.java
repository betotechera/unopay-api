package br.com.unopay.api.market.model;

import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
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
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "negotiation_billing_detail")
public class NegotiationBillingDetail {

    public NegotiationBillingDetail(){}

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
    private Integer memberTotal;

    @Column(name = "value")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private BigDecimal value;

    @Column(name = "created_date_time")
    @JsonView({Views.NegotiationBilling.Detail.class})
    private Date createdDateTime;

    @Version
    @JsonIgnore
    private Integer version;
}
