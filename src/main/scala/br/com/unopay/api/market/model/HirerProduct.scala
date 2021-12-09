package br.com.unopay.api.market.model

import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.model.validation.group.{Create, Update, Views}
import br.com.unopay.api.model.{Product, Updatable}
import br.com.unopay.api.uaa.exception.Errors
import br.com.unopay.bootcommons.exception.UnovationExceptions
import com.fasterxml.jackson.annotation.JsonView
import lombok.Data
import org.hibernate.annotations.GenericGenerator

import java.io.Serializable
import java.util.Date
import javax.persistence._
import scala.beans.BeanProperty

@Data
@Entity
@Table(name = "hirer_product")
class HirerProduct extends Serializable with Updatable {

  @Id
  @BeanProperty
  @Column(name = "id")
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid2")
  var id: String = _

  @BeanProperty
  @Column(name = "created_date_time")
  @JsonView(Array(classOf[Views.HirerProduct.Detail]))
  var createdDateTime: Date = _

  @BeanProperty
  @ManyToOne
  @JoinColumn(name = "product_id")
  @NotNull(groups = Array(classOf[Create], classOf[Update]))
  @JsonView(Array(classOf[Views.HirerProduct.List]))
  var product: Product = _

  @BeanProperty
  @ManyToOne
  @JoinColumn(name = "hirer_id")
  @NotNull(groups = Array(classOf[Create], classOf[Update]))
  @JsonView(Array(classOf[Views.HirerProduct.List]))
  var hirer: Hirer = _


  @Column(name = "expiration")
  @BeanProperty
  @JsonView(Array(classOf[Views.HirerProduct.List]))
  var expiration: Date = _


  def validate() = {
    if (hirer == null) {
      throw UnovationExceptions.unprocessableEntity().withErrors(Errors.HIRER_REQUIRED)
    }
    if (product == null) {
      throw UnovationExceptions.unprocessableEntity().withErrors(Errors.PRODUCT_REQUIRED)
    }
  }

}
