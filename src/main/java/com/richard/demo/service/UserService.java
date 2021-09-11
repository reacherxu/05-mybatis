/**
 * SAP Inc.
 * Copyright (c) 1972-2020 All Rights Reserved.
 */
package com.richard.demo.service;

import java.util.Date;
import java.util.List;

import org.springframework.core.io.Resource;

import com.richard.demo.entity.User;

/**
 *
 * @author richard.xu03@sap.com
 * @version $Id: UserService.java, v 0.1 May 31, 2020 5:20:46 PM richard.xu Exp $
 */
public interface UserService {

    void addUser(User user);

    // test roll back
    void addUserWithExceptionRollback(User user);

    void addUserWithExceptionNoRollback(User user);

    // test event listener
    User addUserAyncEvent(User user);

    // test event listener without transaction
    User addUserWithoutTransaction(User user);

    Resource fileDeletion();

    void sendDate(Date date);


    List<User> findAll();

    User findUserById(Integer id);

    void updateUser(User user);
}
