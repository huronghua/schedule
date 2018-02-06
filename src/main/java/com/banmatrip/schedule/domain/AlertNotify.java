package com.banmatrip.schedule.domain;

import lombok.Data;

import java.util.List;

/**
 * @author jepson
 * @Description:
 * @create 2017-11-12 20:10
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
@Data
public class AlertNotify {

    /**消息通知ID**/
    private String notifyId;
    /**通知用户ID**/
    private List<String> notifyUserId;
    /**配置ID**/
    private String configId;
}