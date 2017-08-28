package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "service")
public class Service implements Serializable {

    public static final long serialVersionUID = 1L;

    public Service(){}

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "code")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Service.List.class,Views.Event.Detail.class, Views.Establishment.Detail.class})
    private Integer code;

    @Column(name = "name")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Service.List.class, Views.Event.Detail.class,
            Views.EstablishmentEvent.List.class, Views.Establishment.Detail.class})
    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Service.Detail.class})
    private ServiceType type;

    @Column(name = "fee_val")
    @JsonView({Views.Service.Detail.class})
    private BigDecimal feeVal;

    @Column(name = "fee_percent")
    @JsonView({Views.Service.Detail.class})
    private Double feePercent;

    public void validate() {
        if(feePercent == null && feeVal == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.LEAST_ONE_FEE_REQUIRED);
        }
        if ((feePercent != null  && feePercent < 0) || (feePercent != null && feePercent > 1D)) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.INVALID_FEE_PERCENT);
        }

    }

    public void updateModel(Service service) {
        this.name = service.getName();
        this.code = service.getCode();
        this.feePercent = service.getFeePercent();
        this.feeVal = service.getFeeVal();
    }

}