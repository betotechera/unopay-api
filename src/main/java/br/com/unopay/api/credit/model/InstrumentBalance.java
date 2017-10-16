package br.com.unopay.api.credit.model;


import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.CascadeType;
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
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "instrument_balance")
public class InstrumentBalance  implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @OneToOne
    @JoinColumn(name="payment_instrument_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.ContractorInstrumentCredit.List.class})
    private PaymentInstrument paymentInstrument;

    @Column(name = "value")
    @JsonView({Views.ContractorInstrumentCredit.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal value;

    @Column(name = "created_date_time")
    @JsonView({Views.ContractorInstrumentCredit.List.class})
    private Date createdDateTime;

    @Version
    @JsonIgnore
    private Integer version;
}
