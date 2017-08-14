package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@ToString
@EqualsAndHashCode(exclude = "contract")
@Table(name = "contract_establishment")
public class ContractEstablishment implements Serializable {

    public static final long serialVersionUID = 1L;

    public ContractEstablishment(){}

    @Id
    @Column(name="id")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="contract_id")
    private Contract contract;

    @ManyToOne
    @JoinColumn(name="establishment_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class})
    private Establishment establishment;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView({Views.Public.class,Views.List.class})
    private Date creation;

    @Column(name = "origin")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class,Views.List.class})
    private ContractOrigin origin = ContractOrigin.APPLICATION;

    @Version
    @JsonIgnore
    private Integer version;

    public String getEstablishmentId() {
        return establishment.getId();
    }

    public void setMeUpBy(Contract contract) {
        this.contract = contract;
        origin = (origin == null) ? ContractOrigin.APPLICATION : origin;

    }

    public void setCreation(Date dateTime){
        this.creation = ObjectUtils.clone(dateTime);
    }

    public Date getCreation(){
        return ObjectUtils.clone(this.creation);
    }
}
