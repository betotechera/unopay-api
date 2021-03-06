package br.com.unopay.api.network.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.SERVICE_REQUIRED;

@Data
@Entity
@Table(name = "event")
public class Event implements Serializable {

    public static final long serialVersionUID = 1L;

    public Event(){}

    @Id
    @Column(name="id")
    @NotNull(groups = {Reference.class})
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JoinColumn(name="service_id")
    @JsonView({Views.Event.List.class,Views.Event.Detail.class, Views.EstablishmentEvent.List.class})
    private Service service;

    @Column(name = "ncm_code")
    @NotNull(groups = {Create.class, Update.class})
    private String ncmCode;

    @Column
    @NotNull(groups = {Create.class, Update.class})
    private String name;

    @Column(name ="request_quantity")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Event.List.class, Views.EstablishmentEvent.List.class})
    private boolean requestQuantity;

    @Column(name ="quantity_unity")
    @JsonView({Views.Event.Detail.class, Views.EstablishmentEvent.List.class})
    private String quantityUnity;

    @Column(name ="request_value")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Event.List.class})
    private boolean requestValue;

    public void updateMe(Event other){
        setName(other.getName());
        setNcmCode(other.getNcmCode());
        setService(other.getService());
        setQuantityUnity(other.getQuantityUnity());
        setRequestQuantity(other.isRequestQuantity());
    }

    public void validate(){
        if(getService() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(SERVICE_REQUIRED);
        }
        if(requestQuantity && quantityUnity == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.QUANTITY_UNITY_REQUIRED);
        }
    }

    public String getProviderId(){
        if(getService() != null){
            return getService().getId();
        }
        return null;
    }

    public boolean toServiceType(ServiceType serviceType){
        if(getService() != null) {
            return Objects.equals(getService().getType(), serviceType);
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public ServiceType serviceType() {
        return service.getType();
    }

    public BigDecimal serviceFeeVal() {
        return service.getFeeVal();
    }
}
