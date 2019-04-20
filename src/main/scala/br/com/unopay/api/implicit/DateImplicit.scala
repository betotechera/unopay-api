package br.com.unopay.api.`implicit`

import java.time.temporal.ChronoUnit
import java.util.Date

object DateImplicit {

    implicit class DateImplicit(date: Date) {
        def plusDays(days: Int) : Date = {
            Date.from(date.toInstant.plus(days, ChronoUnit.DAYS))
        }
    }

}
