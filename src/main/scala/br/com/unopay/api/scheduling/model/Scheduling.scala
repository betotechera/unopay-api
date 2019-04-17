package br.com.unopay.api.scheduling.model

import java.io.Serializable
import java.time.LocalDateTime

import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.market.model.AuthorizedMember
import br.com.unopay.api.model.{Contract, PaymentInstrument, Updatable}
import br.com.unopay.api.network.model.Branch
import br.com.unopay.api.uaa.model.UserDetail
import javax.persistence._
import javax.validation.constraints.NotNull
import lombok.{EqualsAndHashCode, Getter}
import org.hibernate.annotations.GenericGenerator

import scala.beans.BeanProperty

@SerialVersionUID(1L)
@EqualsAndHashCode(of = Array ("id"))
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
    var token: String = _

    @BeanProperty
    @Column(name = "created_date_time")
    var createdDateTime: LocalDateTime= _

    @BeanProperty
    @OneToOne
    @JoinColumn(name = "branch_id")
    var branch: Branch = _

    @BeanProperty
    @ManyToOne
    @JoinColumn(name = "contract_id")
    var contract: Contract = _

    @BeanProperty
    @NotNull
    @ManyToOne
    @JoinColumn(name = "contractor_id")
    var contractor: Contractor = _

    @BeanProperty
    @ManyToOne
    @JoinColumn(name = "payment_instrument_id")
    var paymentInstrument: PaymentInstrument = _

    @BeanProperty
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: UserDetail = _

    @BeanProperty
    @ManyToOne
    @JoinColumn(name = "authorized_member_id")
    var authorizedMember: AuthorizedMember = _

    @PrePersist
    def prePersist(): Unit = {
        this.createdDateTime = LocalDateTime.now()
    }
}
