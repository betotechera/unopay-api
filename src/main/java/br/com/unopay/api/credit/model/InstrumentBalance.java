package br.com.unopay.api.credit.model;


import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.util.Rounder;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.INVALID_VALUE;

@Data
@Entity
@ToString(exclude = "paymentInstrument")
@EqualsAndHashCode(of = {"id", "documentNumber"})
@Table(name = "instrument_balance")
public class InstrumentBalance  implements Serializable {

    public static final long serialVersionUID = 1L;

    public InstrumentBalance(){}

    public InstrumentBalance(PaymentInstrument paymentInstrument, BigDecimal value){
        this.paymentInstrument = paymentInstrument;
        this.value = Rounder.round(value);
        this.documentNumber = paymentInstrument.getContractor().getDocumentNumber();
        this.createdDateTime = new Date();
        this.updatedDateTime = new Date();
    }

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name="payment_instrument_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.InstrumentBalance.Detail.class})
    private PaymentInstrument paymentInstrument;

    @Column(name = "value")
    @JsonView({Views.InstrumentBalance.List.class, Views.PaymentInstrument.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal value;

    @Column(name = "document_number")
    @JsonView({Views.InstrumentBalance.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private String documentNumber;

    @Column(name = "created_date_time")
    @JsonView({Views.InstrumentBalance.Detail.class})
    private Date createdDateTime;

    @Column(name = "updated_date_time")
    @JsonView({Views.InstrumentBalance.List.class})
    private Date updatedDateTime;

    @Version
    @JsonIgnore
    private Integer version;

    public void add(BigDecimal value) {
        if(value == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(INVALID_VALUE);
        }
        if(this.value == null){
            this.value = value;
            return;
        }
        this.value = Rounder.round(this.value.add(value));
    }

    public void subtract(BigDecimal value) {
        if(value == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(INVALID_VALUE);
        }
        if(this.value == null || this.value.compareTo(value) == -1){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.BALANCE_LESS_THAN_REQUIRED);
        }
        this.value = Rounder.round(this.value.subtract(value));
    }
}
