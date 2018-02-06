package com.banmatrip.schedule.controller;

import com.banmatrip.schedule.domain.ScheduleJob;
import com.banmatrip.schedule.factory.QuartzJobFactory;
import com.banmatrip.schedule.service.ScheduleService;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jepson
 * @Description:
 * @create 2017-11-05 20:34
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
@Controller
public class ScheduleController {

    /**日志句柄**/
    private static final Logger log = LoggerFactory.getLogger(ScheduleController.class);

   @Autowired
    private ScheduleService scheduleService;

   @Autowired
   private QuartzJobFactory quartzJobFactory;

    @Autowired
    private Scheduler scheduler;

    @RequestMapping("/index.htm")
    public String toIndex(HttpServletRequest request, Model model){

        request.setAttribute("scheduleJobs", scheduleService.getScheduleJobs());
        request.setAttribute("test", "test");
        return "index";
    }

    /**
     * 启用禁用
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/action.htm")
    public String action(HttpServletRequest request,Model model){
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setId(request.getParameter("id").toString());
        scheduleJob.setJobStatus(request.getParameter("jobStatus").toString());
        scheduleJob.setJobName(request.getParameter("jobName").toString());
        scheduleJob.setJobGroup(request.getParameter("jobGroup").toString());

        quartzJobFactory.updateScheduler(scheduleJob,request.getParameter("del"));

        return "redirect:/index.htm";
    }

    /**
     * 修改表达式(启用中的任务)
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/update.htm")
    public String update(HttpServletRequest request,Model model){
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setId(request.getParameter("id").toString());
        scheduleJob.setJobStatus(request.getParameter("jobStatus").toString());
        scheduleJob.setJobName(request.getParameter("jobName").toString());
        scheduleJob.setJobGroup(request.getParameter("jobGroup").toString());
        scheduleJob.setCronExpression(request.getParameter("cronExpression").toString());

        quartzJobFactory.updateScheduler(scheduleJob,"0");

        return "redirect:/index.htm";
    }

    /**
     * 修改表达式(禁用中的任务)
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/updatecronExpression.htm")
    public String updatecronExpression(HttpServletRequest request,Model model){
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setId(request.getParameter("id").toString());

        scheduleJob.setCronExpression(request.getParameter("cronExpression").toString());
        String updatetype =request.getParameter("updatetype");
        //如果是禁用修改则直接修改
        scheduleService.update(scheduleJob);


        return "redirect:/index.htm";
    }

    /**
     * 删除任务
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/deletejob.htm")
    public String deletejob(HttpServletRequest request,Model model){
        //如果是禁用修改则直接删除
        scheduleService.deletebyId(request.getParameter("id").toString());
        return "redirect:/index.htm";
    }

    /**
     * 创建任务
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/newjob.htm")
    public String newJob(HttpServletRequest request,Model model) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobName(request.getParameter("jobName"));
        scheduleJob.setJobGroup(request.getParameter("jobGroup"));
        scheduleJob.setCronExpression(request.getParameter("cronExpression"));
        scheduleJob.setDescription(request.getParameter("description"));
        scheduleJob.setTargetObject(request.getParameter("targetObject"));
        scheduleJob.setTargetMethod(request.getParameter("targetMethod"));
        scheduleJob.setConcurrent("0");
        scheduleJob.setIsSpringBean("1");
        scheduleJob.setJobStatus("0");
        scheduleService.create(scheduleJob);
        try {
            quartzJobFactory.createSheduler(scheduler, scheduleJob);
        } catch(Exception e) {
            log.error("创建定时任务失败!" + e.getMessage());
        }
        return "redirect:/index.htm";
    }

}