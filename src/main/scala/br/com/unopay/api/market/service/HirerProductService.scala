package br.com.unopay.api.market.service

import br.com.unopay.api.bacen.service.HirerService
import br.com.unopay.api.market.model.HirerProduct
import br.com.unopay.api.market.model.filter.HirerProductFilter
import br.com.unopay.api.market.repository.HirerProductRepository
import br.com.unopay.api.service.ProductService
import br.com.unopay.api.uaa.exception.Errors
import br.com.unopay.bootcommons.exception.UnovationExceptions
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.{Page, PageRequest}
import org.springframework.stereotype.Service


@Service
@Autowired
class HirerProductService(repository: HirerProductRepository, productService: ProductService, hirerService: HirerService) {

  def deleteById(id: String): Unit = {
    val current = findById(id)
    repository.delete(current)
  }

  def findById(id: String) = {
    repository.findById(id).orElseThrow(() => UnovationExceptions.notFound().withErrors(Errors.HIRER_PRODUCT_NOT_FOUND))
  }

  def create(hirerProduct: HirerProduct) = {
    hirerProduct.validate()
    defineValidReferences(hirerProduct)
    save(hirerProduct)
  }

  def save(hirerProduct: HirerProduct) = {
    repository.save(hirerProduct)
  }

  def findByFilter(filter: HirerProductFilter, pageable: UnovationPageRequest): Page[HirerProduct] = {
    repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()))
  }


  private def defineValidReferences(hirerProduct: HirerProduct) = {
    hirerProduct.setHirer(hirerService.getById(hirerProduct.hirer.getId))
    hirerProduct.setProduct(productService.findById(hirerProduct.product.getId))
  }

}
