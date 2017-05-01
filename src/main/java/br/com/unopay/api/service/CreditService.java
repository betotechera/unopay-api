package br.com.unopay.api.service;

import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.model.Credit;
import br.com.unopay.api.repository.CreditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CreditService {

    private CreditRepository repository;
    private HirerService hirerService;

    @Autowired
    public CreditService(CreditRepository repository,
                         HirerService hirerService) {
        this.repository = repository;
        this.hirerService = hirerService;
    }

    public Credit insert(Credit credit) {
        credit.validate();
        credit.setupMyCreate();
        updateBalances(credit);
        hirerService.findByDocumentNumber(credit.getHirerDocument());
        log.info("Insert credit value={} from hirer={}, available balance={}, block balance={}", credit.getValue(),
                credit.getHirerDocument(), credit.getAvailableBalance(), credit.getBlockedBalance());
        return repository.save(credit);
    }

    private void updateBalances(Credit credit) {
        Optional<Credit> lastCredit = repository.findFirstByOrderByCreatedDateTimeDesc();
        credit.incrementAvailableBalance(lastCredit);
        credit.incrementBlockedBalance(lastCredit);
        log.info("Inserted balance value: {}, Last credit id={}", credit.getAvailableBalance(),lastCredit.orElse(null));
    }

    public Credit findById(String id) {
        return repository.findOne(id);
    }
}