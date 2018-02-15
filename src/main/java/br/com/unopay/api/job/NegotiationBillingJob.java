package br.com.unopay.api.job;

import br.com.unopay.api.market.service.NegotiationBillingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NegotiationBillingJob {

    private NegotiationBillingService negotiationBillingService;

    @Autowired
    public NegotiationBillingJob(NegotiationBillingService negotiationBillingService) {
        this.negotiationBillingService = negotiationBillingService;
    }

    @Scheduled(cron = "0 0 3 ? * *")
    void execute() {
        log.info("running negotiation billing job");
        negotiationBillingService.process();
        log.info("finished negotiation billing job");
    }
}
