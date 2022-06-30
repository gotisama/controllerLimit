package com.goti.controllerLimit.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisUtils
 * @Description redis工具
 * @Author goti
 * @Date 11:56 2022/6/30
 * @Version 1.0
 **/
public class RedisUtils {
    public static RedisDS redisDS;

    /**
     * 获取jedis对象
     * @return jedis对象
     */
    public static Jedis getJedis(){
        if (redisDS==null){
            redisDS=RedisDS.create();
        }
        return redisDS.getJedis();
    }

    /**
     * 设置接口限制信息
     * @param key 键名称
     * @param value 限制次数
     * @param seconds 过期时间
     */
    public static void setLimit(String key, String value, Long seconds) {
        Jedis jedis = getJedis();
        try {
            SetParams setParams=new SetParams();
            setParams.ex(seconds);
            setParams.nx();
            jedis.set(key, value, setParams);
            jedis.expire(key, seconds);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            jedis.close();
        }
    }
    /**
     * 检查接口是否限制
     * @param key 键名称
     * @param value 限制次数
     * @param seconds 过期时间
     */
    public static boolean checkLimit(String key, int value, Long seconds) {
        Jedis jedis = getJedis();
        try {
            if(jedis.exists(key)){
                String num = jedis.get(key);
                if(ObjectUtil.isEmpty(num)){
                    setLimit(key, 1+"", seconds);
                }else {
                    int intNum = Integer.parseInt(num);
                    int maxNum = 1;
                    if (ObjectUtil.isEmpty(value)) {
                        maxNum = value;
                    }
                    if (intNum == maxNum) {
                        return false;
                    }else{
                        setLimit(key, (intNum + 1) + "", seconds);
                        return true;
                    }
                }
            }else{
                setLimit(key,  1 + "", seconds);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return false;
    }

    /**
     * 获取接口限制信息
     * @param key 键名称
     * @return 限制次数
     */
    public static Long getLimitTime(String key){
        Jedis jedis = getJedis();
        Long l=jedis.ttl(key);
        jedis.close();
        if (l==-2){
            return 0L;
        }else{
            return l;
        }
    }



}
