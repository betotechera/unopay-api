package br.com.unopay.api.market.model

import br.com.unopay.api.model.{Billable, Person, Updatable}
import br.com.unopay.api.model.validation.group.Create
import br.com.unopay.api.model.validation.group.Update
import br.com.unopay.api.model.validation.group.Views
import br.com.unopay.api.order.model.PaymentStatus
import br.com.unopay.api.uaa.exception.Errors
import br.com.unopay.bootcommons.exception.UnovationExceptions
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonView}
import lombok.Data
import org.hibernate.annotations.GenericGenerator
import javax.persistence._
import javax.validation.Valid
import javax.validation.constraints.NotNull
import java.io.Serializable
import java.lang._
import java.util.Date

import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.billing.boleto.model.TicketPaymentSource

import scala.beans.BeanProperty

@Data
@Entity
@Table(name = "bonus_billing")
@SerialVersionUID(2732233885546623588L)
class BonusBilling extends Serializable with Updatable with Billable{

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
    var payer: Person = _

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

    @JsonIgnore
    @OneToMany
    @BeanProperty
    @JoinTable(name = "contractor_bonus_billing",
        joinColumns = Array(new JoinColumn(name = "bonus_billing_id")),
        inverseJoinColumns = Array(new JoinColumn(name = "contractor_bonus_id")))
    var contractorBonuses: java.util.Set[ContractorBonus] = _

    @Valid
    @BeanProperty
    @ManyToOne
    @JoinColumn(name = "issuer_id")
    @NotNull(groups = Array(classOf[Create], classOf[Update]))
    @JsonView(Array(classOf[Views.Order.Detail]))
    var issuer: Issuer = _

    def addToContractorBonuses(bonus: ContractorBonus) {
        if (contractorBonuses == null) setContractorBonuses(new java.util.HashSet[ContractorBonus]())
        contractorBonuses.add(bonus)
    }

    def validateMe() {
        if(payer == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.PERSON_REQUIRED)
        }

        if(total == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.BONUS_BILLING_TOTAL_REQUIRED)
        }

        if(issuer == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.BONUS_BILLING_ISSUER_REQUIRED)
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
        if(payer != null) payer.getId else null
    }

    override def getValue: java.math.BigDecimal = java.math.BigDecimal.valueOf(total)

    override def getCreateDateTime: Date = createdDateTime

    override def getBillingMail: String = {
        return if (payer.isLegal)
            payer.getLegalPersonDetail.getResponsibleEmail
        else
            payer.getPhysicalPersonDetail.getEmail
    }

    override def getPaymentSource: TicketPaymentSource = TicketPaymentSource.CONTRACTOR_BONUS
}
