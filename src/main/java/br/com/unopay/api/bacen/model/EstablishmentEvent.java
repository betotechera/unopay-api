package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
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
public class EstablishmentEvent implements Updatable{

    @Id
    @Column(name="id")
    @NotNull(groups = {Reference.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @ManyToOne
    @JoinColumn(name="event_id")
    @NotNull(groups = {Create.class, Update.class})
    private Event event;

    @ManyToOne
    @JoinColumn(name="establishment_id")
    @NotNull(groups = {Create.class, Update.class})
    private Establishment establishment;

    @Column(name = "value")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal value;


    @Column(name = "expiration")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private Date expiration;

    @JsonIgnore
    @Version
    private Integer version;

}
