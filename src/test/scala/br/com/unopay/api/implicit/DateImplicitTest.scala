package br.com.unopay.api.`implicit`

import java.time.{LocalDateTime, Month, ZoneId, ZoneOffset}
import java.util.{Calendar, Date}

import br.com.unopay.api.ScalaFixtureTest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DateImplicitTest extends ScalaFixtureTest {

    it should "plus 10 days in Date" in {

        val localDateTime = LocalDateTime.of(2019, Month.APRIL, 29, 1, 0, 0)
        val date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant)

        val dateImplicit = new DateImplicit.DateImplicit(date)

        val newDateWithPlus = dateImplicit.plusDays(10)
        val newLocalDateWithPlus = LocalDateTime.ofInstant(newDateWithPlus.toInstant, ZoneId.systemDefault())

        assert(newLocalDateWithPlus.getDayOfMonth == 9)
        assert(newLocalDateWithPlus.getMonth == Month.MAY)
        assert(newLocalDateWithPlus.getYear == 2019)
        assert(newLocalDateWithPlus.getHour == 1)
        assert(newLocalDateWithPlus.getMinute == 0)
        assert(newLocalDateWithPlus.getSecond == 0)

    }
}
