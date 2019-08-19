package org.seckill.exception;

/**
 * @author ：leesom
 * @date ：Created in 2019-08-19 22:23
 * @description：重复秒杀异常（运行期异常）
 * @modified By：
 * @version: $
 */
public class RepeatKillException extends RuntimeException{

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
