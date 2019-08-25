--秒杀执行的存储过程
DELIMITER $$ -- console 将 ;  转换为  $$
--定义存储过程
--参数
--  in 输入参数，可以被使用
--  out 输出参数，不可以被使用，可以被赋值
--row_count() 返回上一条修改sql（delete，insert，update）的修改行数
--row_count:  0:未修改数据  >0:修改的行数  <0:sql错误/未执行sql
CREATE PROCEDURE `seckill`.`execute_seckill`
    (in v_seckill_id bigint,in v_phone bigint,
    in v_kill_time timestamp,out r_result int)
    BEGIN
        DECLARE insert_count int DEFAULT 0;
        START TRANSACTION;
        INSERT ignore INTO success_killed
            (seckill_id,user_phone,create_time)
            values (v_seckill_id,v_phone,v_kill_time);
        SELECT row_count() into insert_count;
        IF (insert_count = 0) THEN
            ROLLBACK ;
            SET r_result = -1;
        ELSEIF (insert_count < 0) THEN
            ROLLBACK ;
            SET r_result = -2;
        ELSE
            UPDATE seckill
            SET number = number - 1
            WHERE seckill_id = v_seckill_id
                AND end_time > v_kill_time
                AND start_time < v_kill_time
                AND number > 0;
            SELECT row_count() into insert_count;
                    IF (insert_count = 0) THEN
                        ROLLBACK ;
                        SET r_result = 0;
                    ELSEIF (insert_count < 0) THEN
                        ROLLBACK ;
                        SET r_result = -2;
                    ELSE
                        COMMIT ;
                        SET r_result = 1;
                    END IF;
        END IF;
    END ;
$$
--存储过程定义结束

DELIMITER ;

SET @r_result = -3;
--执行存储过程
call execute_seckill(1003,13133233345,now(),@r_result);

--获取结果
SELECT @r_result;

--存储过程
--存储过程使事务的行级锁持有时间尽可能的短