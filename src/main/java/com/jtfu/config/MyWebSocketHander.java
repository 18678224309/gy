package com.jtfu.config;


import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyWebSocketHander implements WebSocketHandler {
    private final static List<WebSocketSession> USERS=new ArrayList<>();
   // private final static List<User> USER_ONLINE=new ArrayList<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
       // System.out.println("============已经连接");
        USERS.add(webSocketSession);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
       // System.out.println("1");
        String msgContent = webSocketMessage.getPayload() + "";
        //System.out.println("前端传到后台的数据 " + msgContent);
        //JSONObject chat = JSON.parseObject(msgContent);
        //System.out.println(chat);
        TextMessage testMsg = new TextMessage(  msgContent);
        for (WebSocketSession wss: USERS) {
            wss.sendMessage(testMsg);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        USERS.remove(webSocketSession);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        USERS.remove(webSocketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
