package br.com.unopay.api.network.model

import java.io.Serializable
import java.util.Date

import br.com.unopay.api.model.Updatable
import br.com.unopay.api.model.validation.group.{Create, Update, Views}
import com.fasterxml.jackson.annotation.JsonView
import javax.persistence._
import javax.validation.constraints.NotNull
import org.codehaus.jackson.annotate.{JsonBackReference, JsonIgnore}
import org.hibernate.annotations.GenericGenerator

import scala.beans.BeanProperty

@Entity
@Table(name = "branch_service_period")
class BranchServicePeriod extends Serializable with Updatable {

  @Id
  @BeanProperty
  @Column(name = "id")
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid2")
  var id: String = _

  @BeanProperty
  @JsonBackReference
  @ManyToOne
  @JoinColumn(name = "branch_id")
  var branch: Branch = _

  @BeanProperty
  @NotNull(groups = Array(classOf[Create], classOf[Update]))
  @Temporal(TemporalType.TIME)
  @Column(name = "begin_service_time")
  @JsonView(Array(classOf[Views.BranchServicePeriod.List]))
  var beginServiceTime: Date = _

  @BeanProperty
  @Temporal(TemporalType.TIME)
  @NotNull(groups = Array(classOf[Create], classOf[Update]))
  @Column(name = "end_service_time")
  @JsonView(Array(classOf[Views.BranchServicePeriod.List]))
  var endServiceTime: Date = _

  @Column(name = "situation")
  @BeanProperty
  @Enumerated(EnumType.STRING)
  @JsonView(Array(classOf[Views.BranchServicePeriod.List]))
  var situation: ServicePeriodSituation = _

  @Column(name = "weekday")
  @BeanProperty
  @Enumerated(EnumType.STRING)
  @JsonView(Array(classOf[Views.BranchServicePeriod.List]))
  var weekday: Weekday = _

  @BeanProperty
  @Column(name = "created_date_time")
  @JsonView(Array(classOf[Views.BranchServicePeriod.List]))
  var createdDateTime: Date = _

  @Version
  @JsonIgnore
  var version: java.lang.Long = _




}
