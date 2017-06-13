package br.com.unopay.api.pamcary.translate

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.model.CargoContract
import br.com.unopay.api.model.CargoProfile
import br.com.unopay.api.model.ComplementaryTravelDocument
import br.com.unopay.api.model.TravelDocument
import br.com.unopay.api.pamcary.transactional.FieldTO
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import static org.hamcrest.Matchers.hasSize
import static spock.util.matcher.HamcrestSupport.that

class KeyValueTranslatorTest extends FixtureApplicationTest {

    def 'should translate basic level'(){
        given:
        List<ComplementaryTravelDocument> complementaryDocuments = Fixture
                                                        .from(ComplementaryTravelDocument.class).gimme(1,"valid")
        List<TravelDocument> documents = Fixture.from(TravelDocument.class).gimme(1,"valid")
        CargoContract cargoContract = Fixture.from(CargoContract.class).gimme("valid", new Rule(){{
            add("travelDocuments", documents)
            add("complementaryTravelDocuments", complementaryDocuments)
        }})
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFields(cargoContract)

        then:
        fieldTOS.find {
            it.key == 'viagem.documento.qtde'
        }?.value == String.valueOf(cargoContract.travelDocuments.size())

        fieldTOS.find {
            it.key == 'viagem.documento1.sigla'
        }?.value == cargoContract.travelDocuments.find().type.name()
        fieldTOS.find {
            it.key == 'viagem.documento1.numero'
        }?.value == cargoContract.travelDocuments.find().documentNumber
        fieldTOS.find {
            it.key == 'viagem.indicador.ressalva'
        }?.value == cargoContract.caveat.name()
    }

    def 'should translate reserve level'(){
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFields(new WithReverseField())

        then:
        fieldTOS.find {
            it.key == 'viagem.reverse'
        }?.value != null
    }

    def 'class without base annotation should not be translated'(){
        when:
        new KeyValueTranslator().extractFields(new WithOutBaseKeyAnnotation())

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'BASE_KEY_REQUIRED'
    }

    def 'should translate complementary level'(){
        given:
        List<ComplementaryTravelDocument> complementaryDocuments = Fixture
                                                            .from(ComplementaryTravelDocument.class).gimme(1,"valid")
        List<TravelDocument> documents = Fixture.from(TravelDocument.class).gimme(1,"valid")
        CargoContract contract = Fixture.from(CargoContract.class).gimme("valid", new Rule(){{
            add("travelDocuments", documents)
            add("complementaryTravelDocuments", complementaryDocuments)
        }})
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFields(contract)

        then:
        fieldTOS.find {
            it.key == 'viagem.documento.complementar1.sigla'
        }?.value == contract.complementaryTravelDocuments.find().type.name()

        fieldTOS.find {
            it.key == 'viagem.documento.complementar.qtde'
        }?.value == String.valueOf(contract.complementaryTravelDocuments.size())

        fieldTOS.find {
            it.key == 'viagem.documento.complementar1.numero'
        }?.value == contract.complementaryTravelDocuments.find().documentNumber
    }

    def 'should translate reference level'(){
        given:
        CargoContract cargoContract = Fixture.from(CargoContract.class).gimme("valid")
        cargoContract.partnerId = '546546'
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFields(cargoContract)

        then:
        fieldTOS.find { it.key == 'viagem.id' }?.value != null
        fieldTOS.find { it.key == 'viagem.id' }.value == cargoContract.partnerId
    }

    def 'given a null reference field value should not be translated'(){
        given:
        CargoContract cargoContract = Fixture.from(CargoContract.class).gimme("valid")
        cargoContract.partnerId = null
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFields(cargoContract)

        then:
        !fieldTOS.find { it.key == 'viagem.id' }
    }

    def 'given a enum null field value should not be translated'(){
        given:
        List<TravelDocument> travelDocuments = Fixture.from(TravelDocument.class).gimme(2, "valid")
        travelDocuments.find().caveat = null
        when:
        List<FieldTO> fieldTOS =  new KeyValueTranslator().extractFields(travelDocuments.find())

        then:
        !fieldTOS.find { it.key == 'viagem.indicador.ressalva' }
    }

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
        def number = '10005550'
        def fieldsTO = [new FieldTO() {{ setKey("viagem.documento.numero"); setValue(number) }}]

        when:
        TravelDocument travelDocument = new KeyValueTranslator().populate(TravelDocument.class,fieldsTO)

        then:
        travelDocument?.documentNumber == number
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
@KeyBase(key = "viagem")
class WithReverseField {

    @KeyField(baseField = "base", reverseField = "reverse")
    private String field = "field"

    public void setField(String field){
        this.field = field;
    }
    public String getField(){
        this.field;
    }
}

class WithOutBaseKeyAnnotation {

    @KeyField(baseField = "viagem.base", reverseField = "viagem.reverse")
    private String field = "field"

    public void setField(String field){
        this.field = field;
    }
    public String getField(){
        this.field;
    }
}
