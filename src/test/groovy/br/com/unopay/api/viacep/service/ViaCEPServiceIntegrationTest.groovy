package br.com.unopay.api.viacep.service

import br.com.unopay.api.SpockApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.HttpClientErrorException


class ViaCEPServiceIntegrationTest extends SpockApplicationTests{

    @Autowired
    ViaCEPService service

    def "Search valid cep"() {
        given:
            def cep = '05305011'
        when:
            def address = service.search(cep)
        then:
            address.localidade == 'São Paulo'
            address.bairro == 'Vila Leopoldina'
            address.cep == '05305-011'
            address.uf == 'SP'
            !address.erro
    }

    def "Search unknown cep"() {
        given:
        def cep = '99999999'
        when:
        def address = service.search(cep)
        then:
        !address.localidade
        !address.bairro
        !address.cep
        !address.uf
        address.erro
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
