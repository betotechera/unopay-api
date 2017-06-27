package br.com.unopay.api.job;

import br.com.unopay.api.service.BatchClosingService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;

@Slf4j
@DisallowConcurrentExecution
public class BatchClosingJob extends Job {

    @Override
    void execute(JobDetail jobDetail) {
        String establishmentId = jobDetail.getKey().getName();
        log.info("running batch closing job for establishment={}", establishmentId);
        getBean(BatchClosingService.class).create(establishmentId);
    }
}
