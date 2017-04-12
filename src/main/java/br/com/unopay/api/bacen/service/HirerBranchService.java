package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.HirerBranch;
import br.com.unopay.api.bacen.model.filter.HirerBranchFilter;
import br.com.unopay.api.bacen.repository.HirerBranchRepository;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class HirerBranchService {

    private HirerBranchRepository repository;
    private PersonService personService;
    private HirerService hirerService;
    private BankAccountService bankAccountService;

    @Autowired
    public HirerBranchService(BankAccountService bankAccountService,
                              HirerService hirerService,
                              PersonService personService,
                              HirerBranchRepository repository){
        this.repository = repository;
        this.bankAccountService = bankAccountService;
        this.hirerService = hirerService;
        this.personService = personService;

    }

    public HirerBranch create(HirerBranch branch) {
        branch.validateCreate();
        saveReferences(branch);
        validateExistingReferences(branch);
        return repository.save(branch);
    }

    public void update(String id, HirerBranch branch) {
        HirerBranch current = findById(id);
        branch.validateUpdate(current);
        validateExistingReferences(branch);
        saveReferences(branch);
        current.updateMe(branch);
        repository.save(current);
    }

    public HirerBranch findById(String id) {
        return  repository.findOne(id);
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    private void saveReferences(HirerBranch branch) {
        personService.save(branch.getPerson());
        bankAccountService.create(branch.getBankAccount());
    }

    private void validateExistingReferences(HirerBranch branch) {
        hirerService.getById(branch.getHeadOffice().getId());
        bankAccountService.findById(branch.getBankAccount().getId());
        personService.findById(branch.getPerson().getId());
    }

    public Page<HirerBranch> findByFilter(HirerBranchFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
}
