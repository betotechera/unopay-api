package br.com.unopay.api.service;

import br.com.unopay.api.market.model.HirerNegotiation;
import br.com.unopay.api.market.service.HirerNegotiationService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractInstallment;
import br.com.unopay.api.repository.ContractInstallmentRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Months;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static br.com.unopay.api.model.ContractInstallment.ONE_INSTALLMENT;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_INSTALLMENTS_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACT_INSTALLMENT_NOT_FOUND;

@Slf4j
@Service
public class ContractInstallmentService {

    private ContractInstallmentRepository repository;
    private HirerNegotiationService hirerNegotiationService;
    @Setter private Date currentDate = new Date();
    @Getter private final Integer boletoDeadlineInDays;

    @Autowired
    public ContractInstallmentService(ContractInstallmentRepository repository,
                                      HirerNegotiationService hirerNegotiationService,
                                      @Value("${unopay.boleto.deadline_in_days}")Integer boletoDeadlineInDays
                                      ) {
        this.repository = repository;
        this.hirerNegotiationService = hirerNegotiationService;
        this.boletoDeadlineInDays = boletoDeadlineInDays;
    }

    @Transactional
    public void create(Contract contract) {
        ContractInstallment firstInstallment = save(new ContractInstallment(contract));
        create(firstInstallment, contract.getPaymentInstallments(), number ->
                save(new ContractInstallment(contract))
        );
    }

    @Transactional
    public void createForHirer(final Contract contract) {
        HirerNegotiation negotiation = hirerNegotiationService
                                            .findByHirerDocument(contract.hirerDocumentNumber(), contract.productId());
        ContractInstallment firstInstallment = save(new ContractInstallment(contract,negotiation, currentDate));
        final int currentInstallmentNumber = getCurrentInstallmentNumber(negotiation);
        create(firstInstallment, negotiation.getInstallments() - currentInstallmentNumber, currentNumber ->{
            ContractInstallment installment = new ContractInstallment(contract,negotiation, currentDate);
            installment.defineValue(negotiation ,currentNumber);
            return installment;
        });
    }

    private void create(final ContractInstallment firstInstallment, final int installments,
                        final Function<Integer, ContractInstallment> supplier) {
        final Date[] previousDate = { firstInstallment.getExpiration() };
        final int[] previousNumber = { firstInstallment.getInstallmentNumber() };
        IntStream.rangeClosed(2, installments).forEach(n->{
            ContractInstallment installment = supplier.apply(previousNumber[0]+ ONE_INSTALLMENT);
            installment.plusOneMonthInExpiration(previousDate[0]);
            installment.incrementNumber(previousNumber[0]);
            save(installment);
            previousDate[0] = installment.getExpiration();
            previousNumber[0] = installment.getInstallmentNumber();
        });
    }

    public ContractInstallment save(ContractInstallment installment) {
        return repository.save(installment);
    }

    public void update(final String id,final ContractInstallment installment) {
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

    public Stream<ContractInstallment> findInstallmentAboutToExpire(){
        LocalDateTime expirationDayBegin = LocalDateTime.now().plusDays(boletoDeadlineInDays)
                .withTime(0,0,0,0);
        LocalDateTime expirationDayEnd = LocalDateTime.now().plusDays(boletoDeadlineInDays)
                .withTime(23,59,59,0);
        Set<ContractInstallment> installments = repository.findInstallmentAboutToExpire(expirationDayBegin.toDate(), expirationDayEnd.toDate());
        return installments.stream().filter(installment -> installment.getContract().withIssuerAsHirer());
    }

    public Set<ContractInstallment> findAllNotPaidInstallments(){
        Set<ContractInstallment> installments = repository.findAllNotPaidInstallments();
        log.info("A total of={} installments were found to be processed", installments.size());
        return installments.stream().filter(installment -> installment.getContract().withIssuerAsHirer()).collect(Collectors.toSet());
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
        ContractInstallment installment = findFirstNotPaid(contractId);
        installment.setPaymentValue(paid);
        installment.setPaymentDateTime(new Date());
        update(installment.getId(), installment);
    }

    public ContractInstallment findFirstNotPaid(String contractId) {
        Set<ContractInstallment> installments = findByContractId(contractId);
        return installments.stream().filter(inst -> inst.getPaymentDateTime() == null)
                .min(Comparator.comparing(ContractInstallment::getInstallmentNumber)).orElseThrow(() ->
                        UnovationExceptions.unprocessableEntity()
                                .withErrors(CONTRACT_INSTALLMENT_NOT_FOUND.withOnlyArgument(contractId)));
    }

    private int getCurrentInstallmentNumber(HirerNegotiation negotiation) {
        return Months.monthsBetween(LocalDate.fromDateFields(negotiation.getEffectiveDate()),
                LocalDate.fromDateFields(currentDate)).getMonths();
    }
}
