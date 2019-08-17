-- 数据库初始化脚本
  --创建数据库
  CREATE DATABASE seckill;

  --使用数据库
  use seckill;

  --创建秒杀库存表
  CREATE TABLE seckill(
    `seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存ID',
    `name` varchar(120) NOT NULL COMMENT '商品名称',
    `number` int NOT NULL COMMENT '库存数量',
    `start_time` timestamp NOT NULL COMMENT '秒杀开始时间',
    `end_time` timestamp NOT NULL COMMENT '秒杀结束时间',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY(seckill_id),
    key idx_start_time(start_time),
    key idx_end_time(end_time),
    key idx_create_time(create_time)
  )ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

  --MySQL：ERROR 1067 (42000): Invalid default value for 'end_time' 错误解决
  --2.错误分析
  --　　表中的第一个TIMESTAMP列（如果未声明为NULL或显示DEFAULT或ON UPDATE子句）将自动分配DEFAULT CURRENT_TIMESTAMP和ON UPDATE CURRENT_TIMESTAMP属性
  --　　第一个之后的TIMESTAMP列（如果未声明为NULL或显示DEFAULT子句）将自动分配DEFAULT '0000-00-00 00:00:00'（零时间戳），这不满足sql_mode中的NO_ZERO_DATE而报错。
  --　　注：sql_mode有两种，一种是空值，一种是严格模式，会给出很多默认设置。在MySQL5.7之后默认使用严格模式。
  --　　　　NO_ZERO_DATE：若设置该值，MySQL数据库不允许插入零日期，插入零日期会抛出错误而不是警告。
  --3.解决方式
  --　　方式一：先执行select @@sql_mode,复制查询出来的值并将其中的NO_ZERO_DATE删除，然后执行set sql_mode = '修改后的值'。
  --　　　　　　此方法只在当前会话中生效
  --　　方式二：先执行select @@global.sql_mode,复制查询出来的值并将其中的NO_ZERO_DATE删除，然后执行set global sql_mode = '修改后的值'。
  --　　　　　　此方法在当前服务中生效，重新MySQL服务后失效
  --　　方法三：在mysql的安装目录下，打开my.ini或my.cnf文件，新增 sql_mode = ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION，
  --　　　　　　然后重启mysql。
  --　　　　　　此方法永久生效

  --初始化数据
  insert into seckill
    (name,number,start_time,end_time)
  values
    ('9.9元秒杀iPhoneXS',100,'2019-08-20 00:00:00','2019-08-21 00:00:00'),
    ('9.9元秒杀MacBook',10,'2019-08-20 00:00:00','2019-08-21 00:00:00'),
    ('9.9元秒杀airpods',200,'2019-08-20 00:00:00','2019-08-21 00:00:00'),
    ('9.9元秒杀ipad pro',50,'2019-08-20 00:00:00','2019-08-21 00:00:00');

  --秒杀成功明细
  --用户登录相关的信息
  CREATE TABLE success_killed(
    `seckill_id` bigint NOT NULL COMMENT '秒杀商品ID',
    `user_phone` bigint NOT NULL COMMENT '用户手机号',
    `state` tinyint NOT NULL DEFAULT -1 COMMENT '状态标识：-1->无效，0->成功，1->已付款，2->已发货。。。 ',
    `create_time` timestamp NOT NULL COMMENT '创建时间',
    PRIMARY KEY (seckill_id,user_phone) ,/* 联合主键*/
    key idx_create_time(create_time)
  )ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

--连接数据库的控制台
mysql -uroot -p123