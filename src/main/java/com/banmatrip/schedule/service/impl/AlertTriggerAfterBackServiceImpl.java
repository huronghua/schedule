package com.banmatrip.schedule.service.impl;

import com.banmatrip.schedule.domain.AlertMessage;
import com.banmatrip.schedule.domain.AlertMessageDetailVO;
import com.banmatrip.schedule.service.AlertStrategyService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jepson
 * @Description:
 * @create 2017-11-12 23:02
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
@Service
public class AlertTriggerAfterBackServiceImpl extends AbstractAlertTriggerService implements AlertStrategyService {

    /**日志句柄**/
    private static final Logger log = LoggerFactory.getLogger(AlertTriggerAfterBackServiceImpl.class);

    /**
     * 发送预警消息至MQ
     * 预留暂不调用MQ
     *
     * @param alertMessageList
     * @return
     */
    @Override
    public String sendAlertMessageToMq(List<AlertMessage> alertMessageList) {
        return null;
    }


    /**
     * 预警中
     *
     * @param
     */
    @Override
    public void processing() {
        List<AlertMessageDetailVO> afterAlertList = this.parseTriggerOrder(preposeAlertProcess(),"after");
        /**发送预警消息至至接口服务**/
        log.info("归来后预警处理.....");
        /**只对有预警消息后才做接口调用**/
        if (CollectionUtils.isNotEmpty(afterAlertList)) {
            try {
                log.info("归来后预警,预警消息条数"+afterAlertList.size()+",调用消息落地接口......开始.......");
                restTemplate.postForObject("/order/saveAlertOrderInfo",afterAlertList,String.class);
                log.info("归来后预警调用消息接口......结束......");
            } catch(Exception e) {
                log.error("调用归来后预警消息落地接口失败!" + e.getMessage());
            }
        }
    }
}