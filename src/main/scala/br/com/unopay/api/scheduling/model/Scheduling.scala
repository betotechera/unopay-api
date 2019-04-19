package br.com.unopay.api.scheduling.model

import java.io.Serializable
import java.util.Date

import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.market.model.AuthorizedMember
import br.com.unopay.api.model.validation.group.{Create, Update, Views}
import br.com.unopay.api.model.{Contract, PaymentInstrument, Updatable}
import br.com.unopay.api.network.model.Branch
import br.com.unopay.api.uaa.model.UserDetail
import com.fasterxml.jackson.annotation.JsonView
import javax.persistence._
import javax.validation.constraints.NotNull
import lombok.{EqualsAndHashCode, Getter, ToString}
import org.hibernate.annotations.GenericGenerator

import scala.beans.BeanProperty

@ToString(of = Array("token"))
@SerialVersionUID(1L)
@EqualsAndHashCode(of = Array ("id", "token"))
@Getter
@Entity
class Scheduling extends Serializable with Updatable {

    @BeanProperty
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    var id: String = _

    @BeanProperty
    @NotNull
    @JsonView(Array(classOf[Views.Scheduling.List]))
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
    @JsonView(Array(classOf[Views.Scheduling.Detail]))
    var contractor: Contractor = _

    @BeanProperty
    @NotNull(groups = Array(classOf[Create], classOf[Update]))
    @ManyToOne
    @JoinColumn(name = "payment_instrument_id")
    @JsonView(Array(classOf[Views.Scheduling.Detail]))
    var paymentInstrument: PaymentInstrument = _

    @BeanProperty
    @NotNull(groups = Array(classOf[Create], classOf[Update]))
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonView(Array(classOf[Views.Scheduling.Detail]))
    var user: UserDetail = _

    @BeanProperty
    @ManyToOne
    @JoinColumn(name = "authorized_member_id")
    @JsonView(Array(classOf[Views.Scheduling.Detail]))
    var authorizedMember: AuthorizedMember = _

    @BeanProperty
    @Column(name = "expiration_date")
    @JsonView(Array(classOf[Views.Scheduling.List]))
    var expirationDate: Date = _

    @BeanProperty
    @Column(name = "cancelation_date")
    @JsonView(Array(classOf[Views.Scheduling.List]))
    var cancelationDate: Date = _

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

    def cancelMe() = {
        if(this.cancelationDate == null) {
            this.cancelationDate = new Date()
        }
    }


    @PrePersist
    def prePersist(): Unit = this.createdDateTime = new Date

    def hasAuthorizedMember: Boolean = this.authorizedMember != null && this.authorizedMember.getId != null

}
