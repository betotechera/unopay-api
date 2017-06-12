package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Event;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.pamcary.translate.KeyField;
import br.com.unopay.api.pamcary.translate.WithKeyFields;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FreightReceipt implements Serializable {

    public static final long serialVersionUID = 1L;

    public FreightReceipt(){}

    @WithKeyFields
    @NotNull(groups = {Create.class,Update.class})
    @JsonView({Views.Public.class})
    private Contract contract;

    @WithKeyFields
    @NotNull(groups = {Create.class,Update.class})
    @JsonView({Views.Public.class})
    private Contractor contractor;

    @WithKeyFields
    @NotNull(groups = {Create.class,Update.class})
    @JsonView({Views.Public.class})
    private Establishment establishment;

    @NotNull(groups = {Create.class,Update.class})
    @JsonView({Views.Public.class})
    private ServiceType serviceType;

    @Valid
    @NotNull(groups = {Create.class,Update.class})
    @JsonView({Views.Public.class})
    private CargoContract cargoContract;

    @JsonView({Views.Public.class})
    private Double fuelSupplyQuantity;

    @NotNull(groups = {Create.class,Update.class})
    @JsonView({Views.Public.class})
    private BigDecimal fuelSupplyValue;

    @NotNull(groups = {Create.class,Update.class})
    @JsonView({Views.Public.class})
    private CreditInsertionType creditInsertionType;

    @NotNull(groups = {Create.class,Update.class})
    @JsonView({Views.Public.class})
    private String instrumentPassword;

    @NotNull(groups = {Create.class,Update.class})
    @JsonView({Views.Public.class})
    private Event fuelEvent;



    @JsonIgnore
    private ContractorInstrumentCredit instrumentCredit;

    public String contractId(){
        if(getContract() != null){
            return getContract().getId();
        }
        return null;
    }

    public String fuelEventId(){
        if(getFuelEvent() != null){
            return getFuelEvent().getId();
        }
        return null;
    }

    public String cargoContractId() {
        if(getCargoContract() != null){
            return getCargoContract().getId();
        }
        return null;
    }
}
