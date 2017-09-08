package com.dyrs.api.websocket;

import com.dyrs.api.service.CustomerService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@ServerEndpoint(
        value = "/websocket/{id}"
)
public class MyWebSocket {
    private static final Logger LOG = LoggerFactory.getLogger(MyWebSocket.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int ONLINE_COUNT = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static HashMap<String, MyWebSocket> WEB_SOCKET_SET = new HashMap<>();
    @Value("${im.customer.url}")
    private String url;
    @Value("${im.customer.secret}")
    private String secret;
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CustomerService customerService;
    SimpleClientHttpRequestFactory requestFactory;
    RestTemplate restTemplate;

    public MyWebSocket() {
        requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);
        restTemplate = new RestTemplate(requestFactory);
    }

    /**
     * 连接创建成功
     *
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam(value = "id") String id, Session session) {
        if (StringUtils.isEmpty(id)) {
            try {
                sendMessage("jiaju_id is must be bull or empty!");
                session.close();
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        } else {
            this.session = session;
            WEB_SOCKET_SET.put(id, this);
            addOnlineCount();
            LOG.info("有新的连接加入！当前连接数为：" + getOnlineCount());
        }
    }

    @OnClose
    public void onClose() {
        WEB_SOCKET_SET.values().remove(this);   //从set中删除
        subOnlineCount();           //在线数减1
        LOG.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    @OnError
    public void onERROR(Session session, Throwable error) {
        LOG.error(error.getMessage());
        try {
            session.close();
            onClose();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        LOG.debug("来自客户端的消息:" + message);
        if ("ping".equals(message.toLowerCase())){
            try {
                session.getBasicRemote().sendText("pong");
            } catch (IOException e) {
                LOG.error("向客户端发送ping消息回复失败，内容为pong！ Exception -> :{}", e.getMessage());
            }
        }
        //群发消息
        /*
        for (MyWebSocket item : WEB_SOCKET_SET.values()) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
        */
    }

    public void sendMessage(final String message) throws IOException {
        synchronized (session) {
            session.getBasicRemote().sendText(message);
        }


    }

    public void notifyClient(String id) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "Offline");

        if (!WEB_SOCKET_SET.isEmpty()) {
            //如果ID是客服，就给他的游客发送离线状态消息
            Object juid = redisTemplate.boundHashOps("account_linking").get(id);
            String juidStr = (juid == null ? id : juid.toString());
            List<String> guests = customerService.getOnlineGuestIDsByCustomerID(juidStr);
            if (!guests.isEmpty()) {
                LOG.info("客服->{} 离线发送离线通知给游客 --> {}", juidStr, guests);
                for (String gt : guests) {
                    try {
                        jsonObject.put("username", id);
                        jsonObject.put("jiaju_id", id);
                        MyWebSocket socket = WEB_SOCKET_SET.get(gt);
                        if (socket != null) {
                            socket.sendMessage(jsonObject.toString());
                            LOG.info("发送离线通知给游客 --> {} {}", gt, jsonObject.toString());
                        } else {
                            LOG.warn("由于游客{}未连接websockte，故通知消息未发送", gt);
                        }
                    } catch (IOException e) {
                        LOG.error(e.getMessage());
                    }

                }
                //数据库删除客服与游客关联记录
                customerService.deleteCustomerGuestByCustomerID(juidStr);
            }
            //如果ID是游客，就给他的客服发送离线状态消息
            List<String> customers = customerService.getOnlineCustomerIDsByGuestID(juidStr);
            if (!customers.isEmpty()) {
                LOG.info("游客->{} 离线发送离线通知给客服 --> {}", juidStr, customers);
                for (String gt : customers) {
                    try {
                        jsonObject.put("username", id);
                        jsonObject.put("jiaju_id", juidStr);
                        MyWebSocket socket = WEB_SOCKET_SET.get(gt);
                        if (socket != null) {
                            socket.sendMessage(jsonObject.toString());
                            LOG.info("发送离线通知给客服 --> {} {}", gt, jsonObject.toString());
                        } else {
                            LOG.warn("由于客服{}未连接websockte，故通知消息未发送", gt);
                        }
                    } catch (IOException e) {
                        LOG.error(e.getMessage());
                    }
                }
            }
        }
    }

    public void notifyClient() throws UnsupportedEncodingException {
        if (WEB_SOCKET_SET.size() > 0) {
            HttpHeaders headers = new HttpHeaders();
            List<MediaType> accepts = new ArrayList<>();
            accepts.add(MediaType.APPLICATION_JSON_UTF8);
            headers.setAccept(accepts);
            headers.set("Authorization", secret);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(headers), String.class);
            if (response.getStatusCodeValue() == 200) {
                //由于RestTemplate内部使用的是ISO-8859-1编码，所以会让中文乱码，在这里要对返回内容从新编码
                onMessage(new String(response.getBody().getBytes("ISO-8859-1"), "UTF-8"), null);
            } else {
                LOG.error("get error by http interface -> " + url);
            }
        } else {
            LOG.debug("No one Online");
        }
    }

    public static int getOnlineCount() {
        return ONLINE_COUNT;
    }

    public static synchronized void addOnlineCount() {
        MyWebSocket.ONLINE_COUNT++;
    }

    public static synchronized void subOnlineCount() {
        if (ONLINE_COUNT == 0) {

        } else {
            MyWebSocket.ONLINE_COUNT--;
        }

    }
}
