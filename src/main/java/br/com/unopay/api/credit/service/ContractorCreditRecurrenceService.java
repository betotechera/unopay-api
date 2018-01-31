package br.com.unopay.api.credit.service;

import br.com.unopay.api.credit.model.ContractorCreditRecurrence;
import br.com.unopay.api.credit.repository.ContractorCreditRecurrenceRepository;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractorCreditRecurrenceService {

    private ContractorCreditRecurrenceRepository repository;

    @Autowired
    public ContractorCreditRecurrenceService(ContractorCreditRecurrenceRepository repository) {
        this.repository = repository;
    }

    public ContractorCreditRecurrence save(ContractorCreditRecurrence creditRecurrence) {
        return repository.save(creditRecurrence);
    }

    public ContractorCreditRecurrence findById(String id) {
        return repository.findOne(id);
    }

    public ContractorCreditRecurrence create(ContractorCreditRecurrence creditRecurrence) {
        creditRecurrence.setCreatedDateTime(new Date());
        return save(creditRecurrence);
    }
}
