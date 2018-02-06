package com.banmatrip.schedule.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author jepson
 * @Description: 预警触发配置
 * @create 2017-11-12 20:07
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
@Data
public class AlertTriggerConfig {

    /**订单ID**/
    private String orderId;
    /**目的地**/
    private String destinationId;
    /**动态成本**/
    private BigDecimal dynamicCost;
    /**预估成本**/
    private BigDecimal estimateCost;
}