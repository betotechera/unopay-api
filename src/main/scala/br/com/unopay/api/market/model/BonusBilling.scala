package br.com.unopay.api.market.model

import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Updatable
import br.com.unopay.api.model.validation.group.Create
import br.com.unopay.api.model.validation.group.Update
import br.com.unopay.api.model.validation.group.Views
import br.com.unopay.api.order.model.PaymentStatus
import br.com.unopay.api.uaa.exception.Errors
import br.com.unopay.bootcommons.exception.UnovationExceptions
import com.fasterxml.jackson.annotation.JsonView
import lombok.Data
import org.hibernate.annotations.GenericGenerator
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.Valid
import javax.validation.constraints.NotNull
import java.io.Serializable
import java.lang._
import java.net.URI
import java.util.Date

import scala.beans.BeanProperty

@Data
@Entity
@Table(name = "bonus_billing")
@SerialVersionUID(2732233885546623588L)
class BonusBilling extends Serializable with Updatable {

    @Id
    @BeanProperty
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    var id: String = _

    @BeanProperty
    @Column(name = "created_date_time")
    @JsonView(Array(classOf[Views.BonusBilling.Detail]))
    var createdDateTime: Date = _

    @Valid
    @BeanProperty
    @ManyToOne
    @JoinColumn(name = "person_id")
    @NotNull(groups = Array(classOf[Create], classOf[Update]))
    @JsonView(Array(classOf[Views.BonusBilling.List]))
    var person: Person = _

    @Column(name = "total")
    @BeanProperty
    @NotNull(groups = Array(classOf[Create], classOf[Update]))
    @JsonView(Array(classOf[Views.BonusBilling.Detail]))
    var total: Double = _

    @Column(name = "processed_at")
    @BeanProperty
    @JsonView(Array(classOf[Views.BonusBilling.Detail]))
    var processedAt: Date = _

    @Column(name = "number")
    @BeanProperty
    @NotNull(groups = Array(classOf[Create], classOf[Update]))
    @JsonView(Array(classOf[Views.BonusBilling.Detail]))
    var number: String = _

    @Column(name = "expiration")
    @BeanProperty
    @NotNull(groups = Array(classOf[Create], classOf[Update]))
    @JsonView(Array(classOf[Views.BonusBilling.Detail]))
    var expiration: Date = _

    @Column(name = "status")
    @BeanProperty
    @Enumerated(EnumType.STRING)
    @NotNull(groups = Array(classOf[Create], classOf[Update]))
    @JsonView(Array(classOf[Views.Order.Detail]))
    var status: PaymentStatus = _

    def validateMe() {
        if(person == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.PERSON_REQUIRED)
        }

        if(total == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.BONUS_BILLING_TOTAL_REQUIRED)
        }

        validateDates()
    }

    private def validateDates() {
        val today = new Date()

        if(processedAt.after(today)) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.INVALID_BONUS_BILLING_PROCESS_DATE)
        }

        if(expiration.before(today)) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.INVALID_BONUS_BILLING_EXPIRATION_DATE)
        }
    }

    def  personId(): String = {
        if(person != null) person.getId else null
    }
}
