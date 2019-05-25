package br.com.unopay.api.network.service;

import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.network.model.Establishment;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstablishmentBranchService {

    private EstablishmentService establishmentService;
    private BranchService branchService;

    @Autowired
    public EstablishmentBranchService(EstablishmentService establishmentService,
                                      BranchService branchService) {
        this.establishmentService = establishmentService;
        this.branchService = branchService;
    }

    public Establishment create(Establishment establishment, AccreditedNetwork accreditedNetwork){
        Optional<Establishment> existing = establishmentService.getByPersonDocumentNumberAndNetworkId(establishment.documentNumber(), accreditedNetwork);
        Establishment current = existing.orElseGet(() ->  establishmentService.create(establishment, accreditedNetwork));
        createBranchWhenIsRequired(establishment);
        return current;
    }

    public Establishment create(Establishment establishment){
        Optional<Establishment> existing = establishmentService.getByPersonDocumentNumber(establishment.documentNumber());
        Establishment current = existing.orElseGet(() -> establishmentService.create(establishment));
        createBranchWhenIsRequired(establishment);
        return current;
    }

    private void createBranchWhenIsRequired(Establishment establishment) {
        if(establishment.isCreateBranch()){
            branchService.create(establishment.toBranch());
        }
    }
}
