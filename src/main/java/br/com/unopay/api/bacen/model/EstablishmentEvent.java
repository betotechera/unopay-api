package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByPosition;
import com.opencsv.bean.CsvDate;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "establishment_event")
public class EstablishmentEvent implements Updatable, Serializable {

    public static final long serialVersionUID = 1L;

    public EstablishmentEvent(){}

    @Id
    @Column(name="id")
    @NotNull(groups = {Reference.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @ManyToOne
    @JoinColumn(name="event_id")
    @JsonView({Views.EstablishmentEvent.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private Event event;


    @ManyToOne
    @JoinColumn(name="establishment_id")
    @JsonView({Views.EstablishmentEvent.List.class})
    private Establishment establishment;

    @CsvBindByName
    @Column(name = "value")
    @JsonView({Views.EstablishmentEvent.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal value;

    @Column(name = "expiration")
    @JsonView({Views.EstablishmentEvent.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private Date expiration;

    @JsonIgnore
    @Version
    private Integer version;

}
