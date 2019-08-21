package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                      "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> seckills = seckillService.getSeckillList();
        logger.info("list = {}",seckills);

        //Closing non transactional SqlSession
        //不是在事务控制下，因为本身是只读
    }

    @Test
    public void getById() {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}",seckill);

        //Closing non transactional SqlSession
        //不是在事务控制下，因为本身是只读
    }

    @Test
    public void exportSeckillUrl() {

        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);

        //为什么Exposer返回的MD5一直是null
        //应该是你的数据createTime没有在startTime-endTime之间，改完之后应该就可以了

        //Exposer{
        // exposed=false,
        // md5='null',
        // seckillId=1000,
        // now=1566383527488,
        // start=1566226800000,
        // end=1566313200000}

        //Exposer{
        // exposed=true,
        // md5='a03132cace2a2e39e1df00c7332d8c57',
        // seckillId=1000,
        // now=0, start=0, end=0}
    }

    @Test
    public void executeSeckill() {

        //同一条数据重复执行会抛异常：seckill repeated
        long id = 1000;
        long phone = 13456468890L;
        String md5 = "a03132cace2a2e39e1df00c7332d8c57";

        SeckillExecution seckillExecution = seckillService.executeSeckill(id,phone,md5);
        logger.info("seckillExecution={}",seckillExecution);

        /**
         * 19:46:33.020 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Creating a new SqlSession
         * 19:46:33.025 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e0f9063]
         * 19:46:33.031 [main] DEBUG o.m.s.t.SpringManagedTransaction - JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@758705fa] will be managed by Spring
         * 19:46:33.036 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - ==>  Preparing: update seckill set number = number - 1 where seckill_id = ? and start_time <= ? and end_time >= ? and number > 0;
         * 19:46:33.067 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - ==> Parameters: 1000(Long), 2019-08-21 19:46:32.997(Timestamp), 2019-08-21 19:46:32.997(Timestamp)
         * 19:46:33.070 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - <==    Updates: 1
         * 19:46:33.070 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e0f9063]
         * 19:46:33.070 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e0f9063] from current transaction
         * 19:46:33.071 [main] DEBUG o.s.d.S.insertSuccessKilled - ==>  Preparing: insert ignore into success_killed (seckill_id,user_phone,state) values (?,?,0);
         * 19:46:33.072 [main] DEBUG o.s.d.S.insertSuccessKilled - ==> Parameters: 1000(Long), 13456468890(Long)
         * 19:46:33.073 [main] DEBUG o.s.d.S.insertSuccessKilled - <==    Updates: 1
         * 19:46:33.080 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e0f9063]
         * 19:46:33.081 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e0f9063] from current transaction
         * 19:46:33.083 [main] DEBUG o.s.d.S.queryByIdWithSeckill - ==>  Preparing: select sk.seckill_id, sk.user_phone, sk.create_time, sk.state, s.seckill_id "seckill.seckill_id", s.name "seckill.name", s.number "seckill.number", s.start_time "seckill.start_time", s.end_time "seckill.end_time", s.create_time "seckill.create_time" from success_killed sk inner join seckill s on sk.seckill_id = s.seckill_id where sk.seckill_id = ? and sk.user_phone = ?;
         * 19:46:33.083 [main] DEBUG o.s.d.S.queryByIdWithSeckill - ==> Parameters: 1000(Long), 13456468890(Long)
         * 19:46:33.098 [main] DEBUG o.s.d.S.queryByIdWithSeckill - <==      Total: 1
         * 19:46:33.106 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e0f9063]
         * 19:46:33.107 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e0f9063]
         * 19:46:33.107 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e0f9063]
         * 19:46:33.107 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e0f9063]
         * 19:46:33.110 [main] INFO  o.seckill.service.SeckillServiceTest - seckillExecution=SeckillExecution{seckillId=1000, state=1, stateInfo='秒杀成功', successKilled=SuccessKilled{seckillId=1000, userPhone=13456468890, state=0, createTime=Wed Aug 21 19:46:33 JST 2019}}
         */
    }

    //集成测试
    @Test
    public void testSeckillLogic() throws Exception{

        long id = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            logger.info("exposer={}",exposer);
            long phone = 13456468890L;
            String md5 = exposer.getMd5();
            try{
                SeckillExecution seckillExecution = seckillService.executeSeckill(id,phone,md5);
                logger.info("seckillExecution={}",seckillExecution);
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }

        }else{
            //秒杀未开始
            logger.warn("exposer={}",exposer);
        }
    }
}