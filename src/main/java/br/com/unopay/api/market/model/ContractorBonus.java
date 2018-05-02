package br.com.unopay.api.market.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static br.com.unopay.api.market.model.BonusSituation.FOR_PROCESSING;
import static br.com.unopay.api.market.model.BonusSituation.PROCESSED;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_BONUS_SITUATION;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_PROCESSED_AT;

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
    @JsonView({Views.ContractorBonus.List.class})
    private Product product;

    @ManyToOne
    @JoinColumn(name="person_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.ContractorBonus.List.class})
    private Person payer;

    @ManyToOne
    @JoinColumn(name="contractor_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.ContractorBonus.Detail.class})
    private Contractor contractor;

    @Column(name = "earned_bonus")
    @NotNull(groups = {Create.class})
    @JsonView({Views.ContractorBonus.List.class})
    private BigDecimal earnedBonus;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.ContractorBonus.List.class})
    private BonusSituation situation = FOR_PROCESSING;

    @Column(name = "processed_at")
    @JsonView({Views.ContractorBonus.Detail.class})
    private Date processedAt;

    @Column(name = "created_date_time")
    @JsonView({Views.ContractorBonus.Detail.class})
    private Date createdDateTime;

    @JsonIgnore
    @Version
    private Integer version;

    public void validateMe() {
        validateProcessedAtWhenSituationProcessed();
        validateSituationWhenProcessedAtNotNull();
    }

    public void validateProcessedAtWhenSituationProcessed() {
        if (getSituation() != null
                && getSituation().equals(PROCESSED)
                && (getProcessedAt() == null || getProcessedAt().toString().equals(""))) {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(INVALID_PROCESSED_AT.withOnlyArgument(getProcessedAt()));
        }
    }

    public void validateSituationWhenProcessedAtNotNull() {
        if (getProcessedAt() != null
                && (getSituation() == null || !getSituation().equals(PROCESSED))) {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(INVALID_BONUS_SITUATION.withOnlyArgument(getSituation()));
        }
    }

    @JsonView({Views.ContractorBonus.List.class})
    public String getContractorPersonShortName() {
        if (getContractor() != null) {
            return getContractor().personShortName();
        }
        return null;
    }
}
