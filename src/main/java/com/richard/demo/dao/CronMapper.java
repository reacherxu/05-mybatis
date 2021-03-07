package com.richard.demo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author richard.xu03@sap.com
 * @version v 0.1 2021/3/7 5:00 PM richard.xu Exp $
 */
@Mapper
@Repository
public interface CronMapper {
    @Select("select cron from cron limit 1")
    public String getCron();

}
