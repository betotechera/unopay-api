package br.com.unopay.api.bacen.model;

import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "service")
public class Service implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @JsonView({Views.Public.class, Views.List.class})
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class, Views.List.class})
    private Integer code;

    @Column
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class, Views.List.class})
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class, Views.List.class})
    private ServiceType type;

    @Column
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class, Views.List.class})
    private BigDecimal taxVal;

    @Column
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class, Views.List.class})
    private Double taxPercent;

    public void validate() {
        if (taxPercent < 0 || taxPercent > 1D)
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.INVALID_TAX_PERCENT);

    }

    public void updateModel(Service service) {
        this.name = service.getName();
        this.code = service.getCode();
        this.taxPercent = service.getTaxPercent();
        this.taxVal = service.getTaxVal();
    }

}