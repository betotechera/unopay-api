package br.com.unopay.api.market.repository

import br.com.unopay.api.market.model.ContractorBonusBilling
import org.springframework.data.repository.CrudRepository

trait ContractorBonusBillingRepository extends  CrudRepository[ContractorBonusBilling, String]{
      def findByContractorBonusId(id: String): java.util.List[ContractorBonusBilling]
      def findByBonusBillingId(id: String): java.util.List[ContractorBonusBilling]
}
