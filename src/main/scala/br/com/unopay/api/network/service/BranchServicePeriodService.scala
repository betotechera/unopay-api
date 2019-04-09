package br.com.unopay.api.network.service

import java.util
import java.util.Date

import br.com.unopay.api.network.model.BranchServicePeriod
import br.com.unopay.api.network.repository.BranchServicePeriodRepository
import br.com.unopay.api.uaa.exception.Errors
import br.com.unopay.api.util.Logging
import br.com.unopay.bootcommons.exception.UnovationExceptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._

@Service
@Autowired
class BranchServicePeriodService(branchServicePeriodRepository: BranchServicePeriodRepository) extends Logging {

  def update(id: String, servicePeriod: BranchServicePeriod): BranchServicePeriod  = {
    val current = branchServicePeriodRepository.findById(id).orElseThrow(() => UnovationExceptions.notFound())
    current.updateMe(servicePeriod)
    branchServicePeriodRepository.save(current)
  }

  def create(servicePeriods: java.util.Collection[BranchServicePeriod]): util.Set[BranchServicePeriod] = {
    checkUniquesPeriods(servicePeriods)
    servicePeriods.asScala.map(period => create(period)).toSet.asJava
  }

  private def checkUniquesPeriods(servicePeriods: util.Collection[BranchServicePeriod]) = {
    val grouped = servicePeriods.asScala.groupBy(_.weekday)
    val result = grouped.filter(_._2.size >= 2).map(period => {
      UnovationExceptions.conflict().withErrors(Errors.PERIOD_ALREADY_REGISTERED.withOnlyArgument(period._1))
    })
    if(result.nonEmpty){
      throw result.head
    }
  }

  def create(servicePeriod: BranchServicePeriod): BranchServicePeriod = {
    servicePeriod.createdDateTime = new Date()
    branchServicePeriodRepository.save(servicePeriod)
  }

}
