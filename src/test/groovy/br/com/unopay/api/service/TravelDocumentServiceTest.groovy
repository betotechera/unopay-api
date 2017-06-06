package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.model.ComplementaryTravelDocument
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.TravelDocument
import org.springframework.beans.factory.annotation.Autowired

class TravelDocumentServiceTest extends SpockApplicationTests {

    @Autowired
    TravelDocumentService service

    def 'should create cargo contract'(){
        given:
        Contract contract = Fixture.from(Contract.class).uses(jpaProcessor).gimme("valid")
        ComplementaryTravelDocument complementaryDocument = Fixture.from(ComplementaryTravelDocument.class)
                                                                        .uses(jpaProcessor).gimme("valid")
        TravelDocument cargoContract = Fixture.from(TravelDocument.class).gimme("valid", new Rule(){{
            add("contract", contract)
            add("complementaryTravelDocument", complementaryDocument)
        }})
        when:
        def created = service.create(cargoContract)
        def result = service.findById(created.id)

        then:
        result.id != null
    }
}
