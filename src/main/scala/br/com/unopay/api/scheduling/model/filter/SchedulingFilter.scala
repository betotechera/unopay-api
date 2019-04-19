package br.com.unopay.api.scheduling.model.filter

import java.util.Date

import br.com.unopay.bootcommons.repository.filter.SearchableField
import lombok.Data
import org.springframework.core.annotation.AliasFor

import scala.beans.BeanProperty

@Data
class SchedulingFilter {

    @BeanProperty
    var token: String = _

    var createdDateTime: Date = _

    @SearchableField(field = "branch.name")
    var branch: String = _

    @SearchableField(field = "branch.headOffice.network.id")
    var network: String = _

    @SearchableField(field = "contractor.id")
    var contractor: String = _

    @SearchableField(field = "contract.code")
    var contract: Long = _

    @SearchableField(field = "contractor.person.document.number")
    var contractorDocument: String = _
}
