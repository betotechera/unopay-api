package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.model.ComplementaryTravelDocument
import org.springframework.beans.factory.annotation.Autowired

class ComplementaryTravelDocumentServiceTest extends SpockApplicationTests {

    @Autowired
    ComplementaryTravelDocumentService service

    def 'should create complementary document'(){
        given:
        ComplementaryTravelDocument document = Fixture.from(ComplementaryTravelDocument.class).gimme("valid")
        when:
        def created = service.create(document)
        def result = service.findById(created.id)

        then:
        result.id != null
    }
}
