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

import javax.persistence.*; // NOSONAR
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "service")
public class Service implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @JsonView({Views.Public.class, Views.List.class})
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "code")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class, Views.List.class})
    private Integer code;

    @Column(name = "name")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class, Views.List.class})
    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class, Views.List.class})
    private ServiceType type;

    @Column(name = "tax_val")
    @JsonView({Views.Public.class, Views.List.class})
    private BigDecimal taxVal;

    @Column(name = "tax_percent")
    @JsonView({Views.Public.class, Views.List.class})
    private Double taxPercent;

    public Service() {}

    public void validate() {
        if(taxPercent == null && taxVal == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.LEAST_ONE_TAX_REQUIRED);
        }
        if ((taxPercent != null  && taxPercent < 0) || (taxPercent != null && taxPercent > 1D)) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.INVALID_TAX_PERCENT);
        }

    }

    public void updateModel(Service service) {
        this.name = service.getName();
        this.code = service.getCode();
        this.taxPercent = service.getTaxPercent();
        this.taxVal = service.getTaxVal();
    }

}