package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Subsidiary;
import br.com.unopay.api.bacen.repository.SubsidiaryRepository;
import br.com.unopay.api.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubsidiaryService {

    private SubsidiaryRepository repository;
    private PersonService personService;
    private EstablishmentService establishmentService;
    private BankAccountService bankAccountService;

    @Autowired
    public SubsidiaryService(BankAccountService bankAccountService,
                             EstablishmentService establishmentService,
                             PersonService personService,
                             SubsidiaryRepository repository){
        this.repository = repository;
        this.bankAccountService = bankAccountService;
        this.establishmentService = establishmentService;
        this.personService = personService;

    }

    public Subsidiary create(Subsidiary subsidiary) {
        subsidiary.validateCreate();
        createReferences(subsidiary);
        validateExistingReferences(subsidiary);
        return repository.save(subsidiary);
    }

    public void update(String id, Subsidiary subsidiary) {
        Subsidiary current = findById(id);
        subsidiary.validateUpdate(current);
        validateExistingReferences(subsidiary);
        current.setTechnicalContact(subsidiary.getTechnicalContact());
        repository.save(current);
    }

    public Subsidiary findById(String id) {
        return  repository.findById(id);
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    private void createReferences(Subsidiary subsidiary) {
        personService.save(subsidiary.getPerson());
        bankAccountService.create(subsidiary.getBankAccount());

    }

    private void validateExistingReferences(Subsidiary subsidiary) {
        establishmentService.findById(subsidiary.getMatrix().getId());
        bankAccountService.findById(subsidiary.getBankAccount().getId());
        personService.findById(subsidiary.getPerson().getId());
    }

    public List<Subsidiary> findByMatrixId(String matrixId) {
        return repository.findByMatrixId(matrixId);
    }
}
