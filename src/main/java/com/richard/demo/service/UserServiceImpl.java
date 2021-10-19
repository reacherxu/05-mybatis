/**
 * SAP Inc.
 * Copyright (c) 1972-2020 All Rights Reserved.
 */
package com.richard.demo.service;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.richard.demo.dao.UserMapper;
import com.richard.demo.entity.FileDeletionEvent;
import com.richard.demo.entity.PushEvent;
import com.richard.demo.entity.User;
import com.richard.demo.entity.UserRegisterEvent;
import com.richard.demo.util.CompactAlgorithm;
import com.richard.demo.util.FileUtil;
import com.richard.demo.util.RedisCacheManager;
import com.richard.demo.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author richard.xu03@sap.com
 * @version $Id: UserServiceImpl.java, v 0.1 May 31, 2020 5:22:02 PM richard.xu Exp $
 */
@Service
@Slf4j
@EnableAsync
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ApplicationContext applicationContext;


    @Autowired
    private RedisUtil redisUtil;

    /**
     * @see com.richard.demo.service.UserService#addUser(com.richard.demo.entity.User)
     */
    @Override
    @Transactional
    public void addUser(User user) {
        userMapper.insertUser(user);
    }

    @Override
    @Transactional
    public void addUserWithExceptionRollback(User user) {
        userMapper.insertUser(user);
        int i = 1 / 0;
    }

    /**
     * this means even this exception happens, will not rollback the database operations
     * 
     * @param user
     */
    @Override
    @Transactional(noRollbackFor = ArithmeticException.class)
    public void addUserWithExceptionNoRollback(User user) {
        userMapper.insertUser(user);
        int i = 1 / 0;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User addUserAyncEvent(User user) {
        userMapper.insertUser(user);
        log.info("processing transaction..........Thread number is {} ", Thread.currentThread().getId());
        User foundUser = userMapper.findUserByName(user.getName());
        eventPublisher.publishEvent(new UserRegisterEvent(this, new Date(), user));
        return foundUser;
    }

    /**
     * 值得注意的是，如果发射事件的地方没有事务，这里需要在注解中加一个参数：fallbackExecution = true
     * 
     * @param user
     * @return
     */
    @Override
    public User addUserWithoutTransaction(User user) {
        userMapper.insertUser(user);
        log.info("no transaction, processing transaction..........Thread number is {} ", Thread.currentThread().getId());
        User foundUser = userMapper.findUserByName(user.getName());
        eventPublisher.publishEvent(new UserRegisterEvent(this, new Date(), user));
        return foundUser;
    }

    @Override
    public Resource fileDeletion() {
        // test sr hierarchy
        // parent folder
        String fileName = new Date().toString();
        String root = FileUtil.getTempLoaction() + fileName;
        FileUtil.createDirectory(new File(root));

        String rootFolder = root + "/" + "ServiceRegistry";
        FileUtil.createDirectory(new File(rootFolder));
        String serviceMappingPath = rootFolder + "/ServiceMapping.json";
        String errorPath = rootFolder + "/Error.json";

        File srcFile = new File(rootFolder);
        File outFile = new File(root + "/" + fileName + ".zip");

        try {
            String mapping = FileUtil.prepareMapping();
            FileUtil.writeFile(mapping, serviceMappingPath);
            FileUtil.writeFile(new JsonObject().toString(), errorPath);

            // sub folder
            Gson gson = new Gson();
            String serviceFolder = rootFolder + "/Services";
            FileUtil.createDirectory(new File(serviceFolder));
            String serviceJson = serviceFolder + "/service1.json";
            String service = FileUtil.prepareService();
            FileUtil.writeFile(gson.toJson(service), serviceJson);


            String cdtFolder = rootFolder + "/ComplexDataTypes";
            FileUtil.createDirectory(new File(cdtFolder));
            String cdtJson = cdtFolder + "/cdt1.json";
            String cdt = FileUtil.prepareCdt();
            FileUtil.writeFile(gson.toJson(cdt), cdtJson);
            log.info("completing hierarchy....");

            log.info("starting zipping....");

            CompactAlgorithm.zipFiles(srcFile, outFile);
            log.info("completing zipping....Thread number is {} ", Thread.currentThread().getId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        eventPublisher.publishEvent(new FileDeletionEvent(this, Arrays.asList(new File(root))));

        return new FileSystemResource(outFile.getPath());
    }

    @Override
    public void eventListener() {
        String msg = "测试数据";
        log.info("发布信息：{}, thread numer is {}", msg, Thread.currentThread().getId());
        ApplicationEvent event = new PushEvent(this, msg);
        applicationContext.publishEvent(event);
    }

    @Async
    // @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    // 如果没有事务的话 ，推荐使用event listener 来实现异步
    @EventListener
    public void onFileDeletionEvent(FileDeletionEvent event) {
        try {
            // to prevent file deletion before return
            Thread.sleep(5000);
            log.info("Thread number is {} ", Thread.currentThread().getId());
            event.getFiles().forEach(file -> FileUtils.deleteQuietly(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Spring的发布订阅模型实际上并不是异步的，而是同步的来将代码进行解耦。
     * 如果需要起异步线程，类上要加注解 @EnableAsync, 也不会影响上一个transaction
     * 
     * @param event
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onUserRegisterAfterCommit(UserRegisterEvent event) {
        try {
            Thread.sleep(5 * 1000);
            log.info("Transaction committed....Thread number is {} ", Thread.currentThread().getId());
            int i = 10 / 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendDate(Date date) {
        log.info("Date now is {}", date.toString());
    }

    /**
     * @see com.richard.demo.service.UserService#findAll()
     */
    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    /**
     * 
     * Cacheable常用的三个参数如下：
     * cacheNames 缓存名称
     * key 缓存的key，需要注意key的写法,可以指定多个cache
     * condition 缓存执行的条件，返回true时候执行
     * 
     * @see com.richard.demo.service.UserService#findUserById(java.lang.Integer)
     */
    @Override

    @Cacheable(value = {"cache_user", "cache_user_2"}, keyGenerator = "userKeyGen", unless = "#result == null")
    public User findUserById(Integer id) {
        redisUtil.set("backup_cache_user::keyGen1_" + id, "file" + id);
        return userMapper.findUserById(id);
    }


    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    /**
     * CacheEvict 标记在方法上，方法执行完毕之后根据条件或key删除对应的缓存
     *
     * @param user
     */
    @Override
    @CachePut(cacheNames = "cache_user", keyGenerator = "userKeyGen")
    public User updateUserInCache(Integer id, User user) {
        userMapper.updateUser(user);
        return userMapper.findUserById(id);
    }

    @Override
    @CacheEvict(cacheNames = "cache_user", keyGenerator = "userKeyGen")
    public void deleteUser(Integer id) {
        userMapper.deleteUser(id);
    }


    @Autowired
    private RedisCacheManager redisCacheManager;

    @Override
    public String getCacheByKey(String cacheName, String key, String type) throws ClassNotFoundException {
        Cache cache = redisCacheManager.getCache(cacheName);
        return cache.get(key, Class.forName(type).getDeclaringClass()).toString();
    }


    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Collection<String> getAllCacheKeys(String cacheName) {
        Collection<String> cacheKeys = redisTemplate.keys(cacheName + "*");
        return cacheKeys;
    }


    public String getCacheKey(String cacheMethodName, String... params) {
        StringBuilder cacheKey = new StringBuilder();
        log.info("[RedisCacheManager#getCacheKey] Cache key: {}.", cacheKey.toString());
        cacheKey.append("keyGen1");
        cacheKey.append("_").append(params[0]);
        return cacheKey.toString();
    }

}
