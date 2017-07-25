package br.com.unopay.api.job;

import java.util.Date;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Slf4j
@Component
public class UnopayScheduler {

    private Scheduler scheduler;

    @Autowired
    public UnopayScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @SneakyThrows
    public  <JOB extends Job> void schedule(String key, Date at, Class<JOB> jobClass) {
        log.info("Schedule job={} with key={} at={}", jobClass.getSimpleName(), key, at);
        JobDetail job = detail(key, jobClass);
        Trigger trigger = trigger(key, jobClass.getSimpleName(), at);
        schedule(job, trigger);
    }

    @SneakyThrows
    public  <JOB extends Job> void schedule(String key, String  cronPattern, Class<JOB> jobClass) {
        log.info("Schedule job={} with key={} and pattern={}", jobClass.getSimpleName(), key, cronPattern);
        JobDetail job = detail(key, jobClass);
        Trigger trigger = trigger(key, jobClass.getSimpleName(), cronPattern);
        schedule(job, trigger);
    }

    @SneakyThrows
    private void schedule(JobDetail job, Trigger trigger) {
        if (scheduler.checkExists(job.getKey())) {
            scheduler.deleteJob(job.getKey());
        }
        scheduler.scheduleJob(job, trigger);
    }

    private  <JOB extends Job> JobDetail detail(String id, Class<JOB> jobClass) {
        return newJob(jobClass)
                .withIdentity(id, jobClass.getSimpleName())
                .build();
    }

    private Trigger trigger(String id, String group, Date scheduledAt) {
        return newTrigger()
                .withIdentity(id, group)
                .startAt(scheduledAt)
                .build();
    }

    private Trigger trigger(String id, String group, String cronPattern) {
        return newTrigger()
                .withIdentity(id, group)
                .withSchedule(cronSchedule(cronPattern))
                .build();
    }

}
