/**
 * SAP Inc.
 * Copyright (c) 1972-2020 All Rights Reserved.
 */
package com.richard.demo.entity;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 *
 * @author richard.xu03@sap.com
 * @version $Id: User.java, v 0.1 May 31, 2020 5:00:38 PM richard.xu Exp $
 */
@Data
public class User {

    private Integer id;
    @NotBlank
    private String name;
    private Integer age;

}
