package br.com.unopay.api.network.repository

import java.util.Optional

import br.com.unopay.api.network.model.BranchServicePeriod
import br.com.unopay.api.network.model.filter.BranchServicePeriodFilter
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository

trait BranchServicePeriodRepository extends UnovationFilterRepository[BranchServicePeriod, String, BranchServicePeriodFilter] {

    def findById(id: String): Optional[BranchServicePeriod]
}
