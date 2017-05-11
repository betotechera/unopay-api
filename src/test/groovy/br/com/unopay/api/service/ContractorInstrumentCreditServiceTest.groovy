package br.com.unopay.api.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.ContractorInstrumentCredit
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
}
