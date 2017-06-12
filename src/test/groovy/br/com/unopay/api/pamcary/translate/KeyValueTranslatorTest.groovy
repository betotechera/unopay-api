package br.com.unopay.api.pamcary.translate

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.model.CargoContract
import br.com.unopay.api.model.CargoProfile
import br.com.unopay.api.model.TravelDocument
import br.com.unopay.api.pamcary.transactional.FieldTO
import static org.hamcrest.Matchers.hasSize
import spock.lang.Ignore
import static spock.util.matcher.HamcrestSupport.that

class KeyValueTranslatorTest extends FixtureApplicationTest {

    @Ignore
    def 'should translate basic level'(){
        given:
        CargoContract cargoContract = Fixture.from(CargoContract.class).gimme(2, "valid")
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFields(CargoContract)

        then:
        fieldTOS.find { it.key == 'viagem.documento.sigla' }?.value == cargoContract.travelDocuments.find().type.name()
        fieldTOS.find { it.key == 'viagem.indicador.ressalva' }?.value == cargoContract.caveat.name()
        fieldTOS.find { it.key == 'viagem.documento.qtde' }?.value == String.valueOf(cargoContract.travelDocuments.find().quantity)
        fieldTOS.find { it.key == 'viagem.documento.numero' }?.value == cargoContract.travelDocuments.find().documentNumber
    }
    @Ignore
    def 'should translate complementary level'(){
        given:
        CargoContract contract = Fixture.from(CargoContract.class).gimme("valid")
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFields(contract.find())

        then:
        fieldTOS.find {
            it.key == 'viagem.documento.complementar1.sigla'
        }?.value == contract.complementaryTravelDocuments.find().type.name()

        fieldTOS.find {
            it.key == 'viagem.documento.complementar1.qtde'
        }?.value == String.valueOf(contract.complementaryTravelDocuments.find().quantity)

        fieldTOS.find {
            it.key == 'viagem.documento.complementar1.numero'
        }?.value == contract.complementaryTravelDocuments.find().documentNumber
    }
    @Ignore
    def 'should translate reference level'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        travelDocuments.find().contract.id = '546546'
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFields(travelDocuments.find())

        then:
        fieldTOS.find { it.key == 'viagem.id' }?.value != null
        fieldTOS.find { it.key == 'viagem.id' }.value.toInteger() == travelDocuments.find().contract.code
    }
    @Ignore
    def 'given a null reference field value should not be translated'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        travelDocuments.find().contract.code = null
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFields(travelDocuments.find())

        then:
        !fieldTOS.find { it.key == 'viagem.id' }
    }
    @Ignore
    def 'given a enum null field value should not be translated'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        travelDocuments.find().caveat = null
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFields(travelDocuments.find())

        then:
        !fieldTOS.find { it.key == 'viagem.indicador.ressalva' }
    }
    @Ignore
    def 'given a null field value should not be translated'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        travelDocuments.find().quantity = null
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFields(travelDocuments.find())

        then:
        !fieldTOS.find { it.key == 'viagem.documento.qtde' }
    }

    def 'should translate fields to travel document'(){
        given:
        def quantity = '10'
        def fieldsTO = [new FieldTO() {{ setKey("viagem.documento.qtde"); setValue(quantity) }}]

        when:
        TravelDocument travelDocument = new KeyValueTranslator().populate(TravelDocument.class,fieldsTO)

        then:
        travelDocument?.quantity == quantity.toInteger()
    }

    def 'should translate partner id in cargo document'(){
        given:
        def id = '122'
        def fieldsTO = [new FieldTO() {{ setKey("viagem.id"); setValue(id) }}]

        when:
        CargoContract travelDocument = new KeyValueTranslator().populate(CargoContract.class,fieldsTO)

        then:
        travelDocument?.partnerId == id
    }

    def 'should translate referenced list fields in travel document'(){
        given:
        def id = '122'
        def fieldsTO = [ new FieldTO() {{ setKey("viagem.documento1.numero"); setValue(id) }} ]

        when:
        CargoContract travelDocument = new KeyValueTranslator().populate(CargoContract.class,fieldsTO)

        then:
        that travelDocument?.travelDocuments, hasSize(1)
        travelDocument.travelDocuments.find().documentNumber == id
    }

    def 'should translate document by document in travel document'(){
        given:
        def id1 = '124'
        def id2 = '123'
        def fieldsTO = [
                new FieldTO() {{ setKey("viagem.documento1.numero"); setValue(id1) }},
                new FieldTO() {{ setKey("viagem.documento1.sigla"); setValue('OVEN') }},
                new FieldTO() {{ setKey("viagem.documento2.numero"); setValue(id2) }},
                new FieldTO() {{ setKey("viagem.documento2.sigla"); setValue('OVEN') }},
        ]

        when:
        CargoContract travelDocument = new KeyValueTranslator().populate(CargoContract.class,fieldsTO)

        then:
        that travelDocument?.travelDocuments, hasSize(2)
        travelDocument.travelDocuments.first().documentNumber == id2
        travelDocument.travelDocuments.last().documentNumber == id1
    }

    def 'given a list of list should translate document by document in travel document'(){
        given:
        def id1 = '124'
        def id2 = '123'
        def fieldsTO = [
                new FieldTO() {{ setKey("viagem.documento1.numero"); setValue(id1) }},
                new FieldTO() {{ setKey("viagem.documento1.sigla"); setValue('OVEN') }},
                new FieldTO() {{ setKey("viagem.documento2.numero"); setValue(id2) }},
                new FieldTO() {{ setKey("viagem.documento2.sigla"); setValue('OVEN') }},
                new FieldTO() {{ setKey("viagem.documento.complementar2.numero"); setValue(id2) }},
                new FieldTO() {{ setKey("viagem.documento.complementar2.sigla"); setValue('CTE') }},
        ]

        when:
        CargoContract travelDocument = new KeyValueTranslator().populate(CargoContract.class,fieldsTO)

        then:
        that travelDocument?.travelDocuments, hasSize(2)
        travelDocument.travelDocuments.first().documentNumber == id2
        travelDocument.travelDocuments.last().documentNumber == id1
        that travelDocument.complementaryTravelDocuments, hasSize(1)
    }

    def 'should translate referenced enum fields in travel document'(){
        given:
        def id = '1'
        def fieldsTO = [ new FieldTO() {{ setKey("viagem.carga.perfil.id"); setValue(id) }} ]

        when:
        CargoContract travelDocument = new KeyValueTranslator().populate(CargoContract.class,fieldsTO)

        then:
        travelDocument.cargoProfile == CargoProfile.DRY_CARGO
    }

}
