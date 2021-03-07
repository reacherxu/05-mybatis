/**
 * SAP Inc.
 * Copyright (c) 1972-2020 All Rights Reserved.
 */
package com.richard.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.richard.demo.dao.UserMapper;
import com.richard.demo.entity.User;

/**
 *
 * @author richard.xu03@sap.com
 * @version $Id: UserServiceImpl.java, v 0.1 May 31, 2020 5:22:02 PM richard.xu Exp $
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * @see com.richard.demo.service.UserService#addUser(com.richard.demo.entity.User)
     */
    @Override
    public void addUser(User user) {
        userMapper.insertUser(user);
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
