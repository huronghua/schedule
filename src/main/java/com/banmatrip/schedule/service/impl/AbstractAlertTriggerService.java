package com.banmatrip.schedule.service.impl;

import com.banmatrip.schedule.domain.AlertConfig;
import com.banmatrip.schedule.domain.AlertMessageDetailVO;
import com.banmatrip.schedule.domain.AlertTriggerConfig;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jepson
 * @Description: 预警触发服务
 * @create 2017-11-12 20:02
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
public abstract class AbstractAlertTriggerService {

    /**日志句柄**/
    private static final Logger log = LoggerFactory.getLogger(AbstractAlertTriggerService.class);

    private static final String ALERT_CONFIG_DYNAMIC_COST = "1";

    @Autowired
    RestTemplate restTemplate;


    /**
     * 获取出发前订单信息
     *
     * @return
     */
    public List<AlertTriggerConfig> getBeforeLeaveOrderInfo() {
        ResponseEntity<List<AlertTriggerConfig>> responseEntity = null;
        try {
            responseEntity = getListResponseEntity("/order/getBeforeLeaveOrderInfo");
        } catch(Exception e) {
            log.error("调用出发前订单信息接口失败!" + e.getMessage());
            return null;
        }
        if (responseEntity != null &&
                CollectionUtils.isNotEmpty(responseEntity.getBody())) {
            log.info("获得出发前订单信息条数" + responseEntity.getBody().size());
        }
        return responseEntity.getBody();
    }


    /***
     *  获取列表相应实体
     *
     * @param url
     * @return
     */
    private ResponseEntity<List<AlertTriggerConfig>> getListResponseEntity(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ParameterizedTypeReference<List<AlertTriggerConfig>> typeReference = new ParameterizedTypeReference<List<AlertTriggerConfig>>() {};
        return restTemplate.exchange(url, HttpMethod.GET,entity,typeReference);
    }


    /**
     * 获取归来后订单信息
     *
     * @return
     */
    public List<AlertTriggerConfig> getAfterBackOrderInfo() {
        ResponseEntity<List<AlertTriggerConfig>> responseEntity = null;
        try {
            responseEntity = getListResponseEntity("/order/getAfterBackOrderInfo");
        } catch(Exception e) {
            log.error("获取归来后订单信息失败!" + e.getMessage());
            return null;
        }
        if (responseEntity != null &&
                CollectionUtils.isNotEmpty(responseEntity.getBody())) {
            log.info("获取归来后订单信息条数" + responseEntity.getBody().size());
        }
        return responseEntity.getBody();
    }


