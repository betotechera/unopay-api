package br.com.unopay.api.network.service

import br.com.six2six.fixturefactory.{Fixture, Rule}
import br.com.unopay.api.ScalaApplicationTest
import br.com.unopay.api.network.model.ServicePeriodSituation.SUSPENDED
import br.com.unopay.api.network.model.{Branch, BranchServicePeriod, Weekday}
import br.com.unopay.api.network.repository.BranchServicePeriodRepository
import br.com.unopay.bootcommons.exception.ConflictException
import org.scalatest.mockito.MockitoSugar
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConverters._


class BranchServicePeriodTest extends ScalaApplicationTest with MockitoSugar {

  @Autowired
  var service: BranchServicePeriodService = _
  @Autowired
  var repository: BranchServicePeriodRepository = _
  var branchUnderTest: Branch = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    branchUnderTest = Fixture.from(classOf[Branch]).uses(jpaProcessor).gimme("valid")
  }

  "given periods with uniques weekdays" should "be created" in {
    val sunday: BranchServicePeriod = Fixture.from(classOf[BranchServicePeriod]).gimme("valid", new Rule(){{
      add("weekday", Weekday.SUNDAY)
      add("branch", branchUnderTest)
    }})
    val monday: BranchServicePeriod = Fixture.from(classOf[BranchServicePeriod]).gimme("valid", new Rule(){{
      add("weekday", Weekday.MONDAY)
      add("branch", branchUnderTest)
    }})
    val tuesday: BranchServicePeriod = Fixture.from(classOf[BranchServicePeriod]).gimme("valid", new Rule(){{
      add("weekday", Weekday.TUESDAY)
      add("branch", branchUnderTest)
    }})
    val wednesday: BranchServicePeriod = Fixture.from(classOf[BranchServicePeriod]).gimme("valid", new Rule(){{
      add("weekday", Weekday.WEDNESDAY)
      add("branch", branchUnderTest)
    }})
    val thursday: BranchServicePeriod = Fixture.from(classOf[BranchServicePeriod]).gimme("valid", new Rule(){{
      add("weekday", Weekday.THURSDAY)
      add("branch", branchUnderTest)
    }})
    val friday: BranchServicePeriod = Fixture.from(classOf[BranchServicePeriod]).gimme("valid", new Rule(){{
      add("weekday", Weekday.FRIDAY)
      add("branch", branchUnderTest)
    }})
    val saturday: BranchServicePeriod = Fixture.from(classOf[BranchServicePeriod]).gimme("valid", new Rule(){{
      add("weekday", Weekday.SATURDAY)
      add("branch", branchUnderTest)
    }})
    val periods = List(sunday,monday, tuesday, wednesday, thursday, friday, saturday).asJavaCollection

    service.create(periods)
    val result = repository.count()
    result should be(7)
  }

  "given periods with non-uniques weekdays" should "not be created" in {
    val sunday: BranchServicePeriod = Fixture.from(classOf[BranchServicePeriod]).gimme("valid", new Rule(){{
      add("weekday", Weekday.SUNDAY)
      add("branch", branchUnderTest)
    }})
    val monday: BranchServicePeriod = Fixture.from(classOf[BranchServicePeriod]).gimme("valid", new Rule(){{
      add("weekday", Weekday.MONDAY)
      add("branch", branchUnderTest)
    }})
    val tuesday: BranchServicePeriod = Fixture.from(classOf[BranchServicePeriod]).gimme("valid", new Rule(){{
      add("weekday", Weekday.TUESDAY)
      add("branch", branchUnderTest)
    }})
    val wednesday: BranchServicePeriod = Fixture.from(classOf[BranchServicePeriod]).gimme("valid", new Rule(){{
      add("weekday", Weekday.WEDNESDAY)
      add("branch", branchUnderTest)
    }})
    val thursday: BranchServicePeriod = Fixture.from(classOf[BranchServicePeriod]).gimme("valid", new Rule(){{
      add("weekday", Weekday.WEDNESDAY)
      add("branch", branchUnderTest)
    }})
    val periods = List(sunday,monday, tuesday, wednesday, thursday).asJavaCollection

    val thrown = the[ConflictException] thrownBy {
      service.create(periods)
    }
    thrown.getErrors.asScala.head.getLogref should be("PERIOD_ALREADY_REGISTERED")
    thrown.getErrors.asScala.head.getArguments should contain("WEDNESDAY")
  }


  it should "save a valid branch service period" in{
    val servicePeriod: BranchServicePeriod = Fixture.from(classOf[BranchServicePeriod]).gimme("valid", new Rule(){{
      add("branch", branchUnderTest)
    }})
    val result = service.create(servicePeriod)
    result should be
  }

  it should "update a valid branch service period" in{
    val servicePeriod: BranchServicePeriod = Fixture.from(classOf[BranchServicePeriod]).uses(jpaProcessor).gimme("valid", new Rule(){{
      add("branch", branchUnderTest)
    }})
    servicePeriod.setSituation(SUSPENDED)

    val result = service.update(servicePeriod.id, servicePeriod)
    result.situation should be(SUSPENDED)
  }

}

