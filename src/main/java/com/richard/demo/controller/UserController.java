/**
 * SAP Inc.
 * Copyright (c) 1972-2020 All Rights Reserved.
 */
package com.richard.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.richard.demo.entity.User;
import com.richard.demo.service.UserService;

/**
 *
 * @author richard.xu03@sap.com
 * @version $Id: UserController.java, v 0.1 May 31, 2020 5:23:20 PM richard.xu Exp $
 */
@RequestMapping("/users")
@Controller
public class UserController {
    @Autowired
    private UserService userService;

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

    @RequestMapping(value = "/addUser1", method = RequestMethod.POST)
    @ResponseBody
    public String addUser1(@RequestBody User user) {
        userService.addUser(user);
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

}
