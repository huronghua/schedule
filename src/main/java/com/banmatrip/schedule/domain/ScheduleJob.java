package com.banmatrip.schedule.domain;


import lombok.Data;

/**
 * @author jepson
 * @Description: 任务信息
 * @create 2017-11-05 17:58
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
@Data
public class ScheduleJob {

    /** 任务ID */
    private String Id;
    /** 任务名称 */
    private String jobName;
    /** 任务分组 */
    private String jobGroup;
    /** 任务状态 0禁用 1启用 2删除*/
    private String jobStatus;
    /** 任务运行时间表达式 */
    private String cronExpression;
    /** 任务描述 */
    private String description;
    /** 任务类 */
    private String targetObject;
    /** 任务方法 */
    private String targetMethod;
    /** 是否是Spring中定义的Bean */
    private String isSpringBean;
    /** 如果isSpringBean = 0需要设置全类名,测试CLAZZ字段需要配置 */
    private String clazz;
    /** 是否并发 0禁用 1启用 */
    private String concurrent;
    /** 一系列的子任务,逗号分开,表示该任务执行完，之后需要执行的任务 */
    private String childJobs;

}