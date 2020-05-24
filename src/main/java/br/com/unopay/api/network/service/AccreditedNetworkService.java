package br.com.unopay.api.network.service;

import br.com.unopay.api.bacen.service.BankAccountService;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.filter.PaymentInstrumentFilter;
import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.network.model.filter.AccreditedNetworkFilter;
import br.com.unopay.api.network.repository.AccreditedNetworkRepository;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccreditedNetworkService {

    private AccreditedNetworkRepository repository;
    private UserDetailService userDetailService;
    private PersonService personService;
    private BankAccountService bankAccountService;


    @Autowired
    public AccreditedNetworkService(AccreditedNetworkRepository repository,
                                    UserDetailService userDetailService,
                                    PersonService personService,
                                    BankAccountService bankAccountService) {
        this.repository = repository;
        this.userDetailService = userDetailService;
        this.personService = personService;
        this.bankAccountService = bankAccountService;
    }

    public AccreditedNetwork create(AccreditedNetwork accreditedNetwork) {
        try {
            personService.createOrUpdate(accreditedNetwork.getPerson());
            bankAccountService.create(accreditedNetwork.getBankAccount());
            return repository.save(accreditedNetwork);
        } catch (DataIntegrityViolationException e){
            log.warn(String.format("Person institution already exists %s", accreditedNetwork.getPerson()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.PERSON_ACCREDITED_NETWORK_ALREADY_EXISTS);

        }
    }

    public Page<AccreditedNetwork> findByFilter(AccreditedNetworkFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public AccreditedNetwork getById(String id) {
        Optional<AccreditedNetwork> accreditedNetwork = repository.findById(id);
        return accreditedNetwork
                .orElseThrow(()-> UnovationExceptions.notFound().withErrors(Errors.ACCREDITED_NETWORK_NOT_FOUND));
    }
    public void update(String id, AccreditedNetwork accreditedNetwork) {
        AccreditedNetwork current = repository.findOne(id);
        current.updateModel(accreditedNetwork);
        personService.createOrUpdate(accreditedNetwork.getPerson());
        bankAccountService.update(current.getBankAccount().getId(),current.getBankAccount());
        repository.save(current);
    }

    public void delete(String id) {
        getById(id);
        if(userDetailService.hasNetwork(id)){
            throw UnovationExceptions.conflict()
                    .withErrors(Errors.ACCREDITED_NETWORK_WITH_USERS);
        }

        repository.delete(id);
    }

    public List<AccreditedNetwork> listForMenu() {
        AccreditedNetworkFilter filter = new AccreditedNetworkFilter();
        UnovationPageRequest pageable = new UnovationPageRequest();
        pageable.setSize(50);
        return findByFilter(filter, pageable).getContent();
    }

}
