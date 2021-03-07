/**
 * SAP Inc.
 * Copyright (c) 1972-2020 All Rights Reserved.
 */
package com.richard.demo.dao;

import java.util.List;

import com.richard.demo.entity.User;

/**
 *
 * @author richard.xu03@sap.com
 * @version $Id: UserMapper.java, v 0.1 May 31, 2020 5:16:55 PM richard.xu Exp $
 */
public interface UserMapper {

    void insertUser(User user);

    List<User> findAll();

    User findUserById(Integer id);

    void updateUser(User user);

}
