package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import groovy.time.TimeCategory
import spock.lang.Unroll

class CargoContractTest extends FixtureApplicationTest {

    void setup(){
        Integer.mixin(TimeCategory)
    }

    def 'should update me'(){
        given:
        CargoContract a = Fixture.from(CargoContract.class).gimme("valid")
        CargoContract b = Fixture.from(CargoContract.class).gimme("valid")
        b.partnerId = '112233'
        when:
         a.updateMe(b)

        then:
        a.partnerId == b.partnerId
    }

    @Unroll
    'given a CargoContract with  DRY_CARGO and #quantity damagedItems should throw error'(){
        given:
        List<TravelDocument> documents = Fixture.from(TravelDocument.class).gimme(1,"valid")
        CargoContract contract = Fixture.from(CargoContract.class).gimme("valid", new Rule(){{
            add("travelDocuments", documents)
        }})
        contract.cargoProfile = CargoProfile.DRY_CARGO
        contract.travelDocuments.find().damagedItems = quantity

        when:
        contract.validate()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors?.first()?.logref == 'DAMAGED_ITEMS_REQUIRED'

        where:
        _ | quantity
        _ | -1
        _ | null
    }

    @Unroll
    'given a CargoContract with  IN_BULK and #weight cargoWeight should throw error'(){
        given:
        List<TravelDocument> documents = Fixture.from(TravelDocument.class).gimme(1,"valid")
        CargoContract contract = Fixture.from(CargoContract.class).gimme("valid", new Rule(){{
            add("travelDocuments", documents)
        }})
        contract.cargoProfile = CargoProfile.IN_BULK
        contract.travelDocuments.find().cargoWeight = weight

        when:
        contract.validate()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors?.first()?.logref == 'WEIGHT_REQUIRED'

        where:
        _ | weight
        _ | -1
        _ | 0
        _ | null
    }


}