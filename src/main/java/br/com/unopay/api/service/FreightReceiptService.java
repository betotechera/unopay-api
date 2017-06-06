package br.com.unopay.api.service;

import br.com.unopay.api.repository.CargoContractRepository;
import br.com.unopay.api.repository.ComplementaryTravelDocumentRepository;
import br.com.unopay.api.repository.TravelDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FreightReceiptService {

    private CargoContractRepository cargoContractRepository;
    private TravelDocumentRepository travelDocumentRepository;
    private ComplementaryTravelDocumentRepository complementaryTravelDocumentRepository;

    @Autowired
    public FreightReceiptService(CargoContractRepository cargoContractRepository,
                                 TravelDocumentRepository travelDocumentRepository,
                                 ComplementaryTravelDocumentRepository complementaryTravelDocumentRepository) {
        this.cargoContractRepository = cargoContractRepository;
        this.travelDocumentRepository = travelDocumentRepository;
        this.complementaryTravelDocumentRepository = complementaryTravelDocumentRepository;
    }
}
