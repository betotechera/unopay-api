package br.com.unopay.api.service;

import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractInstallment;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.repository.ContractInstallmentRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import javax.transaction.Transactional;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_INSTALLMENTS_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_INSTALLMENT_NOT_FOUND;

@Service
public class ContractInstallmentService {

    private ContractInstallmentRepository repository;

    @Autowired
    public ContractInstallmentService(ContractInstallmentRepository repository) {
        this.repository = repository;
    }


    @Transactional
    public void create(Contract contract) {
        ContractInstallment firstInstallment = save(new ContractInstallment(contract));
        final Date[] previousDate = { firstInstallment.getExpiration() };
        final int[] previousNumber = { firstInstallment.getInstallmentNumber() };
        IntStream.rangeClosed(2, contract.getPaymentInstallments()).forEach(n->{
            ContractInstallment installment = new ContractInstallment(contract);
            installment.plusExpiration(previousDate[0]);
            installment.incrementNumber(previousNumber[0]);
            save(installment);
            previousDate[0] = installment.getExpiration();
            previousNumber[0] = installment.getInstallmentNumber();
        });
    }

    public ContractInstallment save(ContractInstallment installment) {
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


    public Set<ContractInstallment> findByContractId(String contractId) {
        Set<ContractInstallment> installments = repository.findByContractId(contractId);
        if(installments.isEmpty()){
            throw UnovationExceptions.notFound().withErrors(CONTRACT_INSTALLMENTS_NOT_FOUND);
        }
        return installments;
    }

    public void deleteByContract(String contractId) {
        Set<ContractInstallment> byContractId = findByContractId(contractId);
        repository.delete(byContractId);
    }

    public void markAsPaid(String contractId, BigDecimal paid) {
        Set<ContractInstallment> installments = findByContractId(contractId);
        ContractInstallment installment = installments.stream().filter(inst -> inst.getPaymentDateTime() == null)
                .findFirst().orElseThrow(UnovationExceptions::unprocessableEntity);
        installment.setPaymentValue(paid);
        installment.setPaymentDateTime(new DateTime().withMillisOfDay(0).toDate());
        update(installment.getId(), installment);
    }
}
