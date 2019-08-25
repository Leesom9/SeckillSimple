package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;

import java.util.List;

/**
 * 业务接口：站在“使用者”的角度去设计接口
 * 1，方法定义粒度
 * 2，参数
 * 3，返回类型
 */
public interface SeckillService {

    /***
     * 查询所有秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /***
     * 秒杀开启时输出秒杀地址
     * 否则输出系统时间和开始秒杀时间
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /***
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SecurityException, RepeatKillException, SeckillCloseException;

    /***
     * 执行秒杀操作  by  存储过程
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);
}
