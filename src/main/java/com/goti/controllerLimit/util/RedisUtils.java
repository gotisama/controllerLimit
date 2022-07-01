package com.goti.controllerLimit.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.log.Log;
import cn.hutool.setting.Setting;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisUtils
 * @Description redis工具
 * @Author goti
 * @Date 11:56 2022/6/30
 * @Version 1.0
 **/
@Slf4j
public class RedisUtils {
    public static RedisDS redisDS;

    /**
     * 获取jedis对象
     *
     * @return jedis对象
     */
    public static Jedis getJedis() {
        if (redisDS == null) {
            String ymlName = SpringUtil.getActiveProfile();
            if (ObjectUtil.isNotEmpty(ymlName)) {
                String config = StrUtil.format("classpath:config/redis-{}.setting", ymlName);
                if (FileUtil.isFile(config)) {
                    log.info("使用了 {}", config);
                    Setting setting = new Setting(config);
                    redisDS = RedisDS.create(setting, null);
                } else {
                    log.info("未使用自定义配置文件");
                    redisDS = RedisDS.create();
                }

            } else {
                log.info("未使用自定义配置文件");
                redisDS = RedisDS.create();
            }
        }
        log.info("未使用自定义配置文件");
        return redisDS.getJedis();
    }


    /**
     * 设置接口限制信息
     *
     * @param key     键名称
     * @param value   限制次数
     * @param seconds 过期时间
     */
    public static void setLimit(String key, String value, Long seconds) {
        Jedis jedis = getJedis();
        try {
            SetParams setParams = new SetParams();
            setParams.ex(seconds);
            setParams.nx();
            jedis.set(key, value, setParams);
            jedis.expire(key, seconds);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }

    /**
     * 检查接口是否限制
     *
     * @param key     键名称
     * @param value   限制次数
     * @param seconds 过期时间
     */
    public static boolean checkLimit(String key, int value, Long seconds) {
        Jedis jedis = getJedis();
        try {
            if (jedis.exists(key)) {
                String num = jedis.get(key);
                if (ObjectUtil.isEmpty(num)) {
                    setLimit(key, 1 + "", seconds);
                } else {
                    int intNum = Integer.parseInt(num);
                    int maxNum = 1;
                    if (ObjectUtil.isEmpty(value)) {
                        maxNum = value;
                    }
                    if (intNum == maxNum) {
                        return false;
                    } else {
                        setLimit(key, (intNum + 1) + "", seconds);
                        return true;
                    }
                }
            } else {
                setLimit(key, 1 + "", seconds);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return false;
    }

    /**
     * 获取接口限制信息
     *
     * @param key 键名称
     * @return 限制次数
     */
    public static Long getLimitTime(String key) {
        Jedis jedis = getJedis();
        Long l = jedis.ttl(key);
        jedis.close();
        if (l == -2) {
            return 0L;
        } else {
            return l;
        }
    }


}
