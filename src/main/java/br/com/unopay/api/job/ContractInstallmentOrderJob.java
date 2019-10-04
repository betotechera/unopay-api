package br.com.unopay.api.job;

import br.com.unopay.api.service.ContractInstallmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContractInstallmentOrderJob {

    private ContractInstallmentService contractInstallmentService;
    @Autowired
    public ContractInstallmentOrderJob(ContractInstallmentService contractInstallmentService) {
        this.contractInstallmentService = contractInstallmentService;
    }

    @Scheduled(cron = "0 10 3 ? * *", zone = "GMT-3")
    void execute() {
        log.info("running contract installment order job");
        contractInstallmentService.createOrders();
        log.info("finished contract installment order job.");
    }
}
