package br.com.unopay.api.job;

import br.com.unopay.api.payment.service.PaymentRemittanceService;
import br.com.unopay.api.service.BatchClosingService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;

@Slf4j
@DisallowConcurrentExecution
public class RemittanceJob extends Job {

    @Override
    void execute(JobDetail jobDetail) {
        String issuerId = jobDetail.getKey().getName();
        log.info("running remittance job for issuer={}", issuerId);
        getBean(PaymentRemittanceService.class).create(issuerId);
        log.info("finished remittance job for issuer={}", issuerId);
    }
}
