package br.com.unopay.api.service;

import br.com.unopay.api.model.ContractInstallment;
import br.com.unopay.api.repository.ContractInstallmentRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_INSTALLMENT_NOT_FOUND;

@Service
public class ContractInstallmentService {

    private ContractInstallmentRepository repository;

    @Autowired
    public ContractInstallmentService(ContractInstallmentRepository repository) {
        this.repository = repository;
    }

    public ContractInstallment create(ContractInstallment installment) {
        return repository.save(installment);
    }

    public void update(String id, ContractInstallment installment) {
        ContractInstallment current = findById(id);
        current.updateMe(installment);
        repository.save(current);
    }

    public ContractInstallment findById(String id) {
        Optional<ContractInstallment> installment = repository.findById(id);
        return installment.orElseThrow(()->UnovationExceptions.notFound().withErrors(CONTRACT_INSTALLMENT_NOT_FOUND));
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

}
