package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static br.com.unopay.api.model.BonusSituation.FOR_PROCESSING;

@Data
@Entity
@Table(name = "contractor_bonus")
public class ContractorBonus implements Serializable {

    public static final long serialVersionUID = 1L;

    public ContractorBonus(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToOne
    @JoinColumn(name="product_id")
    @NotNull(groups = {Create.class})
    private Product product;

    @ManyToOne
    @JoinColumn(name="person_id")
    @NotNull(groups = {Create.class})
    private Person person;

    @ManyToOne
    @JoinColumn(name="contractor_id")
    @NotNull(groups = {Create.class})
    private Contractor contractor;

    @Column(name = "earned_bonus")
    @NotNull(groups = {Create.class})
    private BigDecimal earnedBonus;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    private BonusSituation situation = FOR_PROCESSING;

    @Column(name = "processed_at")
    private Date processedAt;

    @Column(name = "created_date_time")
    private Date createdDateTime;

    @JsonIgnore
    @Version
    private Integer version;
}
