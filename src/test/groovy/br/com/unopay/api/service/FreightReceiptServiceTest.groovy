package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.model.CargoContract
import org.springframework.beans.factory.annotation.Autowired

class FreightReceiptServiceTest extends SpockApplicationTests {

    @Autowired
    FreightReceiptService service

    def 'should create cargo contract'(){
        given:
        CargoContract cargoContract = Fixture.from(CargoContract.class).gimme("valid")
        when:
        cargoContract.cargoProfile
        then:
        true
    }
}
