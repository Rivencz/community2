package com.nowcoder.community2.service;

import com.nowcoder.community2.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    //    将访客对应ip添加到UVkey中
    public void recordUV(String ip) {
//        根据日期计算出对应的key
        String redisKey = RedisKeyUtil.getUVKey(sdf.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    //    获取一个日期范围内的UV数量
    public long calculateUV(Date start, Date end) {

        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
//        保存日期范围中的所有key
        List<String> keyList = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String key = RedisKeyUtil.getUVKey(sdf.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE, 1);
        }

//        将日期区间中的所有key中内容合并到一个key中
        String unionKey = RedisKeyUtil.getUVKey(sdf.format(start), sdf.format(end));
        redisTemplate.opsForHyperLogLog().union(unionKey, keyList.toArray());

        return redisTemplate.opsForHyperLogLog().size(unionKey);
    }

    //   将活跃用户的id添加到DAUkey中
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtil.getDAUKey(sdf.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }

    //    根据开始和结束时间统计该区间中的DAU即活跃用户的数量
    public long calculateDAU(Date start, Date end) {
//        首先获取该区间中的key集合，然后通过OR与运算来获取所有true的数量
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
//        保存日期范围中的所有key
        List<byte[]> keyList = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String key = RedisKeyUtil.getDAUKey(sdf.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE, 1);
        }

        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(sdf.format(start), sdf.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });
    }
}
