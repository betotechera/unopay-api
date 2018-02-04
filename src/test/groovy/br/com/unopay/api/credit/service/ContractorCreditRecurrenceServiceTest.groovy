package br.com.unopay.api.credit.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.model.ContractorCreditRecurrence
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.PaymentInstrumentType
import br.com.unopay.api.service.PaymentInstrumentService
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class ContractorCreditRecurrenceServiceTest  extends SpockApplicationTests {

    @Autowired
    private ContractorCreditRecurrenceService service
    @Autowired
    private FixtureCreator fixtureCreator
    @Autowired
    private PaymentInstrumentService paymentInstrumentService

    Contract digitalWalletContract

    def setup(){
        digitalWalletContract = fixtureCreator.createPersistedContract()
        Fixture.from(PaymentInstrument.class).uses(jpaProcessor).gimme("valid", new Rule() {{
                add("product", digitalWalletContract.product)
                add("contractor", digitalWalletContract.contractor)
                add("type", PaymentInstrumentType.DIGITAL_WALLET)
        }})
    }

    def 'given a valid recurrence should be created'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        def instrument = fixtureCreator.createInstrumentToProduct(contract.product)
        ContractorCreditRecurrence recurrence = Fixture.from(ContractorCreditRecurrence).gimme("valid", new Rule(){{
            add("contract", contract)
            add("hirer", contract.hirer)
            add("paymentInstrument", instrument)
        }})

        when:
        ContractorCreditRecurrence created = service.save(recurrence)
        ContractorCreditRecurrence found = service.findById(created.id)

        then:
        found
    }

    def 'given a credit recurrence with unknown instrument should return error'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        def instrument = fixtureCreator.createPaymentInstrument()
        ContractorCreditRecurrence recurrence = Fixture.from(ContractorCreditRecurrence).gimme("valid", new Rule(){{
            add("contract", contract)
            add("hirer", contract.hirer)
            add("paymentInstrument", instrument.with { id = ''; it })
        }})

        when:
        service.create(recurrence)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_INSTRUMENT_NOT_FOUND'
    }

    def 'given a credit recurrence with unknown hirer should return error'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        def instrument = fixtureCreator.createInstrumentToProduct(contract.product)
        ContractorCreditRecurrence recurrence = Fixture.from(ContractorCreditRecurrence).gimme("valid", new Rule(){{
            add("contract", contract)
            add("hirer", contract.hirer.with { id = ''; it })
            add("paymentInstrument", instrument)
        }})

        when:
        service.create(recurrence)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_NOT_FOUND'
    }

    def 'given a credit recurrence with unknown contract should return error'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        def instrument = fixtureCreator.createInstrumentToProduct(contract.product)
        ContractorCreditRecurrence recurrence = Fixture.from(ContractorCreditRecurrence).gimme("valid", new Rule(){{
            add("contract", contract.with { id = ''; it})
            add("hirer", contract.hirer)
            add("paymentInstrument", instrument)
        }})

        when:
        service.create(recurrence)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    def 'given a recurrence  without created date should be created'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        def instrument = fixtureCreator.createInstrumentToProduct(contract.product)
        ContractorCreditRecurrence recurrence = Fixture.from(ContractorCreditRecurrence).gimme("valid", new Rule(){{
            add("contract", contract)
            add("hirer", contract.hirer)
            add("createdDateTime", null)
            add("paymentInstrument", instrument)
        }})

        when:
        ContractorCreditRecurrence created = service.create(recurrence)
        ContractorCreditRecurrence found = service.findById(created.id)

        then:
        timeComparator.compare(found.createdDateTime, new Date()) == 0
    }

    def 'given a credit recurrence without instrument should be created with digital wallet instrument'(){
        given:
        ContractorCreditRecurrence recurrence = Fixture.from(ContractorCreditRecurrence).gimme("valid", new Rule(){{
            add("contract", digitalWalletContract)
            add("hirer", digitalWalletContract.hirer)
            add("paymentInstrument", null)
        }})

        when:
        ContractorCreditRecurrence created = service.create(recurrence)
        ContractorCreditRecurrence found = service.findById(created.id)

        then:
        PaymentInstrumentType.DIGITAL_WALLET == found.paymentInstrument.type
    }

    def 'given a credit recurrence without instrument should be created with contract contractor instrument'(){
        given:
        def instruments = paymentInstrumentService.findByContractorId(digitalWalletContract.contractor.id)
        ContractorCreditRecurrence recurrence = Fixture.from(ContractorCreditRecurrence).gimme("valid", new Rule(){{
            add("contract", digitalWalletContract)
            add("hirer", digitalWalletContract.hirer)
            add("paymentInstrument", null)
        }})

        when:
        ContractorCreditRecurrence created = service.create(recurrence)
        ContractorCreditRecurrence found = service.findById(created.id)

        then:
        found.paymentInstrument in instruments
    }

    def 'given a credit recurrence without instrument and contractor without digital wallet should return error'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        ContractorCreditRecurrence recurrence = Fixture.from(ContractorCreditRecurrence).gimme("valid", new Rule(){{
            add("contract", contract)
            add("hirer", contract.hirer)
            add("paymentInstrument", null)
        }})

        when:
        service.create(recurrence)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_INSTRUMENT_NOT_FOUND'
    }


    @Unroll
    'given a credit recurrence with value #value should not be created'(){
        given:
        def invalidValue = value
        def contract = fixtureCreator.createPersistedContract()
        def instrument = fixtureCreator.createInstrumentToProduct(contract.product)
        ContractorCreditRecurrence recurrence = Fixture.from(ContractorCreditRecurrence).gimme("valid", new Rule(){{
            add("contract", contract)
            add("hirer", contract.hirer)
            add("paymentInstrument", instrument)
            add("value", invalidValue)
        }})

        when:
        service.create(recurrence)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'INVALID_VALUE'

        where:
        _ | value
        _ | null
        _ | 0.0
        _ | -1.0
    }

    def 'given a credit recurrence with hirer from another contract should not be created'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        def instrument = fixtureCreator.createInstrumentToProduct(contract.product)
        def hirer = fixtureCreator.createHirer()
        ContractorCreditRecurrence recurrence = Fixture.from(ContractorCreditRecurrence).gimme("valid", new Rule(){{
            add("contract", contract)
            add("hirer", hirer)
            add("paymentInstrument", instrument)
        }})

        when:
        service.create(recurrence)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'HIRER_BELONG_TO_OTHER_CONTRACT'
    }

}
