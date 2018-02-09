package br.com.unopay.api.model;

import br.com.unopay.api.market.model.HirerNegotiation;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.util.Time;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

@Data
@Entity
@ToString(exclude = "contract")
@EqualsAndHashCode(of = { "id", "expiration"})
@Table(name = "contract_installment")
public class ContractInstallment implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;
    public static final int ONE_INSTALLMENT = 1;
    public static final int END_OF_FEBRUARY = 28;

    public ContractInstallment(){}

    public ContractInstallment(Contract contract, Date currentDate) {
        this.value = contract.getAnnuity()
                .divide(new BigDecimal(contract.getPaymentInstallments()), 2, RoundingMode.HALF_UP);
        this.currentDate = currentDate;
        this.installmentNumber = ONE_INSTALLMENT;
        this.contract = contract;
        defineExpiration(contract);
    }

    public ContractInstallment(Contract contract, HirerNegotiation negotiation, Date currentDate) {
        this.currentDate = currentDate;
        defineValue(negotiation);
        this.installmentNumber = ONE_INSTALLMENT;
        this.contract = contract;
        defineExpirationForNegotiation(negotiation, currentDate);
    }

    @Id
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="contract_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Contract.Installment.class})
    private Contract contract;

    @Column(name = "installment_number")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Contract.Installment.class})
    private int installmentNumber;

    @Column(name = "value")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Contract.Installment.class})
    private BigDecimal value;

    @Column(name = "expiration")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Contract.Installment.class})
    private Date expiration;

    @Column(name = "payment_date_time")
    @JsonView({Views.Contract.Installment.class})
    private Date paymentDateTime;

    @Column(name = "payment_value")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Contract.Installment.class})
    private BigDecimal paymentValue;

    @Version
    @JsonIgnore
    private Integer version;


    @Transient
    @JsonIgnore
    private Date currentDate = new Date();

    public void plusOneMonthInExpiration(Date previousMonth) {
        if(new DateTime(currentDate).getDayOfMonth() >= END_OF_FEBRUARY) {
            this.expiration = Time.createDateTime(previousMonth)
                    .plusMonths(installmentNumber).dayOfMonth().withMaximumValue().toDate();
            return;
        }
        this.expiration = Time.createDateTime(previousMonth).plusMonths(installmentNumber).toDate();
    }

    public void incrementNumber(Integer previousNumber) {
        this.installmentNumber = previousNumber + ONE_INSTALLMENT;
    }

    private void defineExpiration(Contract contract) {
        if(contract.withMembershipFee()) {
            plusOneMonthInExpiration(currentDate);
            return;
        }
        this.expiration = currentDate;
    }

    private void defineExpirationForNegotiation(HirerNegotiation negotiation, Date currentDate) {
        if(negotiation.getEffectiveDate().after(currentDate)){
            this.expiration = negotiation.getEffectiveDate();
            return;
        }
        this.expiration = currentDate;
    }

    public void defineValue(HirerNegotiation negotiation) {
        defineValue(negotiation, installmentNumber);
    }
    public void defineValue(HirerNegotiation negotiation, Integer installmentNumber) {
        if(negotiation.withFreeInstallments() &&
                installmentNumber <= negotiation.getFreeInstallmentQuantity()) {
            this.value = BigDecimal.ZERO;
            return;
        }
        this.value = negotiation.getInstallmentValue();

    }
}
