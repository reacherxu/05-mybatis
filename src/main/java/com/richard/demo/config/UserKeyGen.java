package com.richard.demo.config;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

/**
 * @author richard.xu03@sap.com
 * @version v 0.1 2021/10/13 5:32 PM richard.xu Exp $
 */
@Component
@Qualifier("userKeyGen")
public class UserKeyGen implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append("keyGen1");
        sb.append("_").append(params[0]);
        return sb.toString();
    }
}
