package br.com.unopay.api.credit.model;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.RecurrencePeriod;
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

    @Column(name = "recurrence_period")
    @NotNull(groups = {Create.class, Update.class})
    @Enumerated(EnumType.STRING)
    @JsonView({Views.ContractorCreditRecurrence.Detail.class})
    private RecurrencePeriod recurrencePeriod;


    @Column(name = "created_date_time")
    @JsonView({Views.ContractorCreditRecurrence.List.class})
    private Date createdDateTime;

    @JsonIgnore
    @Version
    private Integer version;

}
