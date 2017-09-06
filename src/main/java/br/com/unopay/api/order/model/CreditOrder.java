package br.com.unopay.api.order.model;

import br.com.unopay.api.billing.model.PaymentRequest;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "credit_order")
@ToString
@EqualsAndHashCode(of = {"id"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditOrder {

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Billing.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToOne
    @NotNull(groups = {Reference.class})
    @JoinColumn(name="product_id")
    @JsonView({Views.Order.List.class})
    private Product product;

    @ManyToOne
    @JoinColumn(name="person_id")
    @JsonView({Views.Order.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private Person person;

    @Column(name = "email")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Order.List.class})
    private String email;

    @Column(name = "order_number")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Order.List.class})
    private String number;

    @JsonView({Views.Order.Private.class})
    @Column(name = "partner_id")
    private String partnerId;

    @Column(name = "create_date_time")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Billing.List.class})
    private Date createDateTime;

    @Valid
    @Transient
    private PaymentRequest paymentRequest;

    @JsonIgnore
    @Version
    private Integer version;

    public void incrementNumber(String lastNumber) {
        Long number = lastNumber == null ? 0 : Long.valueOf(lastNumber);
        number++;
        this.number = StringUtils.leftPad(String.valueOf(number),10,"0");
    }
}
