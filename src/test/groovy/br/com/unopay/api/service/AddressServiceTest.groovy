package br.com.unopay.api.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.model.State
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore

@Ignore
class AddressServiceTest extends SpockApplicationTests {

    @Autowired
    AddressService service

    def "Search valid cep"() {
        given:
        def cep = '05305011'
        when:
        def address = service.search(cep)
        then:
        address.city == 'São Paulo'
        address.district == 'Vila Leopoldina'
        address.streetName == 'Avenida Imperatriz Leopoldina'
        address.zipCode == '05305011'
        address.state == State.SP
    }

    def "Search unknown cep"() {
        given:
        def cep = '99999999'
        when:
        def address = service.search(cep)
        then:
        address
        address.zipCode
        !address.city
        !address.district
        !address.state
    }

    def "Search cep with error"() {
        given:
        def cep = '999999999'
        when:
        def address = service.search(cep)
        then:
        address
        address.zipCode
        !address.city
        !address.district
        !address.state
    }


}
