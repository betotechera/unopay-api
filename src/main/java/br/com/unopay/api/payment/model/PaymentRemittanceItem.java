package br.com.unopay.api.payment.model;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;


@Data
@Entity
@ToString(exclude = { "paymentRemittance", "establishment" })
@EqualsAndHashCode
@Table(name = "payment_remittance_item")
public class PaymentRemittanceItem  implements Serializable {

    public static final long serialVersionUID = 1L;

    public PaymentRemittanceItem(){}

    public PaymentRemittanceItem(BatchClosing batchClosing){
        this.establishment = batchClosing.getEstablishment();
        this.establishmentBankCode = batchClosing.getEstablishment().getBankAccount().getBacenCode();
        this.situation = RemittanceSituation.PROCESSING;
    }

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="payment_remittance_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class,Views.List.class})
    private PaymentRemittance paymentRemittance;

    @ManyToOne
    @JoinColumn(name="establishment_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Establishment establishment;

    @Column(name = "establishment_bank_code")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private Integer establishmentBankCode;

    @Column(name = "value")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private BigDecimal value;

    @Column(name = "situation")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private RemittanceSituation situation;

    @Column(name = "occurrence_code")
    @JsonView({Views.Public.class})
    @NotNull(groups = {Create.class})
    private String occurrenceCode;

    @JsonIgnore
    @Version
    private Integer version;

    public void updateValue(BigDecimal value){
        if(this.value ==null ){
            this.value = value;
            return;
        }
        this.value = this.value.add(value);
    }
}
