package org.seckill.dao.cache;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author ：leesom
 * @date ：Created in 2019/8/25 10:02
 * @description：
 * @modified By：
 * @version: $
 */
public class RedisDao {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    private final JedisPool jedisPool;

    public RedisDao(String ip, int port){
        jedisPool = new JedisPool(ip,port);
    }

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    //访问数据时，直接先访问缓存
    public Seckill getSeckill(long seckillId){

        //redis操作逻辑
        try {
            //jedisPool相当于数据库的连接池
            //jedis相当于数据库的connection
            Jedis jedis = jedisPool.getResource();

            try {
                String key = "seckill:"+seckillId;
                /**
                 * 没有实现内部序列化操作
                 * 不关心你需要的是一个什么样语言的对象或者图片，存储的都是二进制数组
                 * get->byte[]->反序列化->Object(seckill)
                 * 采用自定义序列方案
                 *
                 * 提供一个class，内部的schema描述clss结构
                 * protostuff：pojo  是拥有get set方法的对象
                 */

                byte[] bytes = jedis.get(key.getBytes());

                if(null != key){
                    //创建空对象
                    Seckill seckill = schema.newMessage();
                    //seckill被反序列化
                    ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);
                    //压缩空间，提高速度，节省cpu
                    return seckill;
                }

            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    //缓存不存在时，put一个seckill
    public String putSeckill(Seckill seckill){
        // set Object(Seckill) -> 序列化 ->byte[] (->发送给redis)

        try {
            //使用前需要启动Redis服务器 -》   redis-server
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema,
                        //缓存器，当对象特别大时，有一个缓冲的过程
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //超时缓存
                int timeout = 60 * 60; // 1h
                //正确：OK  错误：错误信息
                String result = jedis.setex(key.getBytes(),timeout,bytes);
                return result;
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
