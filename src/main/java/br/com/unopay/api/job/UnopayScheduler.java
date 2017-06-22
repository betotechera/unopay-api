package br.com.unopay.api.job;

import java.util.Date;
import lombok.SneakyThrows;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UnopayScheduler {

    private Scheduler scheduler;

    private static final String EVERY_DAY = "0 0 3 ? * MON-FRI *";
    private static final String WEEKLY = "0 0 3 ? * MON *";
    private static final String BIWEEKLY = "0 3 0 1,15 * ?";
    private static final String EVERY_MONTH = "0 0 3 ? * MON#1 *";

    @Autowired
    public UnopayScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @SneakyThrows
    public  <JOB extends Job> void schedule(String key, Date at, Class<JOB> jobClass) {
        JobDetail job = detail(key, jobClass);
        SimpleTrigger trigger = trigger(key, jobClass.getSimpleName(), at);
        schedule(job, trigger);
    }

    @SneakyThrows
    public  <JOB extends Job> void schedule(String key, String  cronPattern, Class<JOB> jobClass) {
        JobDetail job = detail(key, jobClass);
        SimpleTrigger trigger = trigger(key, jobClass.getSimpleName(), cronPattern);
        schedule(job, trigger);
    }

    private  <JOB extends Job> JobDetail detail(String id, Class<JOB> jobClass) {
        return newJob(jobClass)
                .withIdentity(id, jobClass.getSimpleName())
                .build();
    }

    private SimpleTrigger trigger(String id, String group, Date scheduledAt) {
        return (SimpleTrigger) newTrigger()
                .withIdentity(id, group)
                .startAt(scheduledAt)
                .build();
    }

    private SimpleTrigger trigger(String id, String group, String cronPattern) {
        return (SimpleTrigger) newTrigger()
                .withIdentity(id, group)
                .withSchedule(cronSchedule(cronPattern))
                .build();
    }

    @SneakyThrows
    private void schedule(JobDetail job, SimpleTrigger trigger) {
        if (scheduler.checkExists(job.getKey())) {
            scheduler.deleteJob(job.getKey());
        }
        scheduler.scheduleJob(job, trigger);
    }
}
