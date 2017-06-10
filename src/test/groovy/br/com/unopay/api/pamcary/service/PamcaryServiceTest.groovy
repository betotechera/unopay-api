package br.com.unopay.api.pamcary.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.model.TravelDocument
import br.com.unopay.api.model.filter.TravelDocumentFilter
import br.com.unopay.api.pamcary.model.TravelDocumentsWrapper
import br.com.unopay.api.pamcary.transactional.FieldTO
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore

class PamcaryServiceTest extends SpockApplicationTests{

    @Autowired
    PamcaryService service

    def 'soap integration test'(){
        given:
        TravelDocumentFilter filter = new TravelDocumentFilter().with
            {
                setContractCode('1125447')
                setContractorDocument('64773370106')
                setContractorDocumentType('2')
                setEstablishmentDocument('27064195503000')
                it
            }

        when:
        TravelDocumentsWrapper searchDoc = service.searchDoc(filter)

        then:
        !searchDoc.travelDocuments.isEmpty()
        searchDoc.cargoContract.cargoProfile != null
    }
}
