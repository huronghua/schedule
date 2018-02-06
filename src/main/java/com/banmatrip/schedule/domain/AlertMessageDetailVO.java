package com.banmatrip.schedule.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author jepson
 * @Description: 预警详情信息
 * @create 2017-11-20 15:44
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
@Data
public class AlertMessageDetailVO {

    /**订单ID**/
    private String orderId;
    /**配置ID**/
    private String configId;
    /**预估时间 年月日时分秒**/
    private String alertTime;
    /**预估成本**/
    private BigDecimal estimateCost;
    /**动态成本**/
    private BigDecimal dynamicCost;
    /**成本差异金额**/
    private BigDecimal costDifference;
    /**成本异常比**/
    private String costDifferenceRate;
}