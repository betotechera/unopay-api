package br.com.unopay.api

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader
import org.joda.time.DateTimeComparator
import org.joda.time.DateTimeFieldType
import spock.lang.Specification

class FixtureApplicationTest extends Specification {

    DateTimeComparator timeComparator = DateTimeComparator.getInstance(DateTimeFieldType.minuteOfHour())

    void setup(){
        FixtureFactoryLoader.loadTemplates("br.com.unopay.api")
    }
}
