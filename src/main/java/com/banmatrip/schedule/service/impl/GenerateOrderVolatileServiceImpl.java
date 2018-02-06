package com.banmatrip.schedule.service.impl;

import com.banmatrip.schedule.service.GenerateOrderVolatileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author jepson
 * @Description: 订单变动生成
 * @create 2017-12-02 22:33
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
@Service
public class GenerateOrderVolatileServiceImpl implements GenerateOrderVolatileService{


    /**日志句柄**/
    private static final Logger log = LoggerFactory.getLogger(SynchronizeOrderServiceImpl.class);

    @Autowired
    RestTemplate reportRestTemplate;

    /**
     * 调度处理
     */
    @Override
    public void handleSchedule() {
        try {
            log.info("订单变动文件生成......开始......");
            reportRestTemplate.postForObject("/orderFlow/generate",null,String.class);
            log.info("订单变动文件生成......结束......");
        } catch(Exception e) {
            log.error("订单变动文件生成失败!" + e.getMessage());
        }
    }
}