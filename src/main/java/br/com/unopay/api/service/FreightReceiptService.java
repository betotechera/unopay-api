package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.FreightReceipt;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FreightReceiptService {

    private CargoContractService cargoContractService;
    private TravelDocumentService travelDocumentService;
    private ComplementaryTravelDocumentService complementaryTravelDocumentService;
    private ContractService contractService;
    private UserDetailService userDetailService;

    @Autowired
    public FreightReceiptService(CargoContractService cargoContractService,
                                 TravelDocumentService travelDocumentService,
                                 ComplementaryTravelDocumentService complementaryTravelDocumentService,
                                 ContractService contractService, UserDetailService userDetailService) {
        this.cargoContractService = cargoContractService;
        this.travelDocumentService = travelDocumentService;
        this.complementaryTravelDocumentService = complementaryTravelDocumentService;
        this.contractService = contractService;
        this.userDetailService = userDetailService;
    }

    public void receipt(String userEmail, FreightReceipt freightReceipt) {
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        checkContract(freightReceipt, currentUser);
        cargoContractService.create(freightReceipt.getCargoContract());
        freightReceipt.getTravelDocuments().forEach(doc -> {
            complementaryTravelDocumentService.create(doc.getComplementaryTravelDocument());
            travelDocumentService.create(doc);
        });
    }

    private void checkContract(FreightReceipt freightReceipt, UserDetail currentUser) {
        Contract contract = contractService.findById(freightReceipt.getContract().getId());
        Establishment establishment = currentUser.myEstablishment().orElse(freightReceipt.getEstablishment());
        contract.checkValidFor(freightReceipt.getContractor(), establishment);
    }
}
