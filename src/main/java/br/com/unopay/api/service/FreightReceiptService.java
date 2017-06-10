package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.bacen.service.EventService;
import br.com.unopay.api.model.CargoContract;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.FreightReceipt;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.TravelDocument;
import br.com.unopay.api.model.filter.TravelDocumentFilter;
import br.com.unopay.api.pamcary.model.TravelDocumentsWrapper;
import br.com.unopay.api.pamcary.service.PamcaryService;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import java.util.List;
import javax.transaction.Transactional;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FreightReceiptService {

    private CargoContractService cargoContractService;
    private TravelDocumentService travelDocumentService;
    private ComplementaryTravelDocumentService complementaryTravelDocumentService;
    private ContractService contractService;
    private UserDetailService userDetailService;
    private ServiceAuthorizeService serviceAuthorizeService;
    private ContractorInstrumentCreditService contractorInstrumentCreditService;
    private EventService eventService;
    @Setter
    private PamcaryService pamcaryService;

    @Autowired
    public FreightReceiptService(CargoContractService cargoContractService,
                                 TravelDocumentService travelDocumentService,
                                 ComplementaryTravelDocumentService complementaryTravelDocumentService,
                                 ContractService contractService, UserDetailService userDetailService,
                                 ServiceAuthorizeService serviceAuthorizeService,
                                 ContractorInstrumentCreditService contractorInstrumentCreditService,
                                 EventService eventService, PamcaryService pamcaryService) {
        this.cargoContractService = cargoContractService;
        this.travelDocumentService = travelDocumentService;
        this.complementaryTravelDocumentService = complementaryTravelDocumentService;
        this.contractService = contractService;
        this.userDetailService = userDetailService;
        this.serviceAuthorizeService = serviceAuthorizeService;
        this.contractorInstrumentCreditService = contractorInstrumentCreditService;
        this.eventService = eventService;
        this.pamcaryService = pamcaryService;
    }

    @Transactional
    public void receipt(String userEmail, FreightReceipt freightReceipt) {
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        checkContract(freightReceipt, currentUser);
        authorizeFuelSupply(userEmail, freightReceipt);
        checkReferences(freightReceipt);
        saveOrUpdate(freightReceipt.getCargoContract(), freightReceipt.getTravelDocuments());
    }

    private void checkReferences(FreightReceipt freightReceipt) {
        cargoContractService.findById(freightReceipt.getCargoContract().getId());
        freightReceipt.getTravelDocuments().forEach(d -> travelDocumentService.findById(d.getId()));
    }

    private void saveOrUpdate(CargoContract cargoContract, List<TravelDocument> travelDocuments) {
        cargoContractService.create(cargoContract);
        travelDocuments.forEach(doc -> {
            complementaryTravelDocumentService.create(doc.getComplementaryTravelDocument());
            travelDocumentService.create(doc);
        });
    }

    @Transactional
    public TravelDocumentsWrapper listDocuments(TravelDocumentFilter filter){
        TravelDocumentsWrapper wrapper = pamcaryService.searchDoc(filter);
        saveOrUpdate(wrapper.getCargoContract(), wrapper.getTravelDocuments());
        return wrapper;
    }

    private void authorizeFuelSupply(String userEmail, FreightReceipt freightReceipt) {
        defineFueInstrumentCredit(freightReceipt);
        eventService.findByIdAndServiceType(freightReceipt.fuelEventId(), ServiceType.FUEL_ALLOWANCE);
        ServiceAuthorize fuelSupplyServiceAuthorize = new ServiceAuthorize().toFuelSupply(freightReceipt);
        serviceAuthorizeService.create(userEmail, fuelSupplyServiceAuthorize);
    }

    private void defineFueInstrumentCredit(FreightReceipt freightReceipt) {
        ContractorInstrumentCredit credit = contractorInstrumentCreditService
                            .findByContractIdAndServiceType(freightReceipt.contractId(), ServiceType.FUEL_ALLOWANCE);
        freightReceipt.setInstrumentCredit(new ContractorInstrumentCredit(){{ setId(credit.getId());}});
    }

    private void checkContract(FreightReceipt freightReceipt, UserDetail currentUser) {
        Contract contract = contractService.findById(freightReceipt.getContract().getId());
        Establishment establishment = currentUser.myEstablishment().orElse(freightReceipt.getEstablishment());
        contract.checkValidFor(freightReceipt.getContractor(), establishment);
    }
}
