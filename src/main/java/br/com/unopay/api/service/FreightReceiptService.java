package br.com.unopay.api.service;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.bacen.service.EventService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.CargoContract;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.FreightReceipt;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.CargoContractFilter;
import br.com.unopay.api.pamcary.service.PamcaryService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Optional;
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
    private Notifier notifier;
    @Setter
    private PamcaryService pamcaryService;

    @Autowired
    public FreightReceiptService(CargoContractService cargoContractService,
                                 TravelDocumentService travelDocumentService,
                                 ComplementaryTravelDocumentService complementaryTravelDocumentService,
                                 ContractService contractService, UserDetailService userDetailService,
                                 ServiceAuthorizeService serviceAuthorizeService,
                                 ContractorInstrumentCreditService contractorInstrumentCreditService,
                                 EventService eventService, Notifier notifier, PamcaryService pamcaryService) {
        this.cargoContractService = cargoContractService;
        this.travelDocumentService = travelDocumentService;
        this.complementaryTravelDocumentService = complementaryTravelDocumentService;
        this.contractService = contractService;
        this.userDetailService = userDetailService;
        this.serviceAuthorizeService = serviceAuthorizeService;
        this.contractorInstrumentCreditService = contractorInstrumentCreditService;
        this.eventService = eventService;
        this.notifier = notifier;
        this.pamcaryService = pamcaryService;
    }

    @Transactional
    public void receipt(String userEmail, FreightReceipt freightReceipt) {
        UserDetail currentUser = userDetailService.getByEmail(userEmail);
        freightReceipt.getCargoContract().validate();
        checkContract(freightReceipt, currentUser);
        authorizeFuelSupply(userEmail, freightReceipt);
        checkReferences(freightReceipt);
        freightReceipt.getCargoContract().markAsDelivered();
        cargoContractService.save(freightReceipt.getCargoContract());
        notifier.notify(Queues.PAMCARY_TRAVEL_DOCUMENTS, freightReceipt);
    }

    private void checkReferences(FreightReceipt freightReceipt) {
        cargoContractService.findById(freightReceipt.getCargoContract().getId());
        freightReceipt.getCargoContract().getTravelDocuments().forEach(d -> travelDocumentService.findById(d.getId()));
        freightReceipt.getCargoContract()
                .getComplementaryTravelDocuments().forEach(d -> complementaryTravelDocumentService.findById(d.getId()));
    }

    @Transactional
    public CargoContract listDocuments(CargoContractFilter filter){
        CargoContract newCargoContract = getCargoContract(filter);
        newCargoContract.setMeUp();
        Optional<CargoContract> currentByPartnerId = cargoContractService
                                                                    .findByPartnerId(newCargoContract.getPartnerId());
        currentByPartnerId.ifPresent(current -> {
            current.updateMeAndReferences(newCargoContract);
            cargoContractService.save(current);
        });
        return currentByPartnerId.orElseGet(() -> cargoContractService.save(newCargoContract));
    }

    private CargoContract getCargoContract(CargoContractFilter filter) {
        CargoContract newCargoContract = pamcaryService.searchDoc(filter);
        if(newCargoContract == null || newCargoContract.getPartnerId() == null) {
            throw UnovationExceptions.notFound().withErrors(Errors.CARGO_CONTRACT_NOT_FOUND);
        }
        return newCargoContract;
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
