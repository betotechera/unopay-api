package br.com.unopay.api.market.service

import br.com.six2six.fixturefactory.Fixture.from
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.UnopayApiScalaApplicationTest
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.model.Product
import br.com.unopay.api.market.model.HirerProduct
import br.com.unopay.bootcommons.exception.{NotFoundException, UnprocessableEntityException}
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConverters._


class HirerProductServiceTest extends UnopayApiScalaApplicationTest  {

  @Autowired
  var service: HirerProductService = _

  it should "save a valid hirer product" in {
    val hirerProduct :HirerProduct = fixtureCreator.validHirerProduct()
    val result = service.save(hirerProduct)
    result should be
  }

  it should "create a valid hirer product" in {
    val hirerProduct: HirerProduct = fixtureCreator.validHirerProduct()

    val created = service.create(hirerProduct)
    val result = service.findById(created.id)
    result should be
  }

  "given it without hirer" should "not be created" in {
    val hirerProduct: HirerProduct = from(classOf[HirerProduct]).gimme("valid", new Rule(){{
      add("hirer", null)
    }})
    val thrown = the[UnprocessableEntityException] thrownBy {
      service.create(hirerProduct)
    }

    thrown.getErrors.asScala.head.getLogref shouldEqual "HIRER_REQUIRED"
  }
  "given it with an unknown hirer" should "not be created" in {
    val hirerProduct: HirerProduct = fixtureCreator.validHirerProduct()
    hirerProduct.hirer = new Hirer

    val thrown = the[NotFoundException] thrownBy {
      service.create(hirerProduct)
    }

    thrown.getErrors.asScala.head.getLogref shouldEqual "HIRER_NOT_FOUND"
  }

  "given it without product" should "not be created" in {
    val hirerProduct: HirerProduct = from(classOf[HirerProduct]).gimme("valid", new Rule(){{
      add("product", null)
    }})
    val thrown = the[UnprocessableEntityException] thrownBy {
      service.create(hirerProduct)
    }

    thrown.getErrors.asScala.head.getLogref shouldEqual "PRODUCT_REQUIRED"
  }

  "given it with an unknown product" should "not be created" in {
    val hirerProduct: HirerProduct = fixtureCreator.validHirerProduct()
    hirerProduct.product = new Product

    val thrown = the[NotFoundException] thrownBy {
      service.create(hirerProduct)
    }

    thrown.getErrors.asScala.head.getLogref shouldEqual "PRODUCT_NOT_FOUND"
  }

  "when find an unknown hirer product it" should "not be found" in {
    val thrown = the[NotFoundException] thrownBy {
      service.findById("")
    }

    thrown.getErrors.asScala.head.getLogref shouldEqual "HIRER_PRODUCT_NOT_FOUND"
  }

  "when find an unknown hirer product it" should "not be deleted" in {
    val thrown = the[NotFoundException] thrownBy {
      service.deleteById("")
    }

    thrown.getErrors.asScala.head.getLogref shouldEqual "HIRER_PRODUCT_NOT_FOUND"
  }




}
