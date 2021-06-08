/**
 * SAP Inc.
 * Copyright (c) 1972-2020 All Rights Reserved.
 */
package com.richard.demo.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.richard.demo.dao.UserMapper;
import com.richard.demo.entity.User;
import com.richard.demo.entity.UserRegisterEvent;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author richard.xu03@sap.com
 * @version $Id: UserServiceImpl.java, v 0.1 May 31, 2020 5:22:02 PM richard.xu Exp $
 */
@Service
@Slf4j
@EnableAsync
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * @see com.richard.demo.service.UserService#addUser(com.richard.demo.entity.User)
     */
    @Override
    @Transactional
    public void addUser(User user) {
        userMapper.insertUser(user);
    }

    @Override
    @Transactional
    public void addUserWithExceptionRollback(User user) {
        userMapper.insertUser(user);
        int i = 1 / 0;
    }

    /**
     * this means even this exception happens, will not rollback the database operations
     * 
     * @param user
     */
    @Override
    @Transactional(noRollbackFor = ArithmeticException.class)
    public void addUserWithExceptionNoRollback(User user) {
        userMapper.insertUser(user);
        int i = 1 / 0;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User addUserAyncEvent(User user) {
        userMapper.insertUser(user);
        log.info("processing transaction..........Thread number is {} ", Thread.currentThread().getId());
        User foundUser = userMapper.findUserByName(user.getName());
        eventPublisher.publishEvent(new UserRegisterEvent(this, new Date(), user));
        return foundUser;
    }

    /**
     * Spring的发布订阅模型实际上并不是异步的，而是同步的来将代码进行解耦。
     * 如果需要起异步线程，类上要加注解 @EnableAsync
     * 
     * @param event
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserRegisterAfterCommit(UserRegisterEvent event) {
        try {
            Thread.sleep(5 * 1000);
            log.info("Transaction committed....Thread number is {} ", Thread.currentThread().getId());
            int i = 10 / 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendDate(Date date) {
        log.info("Date now is {}", date.toString());
    }

    /**
     * @see com.richard.demo.service.UserService#findAll()
     */
    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    /**
     * @see com.richard.demo.service.UserService#findUserById(java.lang.Integer)
     */
    @Override
    public User findUserById(Integer id) {
        return userMapper.findUserById(id);
    }

    /**
     * 
     * @param user
     */
    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

}
