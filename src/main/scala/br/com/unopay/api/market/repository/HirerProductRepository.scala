package br.com.unopay.api.market.repository

import java.util.Optional

import br.com.unopay.api.market.model.HirerProduct
import br.com.unopay.api.market.model.filter.HirerProductFilter
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository

trait HirerProductRepository extends UnovationFilterRepository[HirerProduct, String, HirerProductFilter] {

  def findById(id:String) : Optional[HirerProduct]

}
