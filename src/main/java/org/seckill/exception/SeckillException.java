package org.seckill.exception;

/**
 * @author ：leesom
 * @date ：Created in 2019-08-19 22:28
 * @description：秒杀业务相关异常
 * @modified By：
 * @version: $
 */
public class SeckillException extends RuntimeException {

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
