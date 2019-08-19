package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * 配置Spring和Junit整合，junit启动时加载springIOC容器
 * spring-test，junit
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() {
        long id = 1001L;
        long userPhone = 13199897789L;

        int insertCount =  successKilledDao.insertSuccessKilled(id,userPhone);

        System.out.println("insertCount:"+insertCount);

        /***
         * 19:43:04.497 [main] DEBUG o.m.s.t.SpringManagedTransaction - JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@95e33cc] will not be managed by Spring
         * 19:43:04.506 [main] DEBUG o.s.d.S.insertSuccessKilled - ==>  Preparing: insert ignore into success_killed (seckill_id,user_phone) values (?,?);
         * 19:43:04.566 [main] DEBUG o.s.d.S.insertSuccessKilled - ==> Parameters: 1000(Long), 13199897789(Long)
         * 19:43:04.578 [main] DEBUG o.s.d.S.insertSuccessKilled - <==    Updates: 1
         * 19:43:04.586 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5a9f4771]
         *
         * 第一次：insertCount:1
         * 联合主键，不允许重复插入
         * 第二次：insertCount:0
         */
    }

    @Test
    public void queryByIdWithSeckill() {
        long id = 1001L;
        long userPhone = 13199897789L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id,userPhone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());

        /***
         * 20:08:20.286 [main] DEBUG o.m.s.t.SpringManagedTransaction - JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@600b9d27] will not be managed by Spring
         * 20:08:20.315 [main] DEBUG o.s.d.S.queryByIdWithSeckill - ==>  Preparing: select sk.seckill_id, sk.user_phone, sk.create_time, sk.state, s.seckill_id "seckill.seckill_id", s.name "seckill.name", s.number "seckill.number", s.start_time "seckill.start_time", s.end_time "seckill.end_time", s.create_time "seckill.create_time" from success_killed sk inner join seckill s on sk.seckill_id = s.seckill_id where sk.seckill_id = ? and sk.user_phone = ?;
         * 20:08:20.367 [main] DEBUG o.s.d.S.queryByIdWithSeckill - ==> Parameters: 1000(Long), 13199897789(Long)
         * 20:08:20.408 [main] DEBUG o.s.d.S.queryByIdWithSeckill - <==      Total: 1
         * 20:08:20.415 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5a9f4771]
         * SuccessKilled{seckillId=1000, userPhone=13199897789, state=-1, createTime=Mon Aug 19 19:43:04 JST 2019}
         * Seckill{seckillId=1000, name='9.9元秒杀iPhoneXS', number=100, startTime=Tue Aug 20 00:00:00 JST 2019, endTime=Wed Aug 21 00:00:00 JST 2019, createTime=Sat Aug 17 23:59:58 JST 2019}
         */
    }
}