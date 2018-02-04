package br.com.unopay.api.credit.model;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
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

import static br.com.unopay.api.uaa.exception.Errors.HIRER_BELONG_TO_OTHER_CONTRACT;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_VALUE;

@Data
@Entity
@Table(name = "contractor_credit_recurrence")
public class ContractorCreditRecurrence {

    @Id
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @ManyToOne
    @JoinColumn(name="hirer_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.ContractorCreditRecurrence.Detail.class})
    private Hirer hirer;

    @ManyToOne
    @JoinColumn(name="contract_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.ContractorCreditRecurrence.Detail.class})
    private Contract contract;

    @Column(name = "value")
    @JsonView({Views.ContractorCreditRecurrence.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal value;

    @ManyToOne
    @JoinColumn(name="payment_instrument_id")
    @JsonView({Views.ContractorCreditRecurrence.List.class})
    private PaymentInstrument paymentInstrument;


    @Column(name = "created_date_time")
    @JsonView({Views.ContractorCreditRecurrence.List.class})
    private Date createdDateTime;

    @JsonIgnore
    @Version
    private Integer version;

    public void validateMe() {
        if(getValue() == null ||
                getValue().compareTo(BigDecimal.ZERO) == 0 ||
                getValue().compareTo(BigDecimal.ZERO) == -1){
            throw UnovationExceptions.unprocessableEntity().withErrors(INVALID_VALUE.withOnlyArgument(getValue()));
        }
        checkHirer();
    }

    public void checkHirer(){
        if(!getContract().containsHirer(getHirer())){
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(HIRER_BELONG_TO_OTHER_CONTRACT.withOnlyArgument(hirerId()));
        }
    }

    public String instrumentId(){
        if(getPaymentInstrument() != null){
            return getPaymentInstrument().getId();
        }
        return null;
    }

    public String contractId(){
        if(getContract() != null){
            return getContract().getId();
        }
        return null;
    }

    public String contractorDocument(){
        if(getContract() != null && getContract().getContractor() != null){
            return getContract().getContractor().getDocumentNumber();
        }
        return null;
    }

    public String hirerId(){
        if(getHirer() !=null){
            return getHirer().getId();
        }
        return null;
    }

    public boolean withPaymentInstrument() {
        return getPaymentInstrument() != null;
    }
}
