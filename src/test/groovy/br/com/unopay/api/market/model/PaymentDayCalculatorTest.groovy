package br.com.unopay.api.market.model

import br.com.unopay.api.FixtureApplicationTest
import org.joda.time.DateTime

class PaymentDayCalculatorTest extends FixtureApplicationTest {

    def 'given a current date after twenty eight day should return first day of next month'(){
        given:
        def currentDate = new DateTime().withDayOfMonth(afterDate).withMillisOfDay(0)

        when:
        def nearDate = new PaymentDayCalculator(currentDate, 3).getNearDate()

        then:
        def expectedDay = new DateTime().plusMonths(1).withDayOfMonth(1).withMillisOfDay(0).toDate()
        timeComparator.compare(expectedDay, nearDate) == 0

        where:
        _ | afterDate
        _ | 26
        _ | 27
        _ | 28
    }

    def 'given a current date before twenty eight day should return the ticket deadline day'(){
        given:
        def currentDate = new DateTime().withDayOfMonth(beforeDate).withMillisOfDay(0)

        when:
        def nearDate = new PaymentDayCalculator(currentDate, 3).getNearDate()

        then:
        timeComparator.compare(currentDate.plusDays(3), nearDate) == 0

        where:
        _ | beforeDate
        _ | 23
        _ | 24
        _ | 25
    }
}
