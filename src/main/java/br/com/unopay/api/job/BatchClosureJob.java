package br.com.unopay.api.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;

@Slf4j
@DisallowConcurrentExecution
public class BatchClosureJob extends Job {

    @Override
    void execute(JobDetail jobDetail) {
        log.info("running batch closure job");
    }
}
