package br.com.unopay.api.scheduling.model

import java.io.Serializable
import java.util
import java.util.Date

import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.market.model.AuthorizedMember
import br.com.unopay.api.model.validation.group.{Create, Update, Views}
import br.com.unopay.api.model.{Contract, PaymentInstrument, Updatable}
import br.com.unopay.api.network.model.{Branch, Event}
import br.com.unopay.api.uaa.model.UserDetail
import com.fasterxml.jackson.annotation.JsonView
import javax.persistence._
import javax.validation.constraints.{Future, NotNull, Size}
import lombok.{EqualsAndHashCode, Getter, ToString}
import org.hibernate.annotations.{BatchSize, GenericGenerator}

import scala.beans.BeanProperty

@ToString(of = Array("token"))
@SerialVersionUID(1L)
@EqualsAndHashCode(of = Array ("id", "token"))
@Getter
@Entity
@Table(name = "scheduling")
class Scheduling extends Serializable with Updatable {


    @Id
    @BeanProperty
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    var id: String = _

    @BeanProperty
    @Column(name = "token")
    var token: String = _

    @BeanProperty
    @Column(name = "created_date_time")
    @JsonView(Array(classOf[Views.Scheduling.List]))
    var createdDateTime: Date = _

    @BeanProperty
    @NotNull(groups = Array(classOf[Create], classOf[Update]))
    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonView(Array(classOf[Views.Scheduling.Detail]))
    var branch: Branch = _

    @BeanProperty
    @NotNull(groups = Array(classOf[Create], classOf[Update]))
    @ManyToOne
    @JoinColumn(name = "contract_id")
    @JsonView(Array(classOf[Views.Scheduling.Detail]))
    var contract: Contract = _

    @BeanProperty
    @NotNull(groups = Array(classOf[Create], classOf[Update]))
    @ManyToOne
    @JoinColumn(name = "contractor_id")
    @JsonView(Array(classOf[Views.Scheduling.List]))
    var contractor: Contractor = _

    @BeanProperty
    @NotNull(groups = Array(classOf[Create], classOf[Update]))
    @ManyToOne
    @JoinColumn(name = "payment_instrument_id")
    @JsonView(Array(classOf[Views.Scheduling.Detail]))
    var paymentInstrument: PaymentInstrument = _

    @BeanProperty
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonView(Array(classOf[Views.Scheduling.Detail]))
    var user: UserDetail = _

    @BeanProperty
    @NotNull(groups = Array(classOf[Create], classOf[Update]))
    @Column(name = "service_description")
    @JsonView(Array(classOf[Views.Scheduling.List]))
    @Size(min = 3, max = 30)
    var serviceDescription: String = _

    @BeanProperty
    @ManyToOne
    @JoinColumn(name = "authorized_member_id")
    @JsonView(Array(classOf[Views.Scheduling.Detail]))
    var authorizedMember: AuthorizedMember = _

    @BeanProperty
    @Future(groups = Array(classOf[Create], classOf[Update]))
    @NotNull(groups = Array(classOf[Create], classOf[Update]))
    @Column(name = "scheduling_date")
    @JsonView(Array(classOf[Views.Scheduling.List]))
    var date: Date = _

    @BeanProperty
    @Column(name = "expiration_date")
    @JsonView(Array(classOf[Views.Scheduling.List]))
    var expirationDate: Date = _

    @BeanProperty
    @Column(name = "cancellation_date")
    @JsonView(Array(classOf[Views.Scheduling.List]))
    var cancellationDate: Date = _

    @BatchSize(size = 10)
    @OneToMany
    @JsonView(Array(classOf[Views.Scheduling.Detail]))
    @JoinTable(name = "scheduling_event",
        joinColumns = Array(new JoinColumn(name = "scheduling_id")),
        inverseJoinColumns = Array(new JoinColumn(name = "event_id")))
    var events: util.List[Event] = _

    def branchId(): java.lang.String = {
        if(this.branch != null) {
           return this.branch.getId
        }
        null
    }

    def contractorId(): String = {
        if(this.contractor != null){
            return this.contractor.getId
        }
        null
    }

    def instrumentId(): String = {
        if(this.paymentInstrument != null){
            return this.paymentInstrument.getId
        }
        null
    }

    def cancelMe(): Unit = {
        if(this.cancellationDate == null) {
            this.cancellationDate = new Date()
        }
    }

    @PrePersist
    def prePersist(): Unit = {
        this.createdDateTime = new Date
    }

    def hasAuthorizedMember: Boolean = this.authorizedMember != null && this.authorizedMember.getId != null

    def hasEvents(): Boolean = {
        this.events != null && !this.events.isEmpty
    }

    def getFormattedAddress(): java.lang.String = {
        if(this.branch != null) {
            return this.branch.formattedAddress()
        }
        null
    }

}
