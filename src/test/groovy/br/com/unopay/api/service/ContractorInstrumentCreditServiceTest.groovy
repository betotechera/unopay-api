package br.com.unopay.api.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.ContractorInstrumentCredit
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class ContractorInstrumentCreditServiceTest extends SpockApplicationTests {

    @Autowired
    ContractorInstrumentCreditService service

    @Autowired
    SetupCreator setupCreator

    def 'instrument credit should be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = setupCreator.createContractorInstrumentCredit()

        when:
        ContractorInstrumentCredit created = service.insert(instrumentCredit)
        ContractorInstrumentCredit result = service.findById(created.id)

        then:
        result.id != null
    }

    def 'instrument with unknown contract credit should not be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = setupCreator.createContractorInstrumentCredit()
        instrumentCredit.with { contract.id = ''; it }

        when:
        service.insert(instrumentCredit)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    def 'instrument with unknown payment instrument credit should not be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = setupCreator.createContractorInstrumentCredit()
        instrumentCredit.with { paymentInstrument.id = ''; it }

        when:
        service.insert(instrumentCredit)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PAYMENT_INSTRUMENT_NOT_FOUND'
    }

    def 'instrument with unknown credit payment account credit should not be inserted'(){
        given:
        ContractorInstrumentCredit instrumentCredit = setupCreator.createContractorInstrumentCredit()
        instrumentCredit.with { creditPaymentAccount.id = ''; it }

        when:
        service.insert(instrumentCredit)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CREDIT_PAYMENT_ACCOUNT_NOT_FOUND'
    }
}
