package com.banmatrip.schedule.service;

import com.banmatrip.schedule.domain.AlertMessage;

import java.util.List;

/**
 *  预警策略
 */
public interface AlertStrategyService {

    /**
     *  发送预警消息至MQ
     * @param alertMessageList
     * @return
     */
    public String sendAlertMessageToMq(List<AlertMessage> alertMessageList);

}
