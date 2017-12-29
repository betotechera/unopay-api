package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.model.UserDetail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@ToString(exclude = {"accreditedNetwork", "issuer"})
@EqualsAndHashCode(exclude = {"accreditedNetwork", "issuer"})
@Table(name = "accredited_network_issuer")
public class AccreditedNetworkIssuer implements Serializable {

    public AccreditedNetworkIssuer(){}

    @Id
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @ManyToOne
    @NotNull(groups = {Create.class})
    @JoinColumn(name="accredited_network_id")
    @JsonView({Views.Issuer.AccreditedNetwork.class})
    private AccreditedNetwork accreditedNetwork;

    @ManyToOne
    @JoinColumn(name="issuer_id")
    @JsonView({Views.Issuer.AccreditedNetwork.class})
    private Issuer issuer;

    @Column(name = "created_date_time")
    @JsonView({Views.Issuer.AccreditedNetwork.class})
    private Date createdDateTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonView({Views.Issuer.AccreditedNetwork.class})
    private UserDetail user;

    @Column(name = "active")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Issuer.AccreditedNetwork.class})
    private Boolean active = Boolean.FALSE;

    @Version
    @JsonIgnore
    private Integer version;

    public void setCreation(Date dateTime){
        this.createdDateTime = ObjectUtils.clone(dateTime);
    }

    public Date getCreation(){
        return ObjectUtils.clone(this.createdDateTime);
    }
}
