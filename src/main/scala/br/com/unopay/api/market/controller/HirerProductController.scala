package br.com.unopay.api.market.controller

import br.com.unopay.api.market.model.HirerProduct
import br.com.unopay.api.market.model.filter.HirerProductFilter
import br.com.unopay.api.market.service.HirerProductService
import br.com.unopay.api.model.validation.group.{Create, Update, Views}
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.util.Logging
import br.com.unopay.bootcommons.jsoncollections.{PageableResults, Results, UnovationPageRequest}
import br.com.unopay.bootcommons.stopwatch.annotation.Timed
import com.fasterxml.jackson.annotation.JsonView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation._
import org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequestUri

@RestController
@Timed(prefix = "api")
@Autowired
class HirerProductController(service: HirerProductService) extends Logging {

    @JsonView(Array(classOf[Views.HirerProduct.Detail]))
    @PreAuthorize("hasRole('ROLE_MANAGE_HIRER_PRODUCT')")
    @PostMapping(value = Array("/hirer-products"))
    def process(@Validated(Array(classOf[Create])) @RequestBody hirerProduct: HirerProduct): ResponseEntity[HirerProduct] = {
        log.info("processing hirer products")
        val created = service.create(hirerProduct)
        val uri = buildUriLocation(created.id)
        ResponseEntity.created(uri).body(created)
    }

    @JsonView(Array(classOf[Views.HirerProduct.Detail]))
    @PreAuthorize("hasRole('ROLE_LIST_HIRER_PRODUCT')")
    @GetMapping(value = Array("/hirer-products/{id}"))
    def get(@PathVariable id: String): HirerProduct = {
        log.info("get hirer product={}", id)
        service.findById(id)
    }

    @JsonView(Array(classOf[Views.HirerProduct.Detail]))
    @PreAuthorize("hasRole('ROLE_MANAGE_HIRER_PRODUCT')")
    @DeleteMapping(value = Array("/hirer-products/{id}"))
    def delete(@PathVariable id: String) {
        log.info("get hirer product={}", id)
        service.deleteById(id)
    }

    @PutMapping(Array("/hirer-products/{id}"))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_HIRER_PRODUCT')")
    def update (@PathVariable id: String, @RequestBody @Validated(Array(classOf[Update])) hirerProduct: HirerProduct): Unit = {
      log.info("updating HirerProduct with id={}", id)
      service.update(id, hirerProduct)
    }


    @JsonView(Array(classOf[Views.HirerProduct.List]))
    @PreAuthorize("hasRole('ROLE_LIST_HIRER_PRODUCT') || #oauth2.isClient()")
    @GetMapping(value = Array("/hirer-products"))
    def getByParams(filter: HirerProductFilter,
                    @Validated pageable: UnovationPageRequest): Results[HirerProduct] = {
        log.info("search hirer product with filter={}", filter)
        val page =  service.findByFilter(filter, pageable)
        pageable.setTotal(page.getTotalElements())
        PageableResults.create(pageable, page.getContent, fromCurrentRequestUri().toUriString)
    }

    private def buildUriLocation(id: String) = {
        fromCurrentRequestUri()
          .path("/{id}")
          .buildAndExpand(id)
          .toUri
    }
}
