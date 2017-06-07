package br.com.unopay.api.pamcary.translate

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.model.TravelDocument
import br.com.unopay.api.pamcary.transactional.FieldTO

class PamcarySoapTranslatorTest extends FixtureApplicationTest {

    def 'should translate basic level'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        when:
        List<FieldTO> fieldTOS =  new PamcarySoapTranslator().translate(travelDocuments.find())

        then:
        fieldTOS.find { it.key == 'viagem.documento.sigla' }?.value == travelDocuments.find().type.name()
        fieldTOS.find { it.key == 'viagem.indicador.ressalva' }?.value == travelDocuments.find().caveat.name()
        fieldTOS.find { it.key == 'viagem.documento.qtde' }?.value == travelDocuments.find().quantity.toString()
        fieldTOS.find { it.key == 'viagem.documento.numero' }?.value == travelDocuments.find().documentNumber
    }

    def 'should translate complementary level'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        when:
        List<FieldTO> fieldTOS =  new PamcarySoapTranslator().translate(travelDocuments.find())

        then:
        fieldTOS.find {
            it.key == 'viagem.documento.complementar.sigla'
        }?.value == travelDocuments.find().complementaryTravelDocument.type.name()

        fieldTOS.find {
            it.key == 'viagem.documento.complementar.qtde'
        }?.value == travelDocuments.find().complementaryTravelDocument.quantity.toString()

        fieldTOS.find {
            it.key == 'viagem.documento.complementar.numero'
        }?.value == travelDocuments.find().complementaryTravelDocument.documentNumber
    }

    def 'should translate reference level'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        travelDocuments.find().contract.id = '546546'
        when:
        List<FieldTO> fieldTOS =  new PamcarySoapTranslator().translate(travelDocuments.find())

        then:
        fieldTOS.find { it.key == 'viagem.id' }?.value != null
        fieldTOS.find { it.key == 'viagem.id' }.value == travelDocuments.find().contract.id
    }

}
