package br.com.unopay.api.config;

import java.util.Properties;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

@Configuration
class QuartzConfig {

    @Value("${quartz.delegateClass}")
    private String quartzDelegate;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(ApplicationContext applicationContext,
                                                     DataSource dataSource,
                                                     @Value("${scheduler.autoStartup:true}") Boolean schedulerAutoStartup) {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setAutoStartup(schedulerAutoStartup);
        scheduler.setDataSource(dataSource);
        scheduler.setQuartzProperties(quartzProperties());
        scheduler.setApplicationContextSchedulerContextKey("applicationContext");
        scheduler.setApplicationContext(applicationContext);
        scheduler.setOverwriteExistingJobs(true);
        scheduler.setSchedulerName("unopay");
        return scheduler;
    }

    @SneakyThrows
    public Properties quartzProperties()  {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        Properties quartzProperties = propertiesFactoryBean.getObject();
        quartzProperties.setProperty("org.quartz.jobStore.driverDelegateClass", quartzDelegate);
        return quartzProperties;
    }


}
