package com.banmatrip.schedule.factory;

import com.banmatrip.schedule.domain.ScheduleJob;
import com.banmatrip.schedule.repository.ScheduleJobMapper;
import com.banmatrip.schedule.util.SpringContextUtil;
import com.banmatrip.schedule.util.UUIDGenerator;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jepson
 * @Description:
 * @create 2017-11-05 20:04
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
@DisallowConcurrentExecution
@Component
public class QuartzJobFactory {

    private static Logger logger = LoggerFactory.getLogger(QuartzJobFactory.class);

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SpringContextUtil springContextUtil;

    // 主工作需要保持名称唯一
    public static List jobNames = new ArrayList();


    public static String getScheduleJobName(String jobName) {
        if (jobNames.contains(jobName)) { // 如果队列中有id，说明其子任务需要执行
            return jobName;
        }
        return null;
    }

    /**
     * @Note : 扫面数据库,查看是否有计划任务的变动
     */
    @PostConstruct
    public void arrageScheduleJob() {
        try {
            // 循环队列取出目标工作对象
            List<ScheduleJob> jobList = scheduleJobMapper.getScheduleJobs();
            if (jobList.size() != 0) {
                for (ScheduleJob job : jobList) {
                    // Keys are composed of both a name and group, and the name  must be unique within the group
                    TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
                    // 获取trigger
                    CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
                    // 不存在，创建一个
                    if (null == trigger) {
                        createSheduler(scheduler, job);
                    } else {// Trigger已存在，那么更新相应的定时设置
                        updateScheduler(scheduler, job, triggerKey, trigger);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新相应的定时设置 根据job_status做相应的处理
     *
     * @param scheduler
     * @param job
     * @param triggerKey
     * @param trigger
     * @throws SchedulerException
     */
    public static void updateScheduler(Scheduler scheduler, ScheduleJob job, TriggerKey triggerKey, CronTrigger trigger)
            throws SchedulerException {
        if (job.getJobStatus().equals("1")) {// 0禁用 1启用
            if (!trigger.getCronExpression().equalsIgnoreCase(job.getCronExpression())) {
                // 表达式调度构建器
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                // 按新的trigger重新设置job执行
                scheduler.rescheduleJob(triggerKey, trigger);
                logger.info(job.getJobGroup() + "." + job.getJobName() + " 更新完毕,目前cron表达式为:" + job.getCronExpression()
                        + " isSpringBean：" + job.getIsSpringBean() + " concurrent: " + job.getConcurrent());
            }
        } else if (job.getJobStatus().equals("0")) {
            scheduler.pauseTrigger(triggerKey);// 停止触发器
            scheduler.unscheduleJob(triggerKey);// 移除触发器
            scheduler.deleteJob(trigger.getJobKey());// 删除任务
            logger.info(job.getJobGroup() + "." + job.getJobName() + "删除完毕");
        }

    }

    /**
     * 创建一个定时任务，并做安排
     *
     * @param scheduler
     * @param job
     * @throws SchedulerException
     * @throws Exception
     */
    public void createSheduler(Scheduler scheduler, ScheduleJob job) throws Exception {
        // 在工作状态可用时,即job_status = 1 ,开始创建
        if (job.getJobStatus().equals("1")) {
            // 新建一个基于Spring的管理Job类
            MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
            // 设置Job名称
            methodInvokingJobDetailFactoryBean.setName(job.getJobName());
            // 定义的任务类为Spring的定义的Bean则调用 getBean方法
            if (job.getIsSpringBean().equals("1")) {// 是Spring中定义的Bean
                Object o = springContextUtil.getApplicationContext().getBean(job.getTargetObject());
                methodInvokingJobDetailFactoryBean
                        .setTargetObject(springContextUtil.getApplicationContext().getBean(job.getTargetObject()));
            } else {// 不是
                methodInvokingJobDetailFactoryBean.setTargetObject(Class.forName(job.getClazz()).newInstance());
            }
            // 设置任务方法
            methodInvokingJobDetailFactoryBean.setTargetMethod(job.getTargetMethod());
            methodInvokingJobDetailFactoryBean.setConcurrent(job.getConcurrent().equals("1") ? true : false);
            // 将管理Job类提交到计划管理类
            methodInvokingJobDetailFactoryBean.afterPropertiesSet();
            /** 并发设置 */

            JobDetail jobDetail = methodInvokingJobDetailFactoryBean.getObject();// 动态
            jobDetail.getJobDataMap().put("scheduleJob", job);
            //jobName存入到队列 每隔一段时间就会扫描所以需要时检测
            if (!jobNames.contains(job.getJobName())) {
                jobNames.add(job.getJobName());
            }
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression()) ;
            // 按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup())
                    .withSchedule(scheduleBuilder).build();
            scheduler.scheduleJob(jobDetail, trigger);// 注入到管理类
            logger.info(job.getJobGroup() + "." + job.getJobName() + "创建完毕");
        }
    }


    /**
     * 创建子任务
     *
     * @param job
     * @throws Exception
     */
    public void createChildJob(ScheduleJob job) throws Exception {
        job.setJobName(UUIDGenerator.getUUID() + job.getJobName()); //只是当前任务的名字而已，暂时的标记作用，不影响配置
        MethodInvokingJobDetailFactoryBean methodInvJobDetailFB = new MethodInvokingJobDetailFactoryBean();
        // 设置Job组名称
        methodInvJobDetailFB.setGroup(job.getJobGroup());
        // 设置Job名称
        methodInvJobDetailFB.setName(job.getJobName()); // 注意设置的顺序，如果在管理Job类提交到计划管理类之后设置就会设置不上
        // 定义的任务类为Spring的定义的Bean则调用 getBean方法
        if (job.getIsSpringBean().equals("1")) {// 是Spring中定义的Bean
            methodInvJobDetailFB
                    .setTargetObject(springContextUtil.getApplicationContext().getBean(job.getTargetObject()));
        } else {// 不是就直接new
            methodInvJobDetailFB.setTargetObject(Class.forName(job.getClazz()).newInstance());
        }
        // 设置任务方法
        methodInvJobDetailFB.setTargetMethod(job.getTargetMethod());
        // 将管理Job类提交到计划管理类
        methodInvJobDetailFB.afterPropertiesSet();

        /** 并发设置 */
        methodInvJobDetailFB.setConcurrent(job.getConcurrent().equals("1") ? true : false);

        JobDetail jobDetail = (JobDetail) methodInvJobDetailFB.getObject();// 动态
        jobDetail.getJobDataMap().put("scheduleJob", job);
        // 不按照表达式
        Trigger trigger = TriggerBuilder.newTrigger()
                // 保证键值不一样
                .withIdentity(job.getJobName(), job.getJobGroup())
                .build();
        /**
         原理：
         因为是立即执行,没有用到表达式，所以按照实际的调度创建顺序依次执行
         */
        scheduler.standby(); //暂时停止 任务都安排完之后统一启动 解决耗时任务按照顺序部署后执行紊乱的问题
        scheduler.scheduleJob(jobDetail, trigger);// 注入到管理类
    }

    /**
     * 更新定时任务
     *
     * @param job
     * @param del
     */
    public void updateScheduler(ScheduleJob job, String del) {
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
        CronTrigger trigger;
        try {
            trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (job.getJobStatus().equals("1")) {// 0禁用 1启用
                if (null == trigger) {
                    ScheduleJob createjob = scheduleJobMapper.findbyId(job.getId());
                    createjob.setJobStatus("1");
                    createSheduler(scheduler, createjob);
                    scheduleJobMapper.update(createjob);
                } else {// Trigger已存在，那么更新相应的定时设置
                    if (!trigger.getCronExpression().equalsIgnoreCase(job.getCronExpression())) {
                        // 表达式调度构建器
                        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                        // 按新的cronExpression表达式重新构建trigger
                        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                        // 按新的trigger重新设置job执行
                        scheduler.rescheduleJob(triggerKey, trigger);
                        scheduleJobMapper.update(job);
                        logger.info(job.getJobGroup() + "." + job.getJobName() + " 更新完毕,目前cron表达式为:" + job.getCronExpression()
                                + " isSpringBean：" + job.getIsSpringBean() + " concurrent: " + job.getConcurrent());
                    }
                }

            } else if (job.getJobStatus().equals("0")) {
                scheduler.pauseTrigger(triggerKey);// 停止触发器
                scheduler.unscheduleJob(triggerKey);// 移除触发器
                scheduler.deleteJob(trigger.getJobKey());// 删除任务
                if ("1".equals(del)) {
                    scheduleJobMapper.deletebyId(job.getId());
                } else {
                    scheduleJobMapper.update(job);
                }

                logger.info(job.getJobGroup() + "." + job.getJobName() + "删除完毕");
            }
        } catch (Exception e) {
            logger.error(job.getJobGroup() + "." + job.getJobName() + "删除失败");
            logger.error("updateScheduler异常：{}", e);
        }


    }
}

