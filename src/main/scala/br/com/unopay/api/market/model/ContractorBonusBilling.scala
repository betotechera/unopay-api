package br.com.unopay.api.market.model

import java.io.Serializable
import javax.persistence._
import javax.validation.Valid

import lombok.Data
import org.hibernate.annotations.GenericGenerator

import scala.beans.BeanProperty


@Data
@Entity
@Table(name = "contractor_bonus_billing")
class ContractorBonusBilling extends Serializable {

    @Id
    @BeanProperty
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    var id: String = _

    @Valid
    @BeanProperty
    @ManyToOne
    @JoinColumn(name = "contractor_bonus_id")
    var contractorBonus: ContractorBonus = _

    @Valid
    @BeanProperty
    @ManyToOne
    @JoinColumn(name = "bonus_billing_id")
    var bonusBilling: BonusBilling = _
}
