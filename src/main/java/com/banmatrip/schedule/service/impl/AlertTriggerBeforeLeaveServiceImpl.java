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
 * @create 2017-11-12 22:50
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
@Service
public class AlertTriggerBeforeLeaveServiceImpl extends AbstractAlertTriggerService implements AlertStrategyService{

    /**日志句柄**/
    private static final Logger log = LoggerFactory.getLogger(AlertTriggerBeforeLeaveServiceImpl.class);

    /**
     * 发送预警消息至MQ
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
     */
    @Override
    public void processing() {
        List<AlertMessageDetailVO> beforeAlertList =  this.parseTriggerOrder(preposeAlertProcess(),"before");
        log.info("出发前预警处理.....");
        /**发送预警消息至接口服务**/
        if (CollectionUtils.isNotEmpty(beforeAlertList)) {
            try {
                log.info("出发前订单消息预警，预警消息条数"+beforeAlertList.size() + "调用预警消息落地接口......开始......");
                restTemplate.postForObject("/order/saveAlertOrderInfo",beforeAlertList,String.class);
                log.info("出发前订单消息预警，调用预警消息落地接口......结束......");
            } catch(Exception e) {
                log.error("调用出发前预警消息落地接口失败!" + e.getMessage());
            }
        }
    }
}