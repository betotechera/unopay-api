package br.com.unopay.api.order.model;

import br.com.unopay.api.billing.creditcard.model.PaymentMethod;
import br.com.unopay.api.billing.creditcard.model.PaymentRequest;
import br.com.unopay.api.billing.creditcard.model.TransactionStatus;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.util.Arrays;
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
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.billing.creditcard.model.TransactionStatus.*;

@Data
@Entity
@Table(name = "\"order\"")
@ToString
@EqualsAndHashCode(of = {"id"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order implements Updatable{

    public Order(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Order.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToOne
    @NotNull(groups = {Reference.class, Create.Order.Adhesion.class})
    @JoinColumn(name="product_id")
    @JsonView({Views.Order.List.class})
    private Product product;

    @Valid
    @ManyToOne
    @JoinColumn(name="person_id")
    @JsonView({Views.Order.List.class})
    @NotNull(groups = {Create.Order.Adhesion.class, Update.class})
    private Person person;

    @ManyToOne
    @JoinColumn(name="payment_instrument_id")
    @JsonView({Views.Order.Detail.class})
    private PaymentInstrument paymentInstrument;

    @Column(name = "order_number")
    @JsonView({Views.Order.List.class})
    private String number;

    @JsonView({Views.Order.Private.class})
    @Column(name = "partner_id")
    private String partnerId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Order.Detail.class, Views.Order.List.class})
    private OrderStatus status = OrderStatus.WAITING_PAYMENT;

    @Column(name = "value")
    @JsonView({Views.Order.Detail.class, Views.Order.List.class})
    private BigDecimal value;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    @JsonView({Views.Order.Detail.class})
    private Contract contract;

    @Column(name = "create_date_time")
    @JsonView({Views.Order.List.class})
    private Date createDateTime;

    @Column(name = "type")
    @NotNull(groups = {Create.Order.class, Update.class})
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Order.Detail.class, Views.Order.List.class})
    private OrderType type;

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

    public void defineStatus(TransactionStatus transactionStatus) {
        if(Arrays.asList(CANCELED, CANCEL_PENDING, REFUND).contains(transactionStatus)){
            this.status = OrderStatus.CANCELED;
            return;
        }
        if(Arrays.asList(CAPTURED, CAPTURE_RECEIVED).contains(transactionStatus)){
            this.status = OrderStatus.PAID;
            return;
        }
        if(DENIED.equals(transactionStatus)){
            this.status = OrderStatus.PAYMENT_DENIED;
            return;
        }
        this.status = OrderStatus.WAITING_PAYMENT;

    }

    public boolean paid() {
        return OrderStatus.PAID.equals(status);
    }

    @JsonIgnore
    public String getDocumentNumber(){
        if(this.person != null){
            return person.getDocument().getNumber();
        }
        return null;
    }

    @JsonIgnore
    public String getProductId(){
        if(this.product != null){
            return product.getId();
        }
        return null;
    }

    @JsonIgnore
    public String getContractId(){
        if(this.contract != null){
            return contract.getId();
        }
        return null;
    }

    @JsonIgnore
    public String getProductCode(){
        if(this.product != null){
            return product.getCode();
        }
        return null;
    }

    public String instrumentId(){
        if(this.getPaymentInstrument() != null){
            return this.getPaymentInstrument().getId();
        }
        return null;
    }

    public boolean is(PaymentMethod method) {
        return paymentRequest.getMethod().equals(method);
    }

    public boolean isType(OrderType type) {
        return this.type.equals(type);
    }

    @JsonProperty
    public BigDecimal getProductInstallmentValue(){
        if(getProduct() != null){
            return getProduct().getInstallmentValue();
        }
        return null;
    }


    @JsonIgnore
    public String getPersonEmail() {
        if(this.person != null && this.person.getPhysicalPersonDetail() != null){
            return this.person.getPhysicalPersonDetail().getEmail();
        }
        return null;
    }

    public void normalize() {
        if(this.person != null){
            this.person.normalize();
        }
        if(this.paymentRequest != null && this.paymentRequest.getCreditCard() != null){
            this.paymentRequest.getCreditCard().normalize();
        }
    }
}
