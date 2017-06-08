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
        fieldTOS.find { it.key == 'viagem.documento.qtde' }?.value == String.valueOf(travelDocuments.find().quantity)
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
        }?.value == String.valueOf(travelDocuments.find().complementaryTravelDocument.quantity)

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

    def 'given a null reference field value should not be translated'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        travelDocuments.find().contract.id = null
        when:
        List<FieldTO> fieldTOS =  new PamcarySoapTranslator().translate(travelDocuments.find())

        then:
        !fieldTOS.find { it.key == 'viagem.id' }
    }

    def 'given a enum null field value should not be translated'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        travelDocuments.find().caveat = null
        when:
        List<FieldTO> fieldTOS =  new PamcarySoapTranslator().translate(travelDocuments.find())

        then:
        !fieldTOS.find { it.key == 'viagem.indicador.ressalva' }
    }

    def 'given a null field value should not be translated'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        travelDocuments.find().quantity = null
        when:
        List<FieldTO> fieldTOS =  new PamcarySoapTranslator().translate(travelDocuments.find())

        then:
        !fieldTOS.find { it.key == 'viagem.documento.qtde' }
    }

    def 'should translate fields to travel document'(){
        given:
        def quantity = 10
        def fieldsTO = [new FieldTO() {{ setKey("viagem.documento.qtde"); setValue(quantity.toString()) }}]

        when:
        TravelDocument travelDocument = new PamcarySoapTranslator().translate(fieldsTO)

        then:
        travelDocument?.quantity == quantity
    }

}
