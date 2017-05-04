package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Establishment;
import static br.com.unopay.api.model.ContractOrigin.UNOPAY;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

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
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
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
    @JoinColumn(name="contract_id")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Public.class})
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
    private ContractOrigin origin = UNOPAY;

    @Version
    @JsonIgnore
    private Integer version;
}
