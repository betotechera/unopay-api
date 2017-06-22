package br.com.unopay.api.job;

import lombok.SneakyThrows;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

abstract class Job extends QuartzJobBean {

    public static final String APPLICATION_CONTEXT_KEY = "applicationContext";

    JobExecutionContext context;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        this.context = context;
        execute(context.getJobDetail());
    }

    abstract void execute(JobDetail jobDetail);

    public <T> T getBean(Class<T> bean) {
        return getApplicationContext().getBean(bean);
    }

    @SneakyThrows
    private ApplicationContext getApplicationContext() {
        return (ApplicationContext) context.getScheduler().getContext().get(APPLICATION_CONTEXT_KEY);
    }

}
