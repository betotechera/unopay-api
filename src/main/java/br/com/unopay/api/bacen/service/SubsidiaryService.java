package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Subsidiary;
import br.com.unopay.api.bacen.repository.SubsidiaryRepository;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.unopay.api.uaa.exception.Errors.SUBSIDIARY_NOT_FOUND;

@Service
public class SubsidiaryService {

    @Autowired
    private SubsidiaryRepository repository;

    @Autowired
    private PersonService personService;

    @Autowired
    private EstablishmentService establishmentService;

    @Autowired
    private BankAccountService bankAccountService;

    public Subsidiary create(Subsidiary subsidiary) {
        subsidiary.validateCreate();
        createReferences(subsidiary);
        validateExistingReferences(subsidiary);
        return repository.save(subsidiary);
    }

    public void update(String id, Subsidiary subsidiary) {
        subsidiary.validateUpdate();
        Subsidiary current = findById(id);
        validateExistingReferences(subsidiary);
        current.setTechnicalContact(subsidiary.getTechnicalContact());
        repository.save(current);
    }

    public Subsidiary findById(String id) {
        Subsidiary subsidiary = repository.findOne(id);
        if(subsidiary == null) throw UnovationExceptions.notFound().withErrors(SUBSIDIARY_NOT_FOUND);
        return subsidiary;
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
    }

    public List<Subsidiary> findByMatrixId(String matrixId) {
        return repository.findByMatrixId(matrixId);
    }
}
