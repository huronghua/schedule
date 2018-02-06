package com.banmatrip.schedule.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @author jepson
 * @Description:
 * @create 2017-11-05 22:03
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
@Configuration
public class QuartzConfigure {

    /**
     * attention:
     * Details：定义quartz调度工厂
     */
    @Bean(name = "scheduler")
    public SchedulerFactoryBean schedulerFactory() {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        // 用于quartz集群,QuartzScheduler 启动时更新己存在的Job
        bean.setOverwriteExistingJobs(true);
        // 延时启动，应用启动1秒后
        //bean.setStartupDelay(1);
        return bean;
    }
}