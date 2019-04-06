package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Branch;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.filter.BranchFilter;
import br.com.unopay.api.bacen.repository.BranchRepository;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.BRANCH_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_BELONG_TO_ANOTHER_NETWORK;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_BRANCH_BELONG_TO_ANOTHER_NETWORK;

@Service
public class BranchService {

    private BranchRepository repository;
    private PersonService personService;
    private AccreditedNetworkService accreditedNetworkService;
    private EstablishmentService establishmentService;

    @Autowired
    public BranchService(EstablishmentService establishmentService,
                         PersonService personService,
                         BranchRepository repository,
                         AccreditedNetworkService accreditedNetworkService){
        this.repository = repository;
        this.establishmentService = establishmentService;
        this.personService = personService;
        this.accreditedNetworkService = accreditedNetworkService;
    }

    public Branch create(Branch branch, AccreditedNetwork accreditedNetwork) {
        AccreditedNetwork network = accreditedNetworkService.getById(accreditedNetwork.getId());
        Establishment headOffice = establishmentService.findByIdAndNetworks(branch.getHeadOffice().getId(), network);
        branch.setHeadOffice(headOffice);
        return create(branch);
    }

    public Branch create(Branch branch) {
        branch.validateCreate();
        saveReferences(branch);
        validateExistingReferences(branch);
        return repository.save(branch);
    }

    public void update(String id, Branch branch, AccreditedNetwork accreditedNetwork) {
        Establishment establishment = checkEstablishmentOwner(id, accreditedNetwork);
        branch.setHeadOffice(establishment);
        update(id, branch);
    }

    public void update(String id, Branch branch) {
        Branch current = findById(id);
        branch.validateUpdate(current);
        validateExistingReferences(branch);
        saveReferences(branch);
        current.updateMe(branch);
        repository.save(current);
    }

    public Branch findById(String id) {
        Optional<Branch> branch = repository.findById(id);
        return branch.orElseThrow(() -> UnovationExceptions.notFound().withErrors(BRANCH_NOT_FOUND));
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    private void saveReferences(Branch branch) {
        personService.create(branch.getPerson());
    }

    private void validateExistingReferences(Branch branch) {
        establishmentService.findById(branch.getHeadOffice().getId());
        personService.findById(branch.getPerson().getId());
    }

    private Establishment checkEstablishmentOwner(String id, AccreditedNetwork accreditedNetwork) {
        Branch branch = findById(id);
        Establishment currentEstablishment = establishmentService.findById(branch.getHeadOffice().getId());
        AccreditedNetwork currentNetwork = accreditedNetworkService.getById(accreditedNetwork.getId());
        if(!currentNetwork.getId().equals(currentEstablishment.getNetwork().getId())){
            throw UnovationExceptions.forbidden().withErrors(ESTABLISHMENT_BRANCH_BELONG_TO_ANOTHER_NETWORK);
        }
        return currentEstablishment;
    }

    public Page<Branch> findByFilter(BranchFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
}
