package br.com.unopay.api.credit.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.model.InstrumentBalance
import br.com.unopay.api.util.Rounder
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.joda.time.DateTimeComparator
import org.joda.time.DateTimeFieldType
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class InstrumentBalanceServiceTest  extends SpockApplicationTests {

    @Autowired
    InstrumentBalanceService service

    @Autowired
    FixtureCreator fixtureCreator

    def 'given valid balance should be create'(){
        given:
        InstrumentBalance balance = Fixture.from(InstrumentBalance.class).gimme("valid", new Rule(){{
            add("paymentInstrument", fixtureCreator.createInstrumentToProduct())
        }})

        when:
        InstrumentBalance created = service.save(balance)
        InstrumentBalance result = service.findBydId(created.id)

        then:
        result != null
    }

    def 'when subtract value in instrument without balance should return error'(){
        given:
        def instrument = fixtureCreator.createInstrumentToProduct()
        BigDecimal value = Rounder.round((Math.random() * 1000))

        when:
        service.subtract(instrument.id, value)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'INSTRUMENT_BALANCE_NOT_FOUND'
    }

    def 'when subtract value in unknown instrument should return error'(){
        given:
        BigDecimal value = Rounder.round((Math.random() * 1000))

        when:
        service.subtract('', value)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'INSTRUMENT_BALANCE_NOT_FOUND'
    }

    def 'when subtract value greater than balance should return error'(){
        given:
        def instrument = fixtureCreator.createInstrumentToProduct()
        BigDecimal value = Rounder.round((Math.random() * 1000))
        service.add(instrument.id, value)

        when:
        service.subtract(instrument.id, value + 0.01)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'BALANCE_LESS_THAN_REQUIRED'
    }

    @Unroll
    'when subtract value less than or equals balance should be subtracted'(){
        given:
        def instrument = fixtureCreator.createInstrumentToProduct()
        service.add(instrument.id, 20.0)

        when:
        service.subtract(instrument.id, value)
        InstrumentBalance balance = service.findByInstrumentId(instrument.id)

        then:
        balance.value == expected

        where:
        value || expected
        20.0  || 0.0
        15.0  || 5.0
        0.1   || 19.9
    }

    def 'when add value in instrument without balance should create balance with value'(){
        given:
        def instrument = fixtureCreator.createInstrumentToProduct()
        BigDecimal value = Rounder.round((Math.random() * 1000))

        when:
        service.add(instrument.id, value)

        InstrumentBalance balance = service.findByInstrumentId(instrument.id)

        then:
        balance.value == value
    }

    def 'when add value in instrument without balance should create balance with instrument document number'(){
        given:
        def instrument = fixtureCreator.createInstrumentToProduct()
        BigDecimal value = Rounder.round((Math.random() * 1000))

        when:
        service.add(instrument.id, value)

        InstrumentBalance balance = service.findByInstrumentId(instrument.id)

        then:
        balance.documentNumber == instrument.contractor.documentNumber
    }

    def 'when add value in instrument with known balance should add value in balance'(){
        given:
        def instrument = fixtureCreator.createInstrumentToProduct()
        BigDecimal value = Rounder.round(Math.random() * 1000)
        service.add(instrument.id, value)

        when:
        service.add(instrument.id, value)

        InstrumentBalance balance = service.findByInstrumentId(instrument.id)

        then:
        balance.value == value + value
    }

    def 'when add value in instrument with known balance should update  updated date time'(){
        given:
        def instrument = fixtureCreator.createInstrumentToProduct()
        BigDecimal value = (Math.random() * 1000)
        service.add(instrument.id, value)

        when:
        service.add(instrument.id, value)

        InstrumentBalance balance = service.findByInstrumentId(instrument.id)

        then:
        DateTimeComparator comparator = DateTimeComparator.getInstance(DateTimeFieldType.secondOfDay())
        comparator.compare(balance.updatedDateTime,new Date()) == 0
    }

    def 'when add value in instrument without balance should create balance with created date'(){
        given:
        def instrument = fixtureCreator.createInstrumentToProduct()
        BigDecimal value = (Math.random() * 1000)

        when:
        service.add(instrument.id, value)

        InstrumentBalance balance = service.findByInstrumentId(instrument.id)

        then:
        DateTimeComparator comparator = DateTimeComparator.getInstance(DateTimeFieldType.secondOfDay())
        comparator.compare(balance.createdDateTime,new Date()) == 0
    }

    def 'when add value in instrument without balance should update updated date'(){
        given:
        def instrument = fixtureCreator.createInstrumentToProduct()
        BigDecimal value = (Math.random() * 1000)

        when:
        service.add(instrument.id, value)

        InstrumentBalance balance = service.findByInstrumentId(instrument.id)

        then:
        DateTimeComparator comparator = DateTimeComparator.getInstance(DateTimeFieldType.secondOfDay())
        comparator.compare(balance.updatedDateTime,new Date()) == 0
    }

    def 'when find balance with known document and instrument should be found'(){
        given:
        def instrument = fixtureCreator.createInstrumentToProduct()
        BigDecimal value = (Math.random() * 1000)

        when:
        service.add(instrument.id, value)

        InstrumentBalance found = service.findByInstrumentIdAndDocument(instrument.id, instrument.documentNumber())

        then:
        found
    }

    def 'when find balance with unknown document and known instrument should not be found'(){
        given:
        def instrument = fixtureCreator.createInstrumentToProduct()
        BigDecimal value = (Math.random() * 1000)

        when:
        service.add(instrument.id, value)

        service.findByInstrumentIdAndDocument(instrument.id, '')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'INSTRUMENT_BALANCE_NOT_FOUND'
    }

    def 'when find balance with known document and unknown instrument should not be found'(){
        given:
        def instrument = fixtureCreator.createInstrumentToProduct()
        BigDecimal value = (Math.random() * 1000)

        when:
        service.add(instrument.id, value)

        service.findByInstrumentIdAndDocument('', instrument.documentNumber())

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'INSTRUMENT_BALANCE_NOT_FOUND'
    }

    def 'when add value in unknown instrument should return error'(){
        when:
        service.add('', 20.0)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_INSTRUMENT_NOT_FOUND'
    }

    def 'when find balance by instrument with unknown instrument should return error'(){
        when:
        service.findByInstrumentId('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'INSTRUMENT_BALANCE_NOT_FOUND'
    }
}
