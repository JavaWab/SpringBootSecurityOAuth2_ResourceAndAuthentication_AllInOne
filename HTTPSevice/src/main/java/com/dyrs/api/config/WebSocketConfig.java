package com.dyrs.api.config;

import com.dyrs.api.service.RedisMsgService;
import com.dyrs.api.websocket.MyWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.Arrays;

@Configuration
public class WebSocketConfig {
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MyWebSocket myWebSocket;

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

    @Bean
    public RedisMessageListenerContainer container() {
        RedisMsgService listener = new RedisMsgService(redisTemplate.getStringSerializer(), myWebSocket);
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(listener, Arrays.asList(new PatternTopic("Online"), new PatternTopic("Offline")));

        return container;
    }
}
