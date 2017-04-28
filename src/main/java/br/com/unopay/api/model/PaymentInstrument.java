package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "payment_instrument")
public class PaymentInstrument implements Serializable {

    public static final long serialVersionUID = 1L;

    public PaymentInstrument(){}

    @Id
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @JsonView({Views.Public.class,Views.List.class})
    @GeneratedValue(generator="system-uuid")
    private String id;

    @Column(name = "type")
    @NotNull(groups = {Create.class, Update.class})
    @Enumerated(value = EnumType.STRING)
    private PaymentInstrumentType type;

    @Column(name = "payment_number")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String number;

    @ManyToOne
    @JoinColumn(name="product_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Product product;

    @ManyToOne
    @JoinColumn(name="contractor_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Contractor contractor;

    @Column(name = "created_date")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private Date createdDate;

    @Column(name = "expiration_date")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private Date expirationDate;

    @Column(name = "password")
    @JsonView({Views.Public.class,Views.List.class})
    private String password;

    @Column(name = "situation")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    @Enumerated(value = EnumType.STRING)
    private PaymentInstrumentSituation situation;

    @Column(name = "external_number_id")
    @JsonView({Views.Public.class,Views.List.class})
    private String externalNumberId;

    @Version
    @JsonIgnore
    private Integer version;


    public void updateMe(PaymentInstrument instrument) {
        type = instrument.getType();
        number = instrument.getNumber();
        product = instrument.getProduct();
        contractor = instrument.getContractor();
        createdDate = instrument.getCreatedDate();
        expirationDate = instrument.getExpirationDate();
        password = instrument.getPassword();
        situation = instrument.getSituation();
        externalNumberId = instrument.getExternalNumberId();
    }

}
