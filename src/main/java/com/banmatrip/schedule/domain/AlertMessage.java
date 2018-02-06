package com.banmatrip.schedule.domain;

import lombok.Data;

/**
 * @author jepson
 * @Description: 预警消息
 * @create 2017-11-12 19:39
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
@Data
public class AlertMessage {

    /**消息ID**/
    private String messageId;
    /**消息类型ID**/
    private String messageTypeId;
    /**接收人ID**/
    private String receiveId;
    /**发送渠道**/
    private String messageSource;
    /**消息类型**/
    private String messageType;
    /**消息状态**/
    private String messageStatus;
    /**预警时间**/
    private String warnTime;
}