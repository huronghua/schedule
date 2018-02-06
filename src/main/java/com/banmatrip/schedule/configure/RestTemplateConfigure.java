package com.banmatrip.schedule.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author jepson
 * @Description:
 * @create 2017-11-21 9:28
 * @Copyright: 2017 www.banmatrip.com All rights reserved.
 **/
@Configuration
public class RestTemplateConfigure {

    @Value("${client.root.uri}")
    private String rootUri;
    /**数据读取超时时间**/
    @Value("${client.read.timeout}")
    private int readTimeout;
    /**连接超时时间**/
    @Value("${client.connect.timeout}")
    private int connectTimeout;


    @Value("${client.report.root.uri}")
    private String reportRootUri;
    /**数据读取超时时间**/
    @Value("${client.report.read.timeout}")
    private int reportReadTimeout;
    /**连接超时时间**/
    @Value("${client.report.connect.timeout}")
    private int reportConnectTimeout;

    /**
     * 实例化Bean配置供spring容器调用
     *
     * @return
     */
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.rootUri(rootUri)
                .setConnectTimeout(connectTimeout)
                .setReadTimeout(readTimeout)
                .build();
    }


    /**
     * 供报表调用restTemplate
     *
     * @return
     */
    @Bean
    RestTemplate reportRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.rootUri(reportRootUri)
                .setConnectTimeout(reportConnectTimeout)
                .setReadTimeout(reportReadTimeout)
                .build();
    }
}