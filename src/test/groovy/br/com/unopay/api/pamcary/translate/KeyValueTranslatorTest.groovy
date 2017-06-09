package br.com.unopay.api.pamcary.translate

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.model.CargoProfile
import br.com.unopay.api.model.TravelDocument
import br.com.unopay.api.pamcary.model.TravelDocumentsWrapper
import br.com.unopay.api.pamcary.transactional.FieldTO
import static org.hamcrest.Matchers.hasSize
import static spock.util.matcher.HamcrestSupport.that

class KeyValueTranslatorTest extends FixtureApplicationTest {

    def 'should translate basic level'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFieldTOList(travelDocuments.find())

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
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFieldTOList(travelDocuments.find())

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
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFieldTOList(travelDocuments.find())

        then:
        fieldTOS.find { it.key == 'viagem.id' }?.value != null
        fieldTOS.find { it.key == 'viagem.id' }.value == travelDocuments.find().contract.code.toString()
    }

    def 'given a null reference field value should not be translated'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        travelDocuments.find().contract.code = null
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFieldTOList(travelDocuments.find())

        then:
        !fieldTOS.find { it.key == 'viagem.id' }
    }

    def 'given a enum null field value should not be translated'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        travelDocuments.find().caveat = null
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFieldTOList(travelDocuments.find())

        then:
        !fieldTOS.find { it.key == 'viagem.indicador.ressalva' }
    }

    def 'given a null field value should not be translated'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        travelDocuments.find().quantity = null
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFieldTOList(travelDocuments.find())

        then:
        !fieldTOS.find { it.key == 'viagem.documento.qtde' }
    }

    def 'should translate fields to travel document'(){
        given:
        def quantity = '10'
        def fieldsTO = [new FieldTO() {{ setKey("viagem.documento.qtde"); setValue(quantity) }}]

        when:
        TravelDocument travelDocument = new KeyValueTranslator().populateTravelDocument(fieldsTO)

        then:
        travelDocument?.quantity == quantity.toInteger()
    }

    def 'should translate referenced fields in travel document'(){
        given:
        def id = '122'
        def fieldsTO = [new FieldTO() {{ setKey("viagem.id"); setValue(id) }}]

        when:
        TravelDocument travelDocument = new KeyValueTranslator().populateTravelDocument(fieldsTO)

        then:
        travelDocument?.contract?.code == id.toInteger()
    }

    def 'should translate referenced list fields in travel document'(){
        given:
        def id = '122'
        def fieldsTO = [ new FieldTO() {{ setKey("viagem.documento1.numero"); setValue(id) }} ]

        when:
        TravelDocumentsWrapper travelDocument = new KeyValueTranslator().populateTravelDocumentWrapper(fieldsTO)

        then:
        that travelDocument?.travelDocuments, hasSize(1)
        travelDocument.travelDocuments.find().documentNumber == id
    }

    def 'should translate referenced enum fields in travel document'(){
        given:
        def id = '1'
        def fieldsTO = [ new FieldTO() {{ setKey("viagem.carga.perfil.id"); setValue(id) }} ]

        when:
        TravelDocumentsWrapper travelDocument = new KeyValueTranslator().populateTravelDocumentWrapper(fieldsTO)

        then:
        travelDocument.cargoContract.cargoProfile == CargoProfile.DRY_CARGO
    }


}
