package br.com.unopay.api.scheduling.repository

import java.util.Optional

import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.model.filter.SchedulingFilter
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository

trait SchedulingRepository extends UnovationFilterRepository[Scheduling, String, SchedulingFilter]{

    def findById(id: String): Optional[Scheduling]

    def findByIdAndBranchHeadOfficeNetworkId(id: String, networkId: String): Optional[Scheduling]

    def findByIdAndContractorId(id: String, networkId: String): Optional[Scheduling]

}
