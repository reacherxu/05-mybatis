package com.richard.demo.service;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.richard.demo.entity.PushEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * @author richard.xu03@sap.com
 * @version v 0.1 2021/9/14 4:03 PM richard.xu Exp $
 */
@Slf4j
@Service
public class EventListenerDemo {

    @EventListener
    @Async
    public void eventListener(PushEvent event) {
        log.info(this.getClass().getSimpleName() + "监听到数据：" + event.getMsg() + " thread numer is " + Thread.currentThread().getId());
    }
}
