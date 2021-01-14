/**
 * SAP Inc.
 * Copyright (c) 1972-2020 All Rights Reserved.
 */
package com.richard.demo.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author richard.xu03@sap.com
 * @version $Id: HelloWorld.java, v 0.1 May 23, 2020 3:31:18 PM richard.xu Exp $
 */
@Controller
public class HelloWorldController {

    /**
     * http://localhost:8080/hello
     * 
     * @return
     */
    @RequestMapping("/hello")
    @ResponseBody
    public Map<String, Object> helloWorld() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("msg", "ok");
        return map;
    }
}
