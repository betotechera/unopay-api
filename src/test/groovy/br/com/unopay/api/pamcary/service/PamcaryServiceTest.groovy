package br.com.unopay.api.pamcary.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.pamcary.service.PamcaryService
import com.sun.xml.internal.ws.developer.JAXWSProperties
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.jboss.ws.core.StubExt
import org.springframework.beans.factory.annotation.Autowired

import javax.net.ssl.SSLSocketFactory
import javax.xml.ws.BindingProvider

class PamcaryServiceTest extends SpockApplicationTests{

    @Autowired
    PamcaryService service

    def 'soap integration test'(){
        when:
        service.execute()
        then:
        true
    }
}
