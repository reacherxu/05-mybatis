/**
 * SAP Inc.
 * Copyright (c) 1972-2020 All Rights Reserved.
 */
package com.richard.demo.service;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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
import com.richard.demo.entity.User;
import com.richard.demo.entity.UserRegisterEvent;
import com.richard.demo.util.CompactAlgorithm;
import com.richard.demo.util.FileUtil;

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

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
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
