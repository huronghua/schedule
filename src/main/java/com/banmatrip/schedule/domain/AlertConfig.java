package com.banmatrip.schedule.domain;

import lombok.Data;

import java.util.List;

/**
 * @author jepson
 * @Description: 预警参数设置
 * @create 2017-11-12 20:29
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
@Data
public class AlertConfig {

    private String configId;
    private String destinationId;
    private String configName;
    private String configValue;
    private String configType;
    private List<String> notifyUserId;
}