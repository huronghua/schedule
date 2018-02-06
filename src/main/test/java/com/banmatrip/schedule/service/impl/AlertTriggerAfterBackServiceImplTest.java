package com.banmatrip.schedule.service.impl;

import com.banmatrip.schedule.domain.AlertConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AlertTriggerAfterBackServiceImplTest {

    @Test
    public void test1_sortAlertConfig() throws Exception {
       List<AlertConfig> configList = new ArrayList<AlertConfig>();
        AlertConfig alertConfig1 = new AlertConfig();
        alertConfig1.setConfigId("1");
        alertConfig1.setConfigValue("-5");
        AlertConfig alertConfig2 = new AlertConfig();
        alertConfig2.setConfigId("2");
        alertConfig2.setConfigValue("5");
        AlertConfig alertConfig3 = new AlertConfig();
        alertConfig3.setConfigId("3");
        alertConfig3.setConfigValue("10");
        AlertConfig alertConfig4 = new AlertConfig();
        alertConfig4.setConfigId("4");
        alertConfig4.setConfigValue("15");
        configList.add(alertConfig1);
        configList.add(alertConfig2);
        configList.add(alertConfig3);
        configList.add(alertConfig4);
        sortAlertConfig(configList,"desc");
        for (AlertConfig alertConfig : configList) {
            System.out.println(alertConfig.getConfigValue());
        }
    }

    @Test
    public void test2_processing() throws Exception {

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
                    //return o1.getConfigValue().compareTo(o2.getConfigValue());
                    return  (int) (Float.parseFloat(o1.getConfigValue()) - Float.parseFloat(o2.getConfigValue()));
                } else {
                    return (int) (Float.parseFloat(o2.getConfigValue()) - Float.parseFloat(o1.getConfigValue()));
                   // return o2.getConfigValue().compareTo(o1.getConfigValue());
                }
            }
        });
    }
}