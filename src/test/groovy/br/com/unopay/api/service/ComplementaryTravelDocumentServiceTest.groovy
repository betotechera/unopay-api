package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.model.CargoContract
import br.com.unopay.api.model.ComplementaryTravelDocument
import org.springframework.beans.factory.annotation.Autowired

class ComplementaryTravelDocumentServiceTest extends SpockApplicationTests {

    @Autowired
    ComplementaryTravelDocumentService service

    def 'should create complementary document'(){
        given:
        CargoContract cargoContract = Fixture.from(CargoContract.class).uses(jpaProcessor).gimme("valid")
        ComplementaryTravelDocument document = Fixture.from(ComplementaryTravelDocument.class).gimme("valid", new Rule(){{
            add("cargoContract", cargoContract)
        }})
        when:
        def created = service.save(document)
        def result = service.findById(created.id)

        then:
        result.id != null
    }
}
