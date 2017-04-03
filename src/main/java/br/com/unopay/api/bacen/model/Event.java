package br.com.unopay.api.bacen.model;

import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static br.com.unopay.api.uaa.exception.Errors.PROVIDER_REQUIRED;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "event")
public class Event implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JoinColumn(name="service_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Provider provider;

    @Column
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String ncmCode;

    @Column
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String name;

    @Column
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private boolean requestQuantity;

    @Column
    @JsonView({Views.Public.class,Views.List.class})
    private String quantityUnity;

    public void updateMe(Event other){
        setName(other.getName());
        setNcmCode(other.getNcmCode());
        setProvider(other.getProvider());
        setQuantityUnity(other.getQuantityUnity());
        setRequestQuantity(other.isRequestQuantity());
    }

    public void validate(){
        if(getProvider() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PROVIDER_REQUIRED);
        }
    }

    public String getProviderId(){
        if(getProvider() != null){
            return getProvider().getId();
        }
        return null;
    }
}