/**
 * SAP Inc.
 * Copyright (c) 1972-2020 All Rights Reserved.
 */
package com.richard.demo.service;

import java.util.List;
import com.richard.demo.pojo.User;

/**
 *
 * @author richard.xu03@sap.com
 * @version $Id: UserService.java, v 0.1 May 31, 2020 5:20:46 PM richard.xu Exp $
 */
public interface UserService {

    void addUser(User user);

    List<User> findAll();

    User findUserById(Integer id);

    void updateUser(User user);
}
