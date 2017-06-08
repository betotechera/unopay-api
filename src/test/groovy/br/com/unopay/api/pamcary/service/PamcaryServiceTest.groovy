package br.com.unopay.api.pamcary.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.pamcary.transactional.FieldTO
import org.springframework.beans.factory.annotation.Autowired

class PamcaryServiceTest extends SpockApplicationTests{

    @Autowired
    PamcaryService service

    def 'soap integration test'(){
        given:
        def fieldTOS = [
                new FieldTO() {{ setKey("parceiro.documento.numero"); setValue('24122925000173') }},
                new FieldTO() {{ setKey("pontoapoio.documento.numero"); setValue('27064195503000') }},
                new FieldTO() {{ setKey("viagem.id"); setValue('1125447') }},
                new FieldTO() {{ setKey("viagem.favorecido.documento.tipo"); setValue('2') }},
                new FieldTO() {{ setKey("viagem.favorecido.documento.numero"); setValue('64773370106') }},
                new FieldTO() {{ setKey("viagem.transacao.nsu"); setValue('1002170144') }},
        ]

        when:
        def searchDoc = service.searchDoc(fieldTOS)

        then:
        searchDoc.find { it.key == 'mensagem.codigo'}?.value == '0'
    }
}
