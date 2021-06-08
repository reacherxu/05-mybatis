package com.richard.demo.entity;

import java.util.Date;

import org.springframework.context.ApplicationEvent;

/**
 * @author richard.xu03@sap.com
 * @version v 0.1 2021/5/28 3:13 PM richard.xu Exp $
 */
public class UserRegisterEvent extends ApplicationEvent {
    private Date registerDate;

    private User user;

    /**
     * Create a new ApplicationEvent.
     *
     * @param registerDate the object on which the event initially occurred (never {@code null})
     */
    public UserRegisterEvent(Object source, Date registerDate, User user) {
        super(source);
        this.registerDate = registerDate;
        this.user = user;
    }
}
