package br.com.unopay.api.market.repository

import br.com.unopay.api.market.model.BonusBilling
import br.com.unopay.api.market.model.filter.BonusBillingFilter
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository

import java.util.Optional

trait BonusBillingRepository extends UnovationFilterRepository[BonusBilling, String, BonusBillingFilter] {

    def findById(id: String): Optional[BonusBilling]
    def findByIdAndPayerId(id: String, payerId: String): Optional[BonusBilling]
    def findFirstByOrderByCreatedDateTimeDesc(): Optional[BonusBilling]
}
