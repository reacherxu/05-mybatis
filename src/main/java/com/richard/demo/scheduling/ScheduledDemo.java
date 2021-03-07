package com.richard.demo.scheduling;


import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author richard.xu03@sap.com
 * @version v 0.1 2021/3/2 1:58 PM richard.xu Exp $
 */
@Component
@Slf4j
public class ScheduledDemo {


    @Scheduled(cron = "0 * * * * ?")
    public void scheduledMethod() {
        log.info(String.format("method A-----, date is %tc, thread id is %s " , new Date(),Thread.currentThread().getId()));
    }

    @Scheduled(cron = "0 * * * * ?")
    public void scheduledMethod2() {
        log.info(String.format("method B-----, date is %tc, thread id is %s " , new Date(),Thread.currentThread().getId()));
    }

}
