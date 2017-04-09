package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Branch;
import br.com.unopay.api.bacen.repository.BranchRepository;
import br.com.unopay.api.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
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
        createReferences(branch);
        validateExistingReferences(branch);
        return repository.save(branch);
    }

    public void update(String id, Branch branch) {
        Branch current = findById(id);
        branch.validateUpdate(current);
        validateExistingReferences(branch);
        current.setTechnicalContact(branch.getTechnicalContact());
        repository.save(current);
    }

    public Branch findById(String id) {
        return  repository.findById(id);
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    private void createReferences(Branch branch) {
        personService.save(branch.getPerson());
        bankAccountService.create(branch.getBankAccount());
    }

    private void validateExistingReferences(Branch branch) {
        establishmentService.findById(branch.getHeadOffice().getId());
        bankAccountService.findById(branch.getBankAccount().getId());
        personService.findById(branch.getPerson().getId());
    }
}
