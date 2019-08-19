package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;

/**
 * @author ：leesom
 * @date ：Created in 2019-08-18 09:27
 * @description：
 * @modified By：
 * @version: $
 */
public interface SeckillDao {

    /***
     * 减库存
     * @param seckillId
     * @param killTime
     * @return 如果影响行数>1，表示更新行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /***
     * 根据ID查询秒杀对象
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /***
     * 根据偏移量查询秒杀商品列表
     *     List<Seckill> queryAll(int offet, int limit);
     *     //java不保存形参
     *     //queryAll(int offet,int limit)  ->  quaryAll(arg0, arg1)
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}
