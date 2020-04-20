package br.com.unopay.api.scheduling.model.filter

import java.lang.Long
import java.util.Date

import br.com.unopay.bootcommons.model.Period
import br.com.unopay.bootcommons.repository.filter.SearchableField

import scala.beans.BeanProperty

class SchedulingFilter {

    @BeanProperty
    @SearchableField
    var token: String = _

    @BeanProperty
    var createdDateTime: Date = _

    @BeanProperty
    @SearchableField
    var date: Period = _

    @BeanProperty
    @SearchableField(field = "branch.name")
    var branch: String = _

    @BeanProperty
    @SearchableField(field = "branch.headOffice.network.id")
    var network: String = _

    @BeanProperty
    @SearchableField(field = "contractor.id")
    var contractor: String = _

    @BeanProperty
    @SearchableField(field = "contract.code")
    var contract: Long = _

    @BeanProperty
    @SearchableField(field = "contractor.person.document.number")
    var contractorDocument: String = _

    override def toString: String = s"SchedulingFilter(token=$token, createdDateTime=$createdDateTime, " +
            s"date=$date, branch=$branch, network=$network, contractor=$contractor, contract=$contract, " +
            s"contractorDocument=$contractorDocument)"
}
