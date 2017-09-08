package com.dyrs.api.service;

import com.dyrs.api.websocket.MyWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//import java.util.concurrent.ExecutorService;

public class RedisMsgService implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(RedisMsgService.class);
    private /*ExecutorService*/ Executor executor = Executors.newFixedThreadPool(100 * Runtime.getRuntime().availableProcessors());
    private RedisSerializer<String> redisSerializer;
    private MyWebSocket myWebSocket;

    public RedisMsgService(RedisSerializer<String> redisSerializer, MyWebSocket myWebSocket) {
        this.redisSerializer = redisSerializer;
        this.myWebSocket = myWebSocket;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        byte[] b_body = message.getBody();
        byte[] b_channel = message.getChannel();
        String body = redisSerializer.deserialize(b_body);
        String channel = redisSerializer.deserialize(b_channel);
        LOG.info(channel + "\t" + body);
        executor.execute(() -> {
            if ("Offline".equals(channel)) {
                String jid_username = body.split("@")[0];
                try {
                    myWebSocket.notifyClient(jid_username);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                LOG.info("status message is not presser");
            }
        });
    }
}
