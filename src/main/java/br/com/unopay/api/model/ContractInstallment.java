package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Create;
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
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@ToString
@Table(name = "contract_installment")
public class ContractInstallment implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;

    public ContractInstallment(){}

    @Id
    @Column(name="id")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @ManyToOne
    @JoinColumn(name="contract_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class})
    private Contract contract;

    @Column(name = "installment_number")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class})
    private Integer installmentNumber;

    @Column(name = "value")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class})
    private BigDecimal value;

    @Column(name = "expiration")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Date expiration;

    @Column(name = "payment_date_time")
    @JsonView({Views.Public.class,Views.List.class})
    private Date paymentDateTime;

    @Column(name = "payment_value")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class})
    private BigDecimal paymentValue;

    @Version
    @JsonIgnore
    private Integer version;

}
