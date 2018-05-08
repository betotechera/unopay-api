package br.com.unopay.api.market.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.order.model.PaymentStatus;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "bonus_billing")
public class BonusBilling implements Serializable, Updatable {

    private static final long serialVersionUID = 2732233885546623588L;

    public BonusBilling() {}

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "created_date_time")
    @JsonView({Views.BonusBilling.Detail.class})
    private Date createdDateTime;

    @Valid
    @ManyToOne
    @JoinColumn(name = "person_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.BonusBilling.List.class})
    private Person person;

    @Column(name = "total")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.BonusBilling.Detail.class})
    private Double total;

    @Column(name = "processed_at")
    @JsonView({Views.BonusBilling.Detail.class})
    private Date processedAt;

    @Column(name = "number")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.BonusBilling.Detail.class})
    private String number;

    @Column(name = "expiration")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.BonusBilling.Detail.class})
    private Date expiration;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Order.Detail.class, Views.Order.Detail.class})
    private PaymentStatus status;

    public void validateMe() {
        if(person == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.PERSON_REQUIRED);
        }

        if(total == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.BONUS_BILLING_TOTAL_REQUIRED);
        }

        validateDates();
    }

    private void validateDates() {
        Date today = new Date();

        if(processedAt.after(today)) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.INVALID_BONUS_BILLING_PROCESS_DATE);
        }

        if(expiration.before(today)) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.INVALID_BONUS_BILLING_EXPIRATION_DATE);
        }
    }

    public String personId() {
        return person != null ? person.getId() : null;
    }
}
