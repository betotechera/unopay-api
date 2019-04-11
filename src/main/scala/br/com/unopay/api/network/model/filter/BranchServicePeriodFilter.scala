package br.com.unopay.api.network.model.filter

import br.com.wingoo.reusable.repository.filter.SearchableField

import scala.beans.BeanProperty

class BranchServicePeriodFilter {

  @BeanProperty
  @SearchableField(field = "branch.id")
  var branch: String = _

}
