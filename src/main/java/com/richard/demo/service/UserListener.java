package com.richard.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author richard.xu03@sap.com
 * @version v 0.1 2021/5/28 3:18 PM richard.xu Exp $
 */
@Slf4j
@Component
public class UserListener {

    @Autowired
    private UserService userService;


    // /**
    // * 消息里面 发生异常不影响上一个事务
    // *
    // * @param event
    // */
    // @Async
    // @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION, classes =
    // UserRegisterEvent.class)
    // public void onUserRegisterCompletion(UserRegisterEvent event) {
    // log.info("Transaction completed........");
    // }
    //
    // @Async
    // @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK, classes =
    // UserRegisterEvent.class)
    // public void onUserRegisterRollback(UserRegisterEvent event) {
    // log.info("Transaction rollback........");
    // }



    // @Async
    // @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, classes =
    // UserRegisterEvent.class)
    // public void onUserRegisterBeforeCommit(UserRegisterEvent event) {
    // log.info("Transaction before commit...........");
    // }

}
