package com.banmatrip.schedule.service.impl;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AlertTriggerBeforeLeaveServiceImplTest {
    @Test
    public void test1_sendAlertMessageToMq() throws Exception {
        assertNull("object is null",null);
    }

    @Test
    public void test2_processing() throws Exception {
        assertEquals("error",3,3);
    }

}