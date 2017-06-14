package br.com.unopay.api.pamcary.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.model.CargoContract
import br.com.unopay.api.model.ComplementaryTravelDocument
import br.com.unopay.api.model.TravelDocument
import br.com.unopay.api.model.filter.CargoContractFilter
import br.com.unopay.api.model.filter.EstablishmentFilter
import br.com.unopay.api.service.CargoContractService
import br.com.unopay.api.service.ComplementaryTravelDocumentService
import br.com.unopay.api.service.FreightReceiptService
import br.com.unopay.api.service.TravelDocumentService
import static org.hamcrest.Matchers.hasSize
import org.springframework.beans.factory.annotation.Autowired
import static spock.util.matcher.HamcrestSupport.that

class PamcaryServiceTest {

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


    def 'soap searchDoc integration test'(){
        given:
        CargoContractFilter filter = createFilter()

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
        CargoContractFilter filter = createFilter()

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

    def 'soap confirmDocDelivery integration test'(){
        given:

        List<ComplementaryTravelDocument> complementaryDocuments = Fixture.from(ComplementaryTravelDocument.class)
                .gimme(1,"valid")
        List<TravelDocument> documents = Fixture.from(TravelDocument.class).gimme(1, "valid")
        CargoContract cargo = Fixture.from(CargoContract.class).gimme("valid",new Rule(){{
            add("partnerId", "1125447")
            add("travelDocuments", documents)
            add("complementaryTravelDocuments", complementaryDocuments)
        }})

        when:
        service.confirmDocDelivery('27064195503000',cargo)

        then:
        true
    }

    private CargoContractFilter createFilter() {
        new CargoContractFilter().with
                {
                    setContractCode('1125447')
                    setContractorDocument('64773370106')
                    setContractorDocumentType('2')
                    setEstablishment(new EstablishmentFilter().with {document = '27064195503000'; it })
                    it
                }
    }
}
