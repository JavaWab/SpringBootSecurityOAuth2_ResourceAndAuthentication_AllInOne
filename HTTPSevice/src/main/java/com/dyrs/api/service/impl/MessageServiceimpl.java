package com.dyrs.api.service.impl;

import com.dyrs.api.dao.MessageDAO;
import com.dyrs.api.entity.IMMessage;
import com.dyrs.api.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class MessageServiceimpl implements MessageService {
    private static final Logger LOG = LoggerFactory.getLogger(MessageServiceimpl.class);
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MessageDAO messageDAO;

    @Override
    public List<IMMessage> getMessageHistory(int page, String from, String to) {
        String key = "message:" + makeNewString(from, to);
        Long size = redisTemplate.opsForZSet().zCard(key);
        Long redis_pages = size % 30 > 0 ? (size / 30) + 1 : size / 30;
        //Redis里可以满足
        List<IMMessage> messageList = new ArrayList<>();
        if (page <= redis_pages) {
            Set<String> sets = redisTemplate.opsForZSet().rangeByScore(key, 0, System.currentTimeMillis(), (page - 1) * 30, 30);
            for (String set : sets) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    IMMessage message = objectMapper.readValue(set, IMMessage.class);
                    messageList.add(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            messageList = messageDAO.findAllByFromUIDaAndToUID(from, to, (page - redis_pages.intValue() - 1) * 30, 30);
        }
        return messageList;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Override
    public void persistentMessages() {
        Set<String> keys = redisTemplate.keys("message:*");
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                BoundZSetOperations<String, String> zSetOps = redisTemplate.boundZSetOps(key);
                Long size = zSetOps.size();
                int num = (int) (size % 1000 > 0 ? ((size / 100) + 1) : (size / 1000));
                ObjectMapper objectMapper = new ObjectMapper();
                for (int i = 1; i <= num; i++) {
                    Set<String> results = zSetOps.rangeByLex(new RedisZSetCommands.Range().gte(size), new RedisZSetCommands.Limit().offset((i - 1) * 1000).count(1000));
                    List<IMMessage> messageList = new ArrayList<>(1000);
                    for (String rst : results) {
                        try {
                            IMMessage message = objectMapper.readValue(rst, IMMessage.class);
                            messageList.add(message);
                        } catch (IOException e) {
                            LOG.warn(rst + " is not JsonObject " + e.getMessage());
                        }
                    }
                    //批量插入数据库
                    messageDAO.save(messageList);
                }
                //删除redis聊天记录
                redisTemplate.delete(key);
            }
        }

    }

    @Override
    public int get7MessagesCount(String from, String to) {
        String key = "message:" + makeNewString(from, to);
        Long size = redisTemplate.opsForZSet().zCard(key);
        /*
        *java中对日期的加减操作
        *gc.add(1,-1)表示年份减一.
        *gc.add(2,-1)表示月份减一.
        *gc.add(3.-1)表示周减一.
        *gc.add(5,-1)表示天减一.
        *以此类推应该可以精确的毫秒吧.没有再试.大家可以试试.
        *GregorianCalendar类的add(int field,int amount)方法表示年月日加减.
        *field参数表示年,月.日等.
        */
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        gc.add(3, -1);
        int db_count = messageDAO.countAllByromUIDaAndToUID(from, to, new java.sql.Date(gc.getTimeInMillis()));
        return size.intValue() + db_count;
    }

    private String makeNewString(String s1, String s2) {
        String[] strings = new String[]{s1, s2};
        Arrays.sort(strings);
        return String.join(":", strings);
    }
}
