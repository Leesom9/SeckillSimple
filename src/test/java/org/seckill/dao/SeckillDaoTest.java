package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置Spring和Junit整合，junit启动时加载springIOC容器
 * spring-test，junit
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    @Resource
    private SeckillDao seckillDao;

    @Test
    public void queryById() {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);

        System.out.println(seckill.getName());
        System.out.println(seckill);

        /**
         * 9.9元秒杀iPhoneXS
         * Seckill{seckillId=1000, name='9.9元秒杀iPhoneXS', number=0, startTime=Tue Aug 20 00:00:00 JST 2019, endTime=Wed Aug 21 00:00:00 JST 2019, createTime=Sat Aug 17 23:59:58 JST 2019}
         */

    }

    //Parameter 'offset' not found. Available parameters are [0, 1, param1, param2]
    //java不保存形参
    //queryAll(int offet,int limit)  ->  quaryAll(arg0, arg1)
    @Test
    public void queryAll() {
        List<Seckill> seckills = seckillDao.queryAll(0,100);
        for(Seckill seckill:seckills){
            System.out.println(seckill);
        }
        /**
         * Seckill{seckillId=1000, name='9.9元秒杀iPhoneXS', number=0, startTime=Tue Aug 20 00:00:00 JST 2019, endTime=Wed Aug 21 00:00:00 JST 2019, createTime=Sat Aug 17 23:59:58 JST 2019}
         * Seckill{seckillId=1001, name='9.9元秒杀MacBook', number=0, startTime=Tue Aug 20 00:00:00 JST 2019, endTime=Wed Aug 21 00:00:00 JST 2019, createTime=Sat Aug 17 23:59:58 JST 2019}
         * Seckill{seckillId=1002, name='9.9元秒杀airpods', number=0, startTime=Tue Aug 20 00:00:00 JST 2019, endTime=Wed Aug 21 00:00:00 JST 2019, createTime=Sat Aug 17 23:59:58 JST 2019}
         * Seckill{seckillId=1003, name='9.9元秒杀ipad pro', number=0, startTime=Tue Aug 20 00:00:00 JST 2019, endTime=Wed Aug 21 00:00:00 JST 2019, createTime=Sat Aug 17 23:59:58 JST 2019}
         */
    }

    @Test
    public void reduceNumber() {
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L,killTime);
        System.out.println("updateCount = " +updateCount);

        /***
         * 00:25:32.653 [main] DEBUG o.m.s.t.SpringManagedTransaction - JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@60410cd] will not be managed by Spring
         * 00:25:32.687 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - ==>  Preparing: update seckill set number = number - 1 where seckill_id = ? and start_time <= ? and end_time >= ? and number > 0;
         * 00:25:32.762 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - ==> Parameters: 1000(Long), 2019-08-19 00:25:31.883(Timestamp), 2019-08-19 00:25:31.883(Timestamp)
         * 00:25:32.771 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - <==    Updates: 0
         * 00:25:32.772 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@352c1b98]
         * updateCount = 0
         */
    }

}