package br.com.unopay.api.viacep.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.address.service.AddressSearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Ignore

@Ignore
class AddressSearchServiceIntegrationTest extends SpockApplicationTests{

    @Autowired
    AddressSearchService service

    def "Search valid cep"() {
        given:
            def cep = '05305011'
        when:
            def address = service.search(cep)
        then:
            address.cidade == 'São Paulo'
            address.bairro == 'Vila Leopoldina'
            address.cep == '05305011'
            address.estado == 'SP'
    }

    def "Search unknown cep"() {
        given:
        def cep = '99999999'
        when:
        def address = service.search(cep)
        then:
        thrown(HttpClientErrorException)
    }
    def "Search invalid cep"() {
        given:
        def cep = '999999999'
        when:
        service.search(cep)
        then:
            thrown(HttpClientErrorException)
    }

}
