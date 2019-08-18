package org.seckill.dao;

import org.seckill.entity.SuccessKilled;

/**
 * @author ：leesom
 * @date ：Created in 2019-08-18 09:34
 * @description：
 * @modified By：
 * @version: $
 */
public interface SuccessKilledDao {

    /**
     * 插入购买明细，可过滤重复
     * @param seckillId
     * @param userPhone
     * @return 插入的行数
     */
    int insertSuccessKilled(long seckillId, long userPhone);

    /**
     * 根据Id查询SuccessKilled并携带秒杀产品对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(long seckillId);
}
