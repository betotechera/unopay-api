package br.com.unopay.api.model;

import br.com.unopay.api.network.model.EstablishmentEvent;
import br.com.unopay.api.network.model.Event;
import br.com.unopay.api.network.model.ServiceType;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED;

@Slf4j
@Data
@Entity
@EqualsAndHashCode(exclude = {"establishmentEvent", "serviceAuthorize"})
@ToString(exclude = {"establishmentEvent", "serviceAuthorize"})
@Table(name = "service_authorize_event")
public class ServiceAuthorizeEvent implements Serializable {

    private static final long serialVersionUID = -5614959827525635394L;

    public ServiceAuthorizeEvent(EstablishmentEvent establishmentEvent){
        this.establishmentEvent = establishmentEvent;
    }

    public ServiceAuthorizeEvent(){}

    @Id
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @ManyToOne
    @NotNull(groups = {Reference.class})
    @JoinColumn(name="establishment_event_id")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private EstablishmentEvent establishmentEvent;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="service_authorize_id")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private ServiceAuthorize serviceAuthorize;

    @ManyToOne
    @JoinColumn(name="event_id")
    @JsonView({Views.ServiceAuthorize.Detail.class, Views.ServiceAuthorize.List.class})
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    @JsonView({Views.ServiceAuthorize.Detail.class, Views.ServiceAuthorize.List.class})
    private ServiceType serviceType;

    @Column(name = "event_value")
    @JsonView({Views.ServiceAuthorize.List.class})
    private BigDecimal eventValue;

    @Column(name = "value_fee")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private BigDecimal valueFee;

    @Column(name = "event_quantity")
    @JsonView({Views.ServiceAuthorize.Detail.class})
    private BigDecimal eventQuantity;

    @Column(name = "created_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView({Views.ServiceAuthorize.List.class})
    private Date createdDateTime;

    public void defineValidEventValues(EstablishmentEvent establishmentEvent){
        this.establishmentEvent = establishmentEvent;
        this.setEvent(establishmentEvent.getEvent());
        this.setServiceType(establishmentEvent.serviceType());
        this.setEventValue(establishmentEvent.getValue());
        this.setValueFee(event.serviceFeeVal());
        this.validate();
        this.createdDateTime = new Date();
    }

    public String establishmentEventId() {
        return establishmentEvent != null ? establishmentEvent.getId() : "";
    }

    public void validate() {
        if(eventValue == null || eventValue.compareTo(BigDecimal.ZERO) < 0 ||
                eventValue.compareTo(BigDecimal.ZERO) == 0){
            log.info("EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED {}", eventValue);
            throw UnovationExceptions.unprocessableEntity().withErrors(EVENT_VALUE_GREATER_THAN_ZERO_REQUIRED);
        }
    }

    public boolean eventQuantityNotDefined(){
        return getEventQuantity() == null ||
                getEventQuantity().compareTo(BigDecimal.ZERO) == 0 ||
                getEventQuantity().compareTo(BigDecimal.ZERO) < 0 ;
    }

    public boolean eventRequestQuantity(){
        if(getEstablishmentEvent() != null && getEstablishmentEvent().getEvent() != null){
            return getEstablishmentEvent().getEvent().isRequestQuantity();
        }
        return false;
    }

    public BigDecimal eventValueByQuantity(){
        if(eventQuantityNotDefined()){
            return this.eventValue;
        }
        return this.eventValue.multiply(getEventQuantity());
    }
}
