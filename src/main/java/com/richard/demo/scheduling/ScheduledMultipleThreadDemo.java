package com.richard.demo.scheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.richard.demo.enums.SchedulerType;

import lombok.extern.slf4j.Slf4j;

/**
 * @author richard.xu03@sap.com
 * @version v 0.1 2021/3/7 5:28 PM richard.xu Exp $
 */
@Component
@Slf4j
public class ScheduledMultipleThreadDemo {
    private static final ExecutorService executor = Executors.newFixedThreadPool(5);


    @Scheduled(cron = "0/2 * * * * ?")
    public void mtDemo() {
        try {
            log.info("Submit polling thread ");
            executor.submit(new VisitTask(String.valueOf(Thread.currentThread().getId()), SchedulerType.MultipleThreadBased));
        } catch (Throwable t) {
            // on failure suppress log, as next cycle will pick the event
            log.error("Error while polling jobs ");
        }
    }
}
