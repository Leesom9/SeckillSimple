package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：leesom
 * @date ：Created in 2019-08-20 19:26
 * @description：接口实现
 * @modified By：
 * @version: $
 */

//@Component 代表所有的组件，不知道其是Service，dao，controller时，使用此注解
//@Service @Dao @Controller

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //注入Service依赖
    //包括@Resource，@Inject都是j2ee规范的一些注解
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    //md5盐值字符串，用于混淆MD5
    private final String slat = "skg4546,/';dew3,][";

    public List<Seckill> getSeckillList() {

        return seckillDao.queryAll(0,4);
    }

    public Seckill getById(long seckillId) {

        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        //优化点：缓存优化  维护：超时的基础上维护一致性
        /**
         * get from cache
         * if null
         *   get DB
         * else
         *   put cache
         * logic
         */

        //1：访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);

        if(null == seckill){
            //2：访问数据库
            seckill = seckillDao.queryById(seckillId);
            if(null == seckill){
                return new Exposer(false,seckillId);
            }else{
                //3：放入redis
                redisDao.putSeckill(seckill);
            }
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();

        if(nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }

        //转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId);

        return new Exposer(true,md5,seckillId);
    }

    private String getMD5(long seckillId){
        String base = seckillId + "/" +slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());

        return md5;
    }

    @Transactional
    /***
     * 使用注解控制事务方法的优点：
     *    1：开发团队达成一致的约定，明确标注事务方法的风格。
     *    2：保证事务方法的执行时间尽可能短，不要穿插其他的网络操作 RPC/HTTP请求（执行方法时间过长）或者剥离到事务方法上层方法
     *    3：不是所有的方法都需要事务，如只有一条crud操作，只读操作。
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SecurityException, RepeatKillException, SeckillCloseException {

        if(md5 == null || !md5.equals(getMD5(seckillId))){
            throw new SecurityException("md5 data rewrite");
        }

        //执行秒杀逻辑：1，减库存。2，记录购买行为
        Date nowTime = new Date();

        try {
            //记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
            if(insertCount <= 0){
                //重复秒杀
                throw new RepeatKillException("seckill repeat");
            }else{
                //减库存,热点商品的竞争
                int updateCount = seckillDao.reduceNumber(seckillId,nowTime);
                if(updateCount <= 0){
                    //秒杀关闭，没有更新到记录  rollback
                    throw new SeckillCloseException("seckill is closed");
                }else{
                    //秒杀成功 commit
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);
                }
            }
        }
        //提前catch，逻辑允许操作之后，也可以执行rollback
        catch (SeckillCloseException e1){
            throw e1;
        }
        catch (RepeatKillException e2){
            throw e2;
        }
        catch (Exception e){
            logger.error(e.getMessage(),e);

            //将所有编译期异常转化为运行期异常
            //rollback回滚，避免减库存和插入购买记录的操作没有同时的进行
            throw new SeckillException("");
        }
    }

    /***
     * 通过java客户端，调用存储过程，完成秒杀。
     * 拿到返回的 int result 判断执行结果
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SecurityException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5){

        if(null == md5 || !md5.equals(getMD5(seckillId))){
            return new SeckillExecution(seckillId,SeckillStatEnum.DATA_REWRITE);
        }

        Date killTime = new Date();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);

        try {
            //使用map，当存储过程执行完之后，result被赋值
            seckillDao.killByProcedure(map);
            //获取result
            int result = MapUtils.getInteger(map,"result",-2);

            if(1 == result){
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,sk);
            }else{
                return new SeckillExecution(seckillId,SeckillStatEnum.stateOf(result));
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
        }
    }
}
