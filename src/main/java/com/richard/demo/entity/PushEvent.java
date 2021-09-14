package com.richard.demo.entity;

import org.springframework.context.ApplicationEvent;

/**
 * @author richard.xu03@sap.com
 * @version v 0.1 2021/9/14 4:01 PM richard.xu Exp $
 */
public class PushEvent extends ApplicationEvent {

    private String msg;

    public PushEvent(Object source, String msg) {
        super(source);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

