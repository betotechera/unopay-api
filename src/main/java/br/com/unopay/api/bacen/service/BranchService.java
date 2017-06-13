package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Branch;
import br.com.unopay.api.bacen.model.filter.BranchFilter;
import br.com.unopay.api.bacen.repository.BranchRepository;
import br.com.unopay.api.service.PersonService;
import static br.com.unopay.api.uaa.exception.Errors.BRANCH_NOT_FOUND;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BranchService {

    private BranchRepository repository;
    private PersonService personService;
    private EstablishmentService establishmentService;
    private BankAccountService bankAccountService;

    @Autowired
    public BranchService(BankAccountService bankAccountService,
                         EstablishmentService establishmentService,
                         PersonService personService,
                         BranchRepository repository){
        this.repository = repository;
        this.bankAccountService = bankAccountService;
        this.establishmentService = establishmentService;
        this.personService = personService;

    }

    public Branch create(Branch branch) {
        branch.validateCreate();
        saveReferences(branch);
        validateExistingReferences(branch);
        return repository.save(branch);
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
        personService.save(branch.getPerson());
        bankAccountService.create(branch.getBankAccount());
    }

    private void validateExistingReferences(Branch branch) {
        establishmentService.findById(branch.getHeadOffice().getId());
        bankAccountService.findById(branch.getBankAccount().getId());
        personService.findById(branch.getPerson().getId());
    }

    public Page<Branch> findByFilter(BranchFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
}