    /**
     * 获取动态成本预警配置信息
     *
     * @param configType
     * @return
     */
    public Map<String, List<AlertConfig>> getDynamicCostAlertConfig(String configType) {
        ResponseEntity<Map<String,List<AlertConfig>>> responseEntity = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(headers);
            ParameterizedTypeReference<Map<String,List<AlertConfig>>> typeReference = new ParameterizedTypeReference<Map<String, List<AlertConfig>>>() {};
            responseEntity =
                    restTemplate.exchange("/getAlertConfigInfo",HttpMethod.GET,entity,typeReference);
        } catch(Exception e) {
            log.error("调用获取动态成本预警配置信息接口失败!" + e.getMessage());
            return null;
        }
        return responseEntity.getBody();
    }


    /**
     * 前置处理
     */
    public Map<String, List<AlertConfig>> preposeAlertProcess() {
        return this.getDynamicCostAlertConfig(ALERT_CONFIG_DYNAMIC_COST);
    }

    /**
     * 解析触发订单
     *
     * @param alertConfigMap
     * @param timeType
     */
    public List<AlertMessageDetailVO> parseTriggerOrder(Map<String, List<AlertConfig>> alertConfigMap, String timeType) {
        List<AlertTriggerConfig> alertTriggerConfigList = null;
        List<AlertMessageDetailVO> resultList = new ArrayList<AlertMessageDetailVO>();
        if ("before".equals(timeType)) {
            alertTriggerConfigList = this.getBeforeLeaveOrderInfo();
        } else {
            alertTriggerConfigList = this.getAfterBackOrderInfo();
        }
        /**根据日期订单预警**/
        if (CollectionUtils.isNotEmpty(alertTriggerConfigList)) {
            for (AlertTriggerConfig alertTriggerConfig : alertTriggerConfigList) {
                /**除数为0保护**/
                if (BigDecimal.ZERO.compareTo(alertTriggerConfig.getEstimateCost()) == 0) {
                    log.error("预估成本为0--订单号" + alertTriggerConfig.getOrderId());
                    continue;
                }
                /**阈值 = (动态成本-预估成本)/预估成本 **/
                BigDecimal ration = (alertTriggerConfig.getDynamicCost().subtract(alertTriggerConfig.getEstimateCost()))
                        .divide(alertTriggerConfig.getEstimateCost(),2,BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100L));
                /**匹配预警配置**/
                patternAlertConfig(alertConfigMap, resultList, alertTriggerConfig, ration);
            }
        }
        return resultList;
    }


    /**
     *  匹配预警配置
     *
     * @param alertConfigMap
     * @param resultList
     * @param alertTriggerConfig
     * @param ration
     */
    private void patternAlertConfig(Map<String, List<AlertConfig>> alertConfigMap, List<AlertMessageDetailVO> resultList, AlertTriggerConfig alertTriggerConfig, BigDecimal ration) {
        List<AlertConfig> levleList = alertConfigMap.get(alertTriggerConfig.getDestinationId());
        if (CollectionUtils.isNotEmpty(levleList)) {
            if (ration.compareTo(BigDecimal.ZERO) == -1) {
                /*对配置列表做从小到大排序**/
                sortAlertConfig(levleList,"asc");
                triggerAscAlamer(resultList,levleList,ration,alertTriggerConfig);
            } else {
                /**配置列表从大到小排序**/
                sortAlertConfig(levleList,"desc");
                triggerDescAlamer(resultList,levleList,ration,alertTriggerConfig);
            }
        } else {
            log.error("目的地:" + alertTriggerConfig.getDestinationId() +
                    "未做预警配置!");
        }
    }

    /**
     * 处理中
     */
    public abstract void processing();


    /**
     *  触发预警
     * @param resultList
     * @param levleList
     * @param ration
     * @param alertTriggerConfig
     */
    private void triggerAscAlamer(List<AlertMessageDetailVO> resultList,List<AlertConfig> levleList,BigDecimal ration,AlertTriggerConfig alertTriggerConfig) {
        for (AlertConfig alertConfig : levleList) {
            if (! StringUtils.isEmpty(alertConfig.getConfigValue())) {
                if (ration.compareTo(new BigDecimal(alertConfig.getConfigValue())) == -1) {
                    /**获取到对应配置**/
                    resultList.add(bulidAlertMessageDetail(alertConfig,alertTriggerConfig,ration));
                }
                /**只执行一次**/
                break;
            } else {
                log.error("预警配置值为空！" + alertConfig.getConfigValue());
            }
        }
    }

    /**
     * 触发预警
     * @param resultList
     * @param levleList
     * @param ration
     * @param alertTriggerConfig
     */
    private void triggerDescAlamer(List<AlertMessageDetailVO> resultList,List<AlertConfig> levleList,BigDecimal ration,AlertTriggerConfig alertTriggerConfig) {
        for (AlertConfig alertConfig : levleList) {
            if (! StringUtils.isEmpty(alertConfig.getConfigValue())) {
                /**如果配置值比阈值大**/
                if (ration.compareTo(new BigDecimal(alertConfig.getConfigValue()))==1) {
                    /**获取到对应配置,剔除为负的配置**/
                    if (Integer.valueOf(alertConfig.getConfigValue()).intValue() >= 0 ) {
                        resultList.add(bulidAlertMessageDetail(alertConfig,alertTriggerConfig,ration));
                        /**已经找到退出**/
                        break;
                    }
                }
            }
        }
    }

    /**
     * 配置列表排序
     * @param configList
     * @param sortType
     * @return
     */
    private void sortAlertConfig(List<AlertConfig> configList, String sortType) {
        Collections.sort(configList, new Comparator<AlertConfig>() {
            @Override
            public int compare(AlertConfig o1, AlertConfig o2) {
                if ("asc".equals(sortType)) {
                    /**升序**/
                    return  (int) (Float.parseFloat(o1.getConfigValue()) - Float.parseFloat(o2.getConfigValue()));
                } else {
                    /**降序**/
                    return (int) (Float.parseFloat(o2.getConfigValue()) - Float.parseFloat(o1.getConfigValue()));
                }
            }
        });
    }

    /**
     * 构建预警消息详情VO对象
     * @param alertConfig
     * @param alertTriggerConfig
     * @param ration
     * @return
     */
    private AlertMessageDetailVO bulidAlertMessageDetail(AlertConfig alertConfig,AlertTriggerConfig alertTriggerConfig,BigDecimal ration) {
        AlertMessageDetailVO alertMessageDetail = new AlertMessageDetailVO();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        alertMessageDetail.setConfigId(alertConfig.getConfigId());
        alertMessageDetail.setOrderId(alertTriggerConfig.getOrderId());
        alertMessageDetail.setAlertTime(sdf.format(Calendar.getInstance().getTime()));
        alertMessageDetail.setDynamicCost(alertTriggerConfig.getDynamicCost());
        alertMessageDetail.setEstimateCost(alertTriggerConfig.getEstimateCost());
        alertMessageDetail.setCostDifference(alertTriggerConfig.getDynamicCost().subtract(alertTriggerConfig.getEstimateCost()));
        alertMessageDetail.setCostDifferenceRate(ration.toString());
        return alertMessageDetail;
    }
}