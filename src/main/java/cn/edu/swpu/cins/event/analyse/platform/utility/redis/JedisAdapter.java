package cn.edu.swpu.cins.event.analyse.platform.utility.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class JedisAdapter implements InitializingBean {
    @Value("${myredis.host}")
    private String redisHost;
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool(redisHost);
    }

    public long sadd(byte[] key, byte[] value) {

        Jedis jedis = null;

        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Set<byte[]> smembers(byte[] key) {
        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            return jedis.smembers(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {

            if (jedis != null) {
                jedis.close();
            }
        }

        return null;
    }

    public Transaction multi(){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.multi();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long expire(String key,int second){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.expire(key,second);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return -1;
    }
}
