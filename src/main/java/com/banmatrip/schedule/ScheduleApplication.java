package com.banmatrip.schedule;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author jepson
 * @Description:
 * @create 2017-11-05 18:10
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
@SpringBootApplication
@ComponentScan("com.banmatrip.schedule")
@MapperScan("com.banmatrip.schedule.repository")
public class ScheduleApplication {
    public static void main(String args[]) {
        SpringApplication.run(ScheduleApplication.class,args);
    }
}