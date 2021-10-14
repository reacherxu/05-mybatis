/**
 * SAP Inc.
 * Copyright (c) 1972-2020 All Rights Reserved.
 */
package com.richard.demo.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.richard.demo.dao.UserMapper;
import com.richard.demo.dto.PairDto;
import com.richard.demo.entity.User;
import com.richard.demo.service.UserService;
import com.richard.demo.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author richard.xu03@sap.com
 * @version $Id: UserController.java, v 0.1 May 31, 2020 5:23:20 PM richard.xu Exp $
 */
@RequestMapping("/users")
@Controller
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // visit template folder page
    @RequestMapping("/{page}")
    public String showPage(@PathVariable String page) {
        return page;
    }

    @RequestMapping("/addUser")
    public String addUser(User user) {
        userService.addUser(user);
        return "ok";
    }

    /**
     * Transactional noRollbackFor function
     * 
     * @param user
     * @return
     */
    @RequestMapping(value = "/addUser1", method = RequestMethod.POST)
    @ResponseBody
    public User addUser1(@RequestBody @Valid User user) {
        userService.addUserWithExceptionNoRollback(user);
        return userMapper.findUserByName(user.getName());
    }

    /**
     * use async method to add user
     * 
     * @param user
     * @return
     */
    @RequestMapping(value = "/addUser3", method = RequestMethod.POST)
    @ResponseBody
    public User addUser3(@RequestBody @Valid User user) {

        return userService.addUserAyncEvent(user);
    }

    /**
     * use async method to add user
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/addUser4", method = RequestMethod.POST)
    @ResponseBody
    public User addUser4(@RequestBody @Valid User user) {

        return userService.addUserWithoutTransaction(user);
    }

    @RequestMapping(value = "/addUser5", method = RequestMethod.POST)
    @ResponseBody
    public Resource addUser5() throws IOException {
        Resource resource = userService.fileDeletion();
        log.info("resource received...");

        return resource;
    }

    /**
     * test event listener
     * 
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/addUser7", method = RequestMethod.POST)
    @ResponseBody
    public void addUser7() throws IOException {
        userService.eventListener();
        log.info("resource received...");
    }

    @RequestMapping(value = "/addUser6", method = RequestMethod.POST)
    @ResponseBody
    public void addUser6(@RequestBody PairDto pairs) throws IOException {
        ImmutablePair<String, String> pair = pairs.getPairs().get(0);
        log.info("resource received...{} : {}", pair.getLeft(), pair.getRight());

    }

    @RequestMapping(value = "/addUser2", method = RequestMethod.POST)
    @ResponseBody
    public String addUser2() {
        return "ok";
    }

    @RequestMapping("/findAll")
    public String findAllUsers(Model model) {
        List<User> list = userService.findAll();
        model.addAttribute("list", list);
        return "userList";
    }

    @RequestMapping("/findAll1")
    @ResponseBody
    public List<User> findAllUsers1() {
        List<User> list = userService.findAll();
        return list;
    }

    @RequestMapping("/findCron")
    @ResponseBody
    public List<User> findAllCron() {
        List<User> list = userService.findAll();
        return list;
    }

    @RequestMapping("/findUserById")
    public String findUserById(Integer id, Model model) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        return "updateUser";
    }

    @RequestMapping(value = "/findUserById1", method = RequestMethod.GET)
    @ResponseBody
    public User findUserById1(@RequestParam(value = "id") Integer id) {
        User user = userService.findUserById(id);
        return user;
    }

    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public String updateUser(User user) {
        userService.updateUser(user);
        return "ok";
    }

    @RequestMapping(value = "/updateUser1", method = RequestMethod.PUT)
    @ResponseBody
    public String updateUser1(@RequestBody User user) {
        userService.updateUser(user);
        return "ok";
    }


    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping(value = "/findUserByIdCache", method = RequestMethod.GET)
    @ResponseBody
    public User getCache(Integer id) {

        User user = (User) redisUtil.get("cache_user::keyGen1_" + id);
        return user;
    }

}
