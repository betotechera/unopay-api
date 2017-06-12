package br.com.unopay.api.pamcary.service

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.model.CargoContract
import br.com.unopay.api.model.filter.EstablishmentFilter
import br.com.unopay.api.model.filter.TravelDocumentFilter
import br.com.unopay.api.service.CargoContractService
import br.com.unopay.api.service.ComplementaryTravelDocumentService
import br.com.unopay.api.service.FreightReceiptService
import br.com.unopay.api.service.TravelDocumentService
import static org.hamcrest.Matchers.hasSize
import org.springframework.beans.factory.annotation.Autowired
import static spock.util.matcher.HamcrestSupport.that

class PamcaryServiceTest extends SpockApplicationTests{

    @Autowired
    PamcaryService service

    @Autowired
    FreightReceiptService freightReceiptService

    @Autowired
    CargoContractService cargoContractService

    @Autowired
    TravelDocumentService travelDocumentService

    @Autowired
    ComplementaryTravelDocumentService complementaryTravelDocumentService

    def 'soap integration test'(){
        given:
        TravelDocumentFilter filter = createFilter()

        when:
        CargoContract searchDoc = service.searchDoc(filter)

        then:
        !searchDoc.travelDocuments.isEmpty()
        searchDoc.cargoProfile != null
        searchDoc.caveat != null
        that searchDoc.travelDocuments, hasSize(1)
        that searchDoc.complementaryTravelDocuments, hasSize(1)
        searchDoc.travelDocuments.find().documentNumber != null
        searchDoc.partnerId != null
        searchDoc.complementaryTravelDocuments.documentNumber != null

    }

    def 'should list and save documents'(){
        given:
        TravelDocumentFilter filter = createFilter()

        when:
        def documents = freightReceiptService.listDocuments(filter)

        then:
        documents != null
        documents.partnerId != null
        that documents.travelDocuments, hasSize(1)
        that documents.complementaryTravelDocuments, hasSize(1)
        cargoContractService.findById(documents.getId())
        documents.travelDocuments.each { travelDocumentService.findById(it.getId())}
        documents.complementaryTravelDocuments.each { complementaryTravelDocumentService.findById(it.getId())}
    }

    private TravelDocumentFilter createFilter() {
        new TravelDocumentFilter().with
                {
                    setContractCode('1125447')
                    setContractorDocument('64773370106')
                    setContractorDocumentType('2')
                    setEstablishment(new EstablishmentFilter().with {document = '27064195503000'; it })
                    it
                }
    }
}
