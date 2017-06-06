package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.model.CargoContract
import br.com.unopay.api.model.Contract
import org.springframework.beans.factory.annotation.Autowired

class CargoContractServiceTest extends SpockApplicationTests {

    @Autowired
    CargoContractService service

    def 'should create cargo contract'(){
        given:
        Contract contract = Fixture.from(Contract.class).uses(jpaProcessor).gimme("valid")
        CargoContract cargoContract = Fixture.from(CargoContract.class).gimme("valid", new Rule(){{
            add("contract", contract)
        }})

        when:
        def created = service.create(cargoContract)
        def result = service.findById(created.id)

        then:
        result.id != null
    }
}
