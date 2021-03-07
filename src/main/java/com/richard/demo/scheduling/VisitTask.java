package com.richard.demo.scheduling;

import java.time.LocalDateTime;

import com.richard.demo.enums.SchedulerType;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author richard.xu03@sap.com
 * @version v 0.1 2021/3/7 5:19 PM richard.xu Exp $
 */
@Slf4j
@Setter
@Getter
public class VisitTask implements Runnable {

    private String threadId;

    private SchedulerType type;

    public VisitTask(String id, SchedulerType type) {
        this.threadId = id;
        this.type = type;
    }

    @Override
    public void run() {
        log.info("welcome to space of {} ,time is {}, type is {}", this.threadId, LocalDateTime.now().toLocalTime(), this.type);
    }
}
