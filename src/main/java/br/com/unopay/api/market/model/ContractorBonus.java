package br.com.unopay.api.market.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.util.Rounder;
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
import static br.com.unopay.api.uaa.exception.Errors.*;

@Data
@Entity
@Table(name = "contractor_bonus")
public class ContractorBonus implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;
    public static final String EMPTY = "";

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

    @JoinColumn(name="source_identification")
    @NotNull(groups = {Create.class, Update.class})
    private String sourceIdentification;

    @Column(name = "source_value")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.ContractorBonus.List.class})
    private BigDecimal sourceValue;

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

    public void setupMyCreate() {
        setCreatedDateTime(new Date());
        validateMe();
        validateAndSetupEarnedBonusIfNull();
    }

    public void setupMyUpdate() {
        validateMe();
        validateAndSetupEarnedBonusIfNull();
    }

    public void validateMe() {
        validateProcessedAtWhenSituationProcessed();
        validateSituationWhenProcessedAtNotNull();
        validateSourceValue();
    }

    public void validateProcessedAtWhenSituationProcessed() {
        if (getSituation() != null
                && getSituation().equals(PROCESSED)
                && (getProcessedAt() == null || getProcessedAt().toString().equals(EMPTY))) {
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

    private void validateAndSetupEarnedBonusIfNull() {
        validateSourceValue();
        if (getEarnedBonus() == null || getEarnedBonus().toString().equals(EMPTY)) {
            setupEarnedBonus();
        }
    }

    private void validateSourceValue() {
        if (getSourceValue() == null || getSourceValue().toString().equals(EMPTY)) {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(INVALID_SOURCE_VALUE.withOnlyArgument(getSourceValue()));
        }
    }

    private void setupEarnedBonus() {
        setEarnedBonus(Rounder.round(new BigDecimal(getProduct().returnBonusPercentage()).multiply(getSourceValue())));
    }

    @JsonView({Views.ContractorBonus.List.class})
    public String getContractorPersonShortName() {
        if (getContractor() != null) {
            return getContractor().personShortName();
        }
        return null;
    }

    public String productId() {
        if (getProduct() != null) {
           return getProduct().getId();
        }
        return null;
    }

    public String contractorId() {
        if (getContractor() != null) {
            return getContractor().getId();
        }
        return null;
    }

    public String payerId() {
        if (getPayer() != null) {
            return getPayer().getId();
        }
        return null;
    }

    public boolean hasIssuer(Issuer issuer) {
        return issuerId().equals(issuer.getId());
    }

    public String issuerId() {
        return product.getIssuer().getId();
    }

    public BigDecimal getEarnedBonus() {
        return earnedBonus;
    }
}
