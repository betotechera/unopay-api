package br.com.unopay.api.order.model;

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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
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

    @Column(name = "number")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Order.List.class})
    private String number;

    @Column(name = "create_date_time")
    @NotNull(groups = {Create.class})
    @JsonView({Views.Billing.List.class})
    private Date createDateTime;

    @JsonIgnore
    @Version
    private Integer version;
}
