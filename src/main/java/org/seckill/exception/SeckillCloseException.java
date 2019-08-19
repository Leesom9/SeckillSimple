package org.seckill.exception;

/**
 * @author ：leesom
 * @date ：Created in 2019-08-19 22:26
 * @description：秒杀关闭异常（运行期异常）
 * @modified By：
 * @version: $
 */
public class SeckillCloseException extends SeckillException {

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
