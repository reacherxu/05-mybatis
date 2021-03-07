package com.richard.demo.scheduling;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

/**
 * @author richard.xu03@sap.com
 * @version v 0.1 2021/3/7 5:19 PM richard.xu Exp $
 */
@Slf4j
public class VisitTask implements Runnable {
    @Override
    public void run() {
        log.info("welcome to Richard's space, time is " + LocalDateTime.now().toLocalTime());
    }
}
