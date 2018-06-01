package br.com.unopay.api.job;

import br.com.unopay.api.market.service.BonusBillingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BonusBillingJob {

    @Autowired
    BonusBillingService service;

    @Scheduled(cron = "0 0 3 ? * *")
    void execute() {
        log.info("running bonus billing job");
        service.process();
        log.info("finished bonus billing job");
    }
}
