package com.jtfu.config;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jtfu.entity.User;
import com.jtfu.mapper.MsgboxMapper;
import com.jtfu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Component
public class MyWebSocketHander implements WebSocketHandler {
    /*private final static List<WebSocketSession> USERS=new ArrayList<>();*/
    public final static Map<String,WebSocketSession> USER_ONLINE=new HashMap<>();

    @Autowired
    IUserService userService;

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        //添加sockt用户，并且更新用户在线状态；
        String userId=webSocketSession.getAttributes().get("userId").toString();
        USER_ONLINE.put(userId,webSocketSession);
        System.err.println(USER_ONLINE.size());
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        //接收前端webSocket发送的数据，进行解析，并发送给指定用户;
        String msgContent = webSocketMessage.getPayload().toString();
        sendMsgToUser(msgContent);
    }




    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        //发生错误时删除用户信息
        removeUser(webSocketSession);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        //关闭时删除用户信息
        removeUser(webSocketSession);
    }

    public void removeUser(WebSocketSession webSocketSession){
        //USERS.remove(webSocketSession);
        //删除socket用户，并且将用户状态置为离线；
        System.err.println("关闭socket-------清理用户!");
        String userId=webSocketSession.getAttributes().get("userId").toString();
        USER_ONLINE.remove(userId);
        System.out.println(USER_ONLINE.size());
        webSocketSession.getAttributes().remove("userId");
    }

    public void  sendMsgToUser(String msgContent) throws IOException {
        JSONObject chat = JSON.parseObject(msgContent);
        JSONObject data=chat.getJSONObject("data");
        JSONObject mine=data.getJSONObject("mine");
        JSONObject to=data.getJSONObject("to");
        String mineId=mine.getString("id");
        String toId=to.getString("id");
        if(USER_ONLINE.containsKey(toId)){
            Map msgMap=new HashMap();
            msgMap.put("username",mine.get("username"));
            msgMap.put("avatar",mine.get("avatar"));
            msgMap.put("id",mineId);
            msgMap.put("type",to.get("type"));
            msgMap.put("content",mine.get("content"));
            msgMap.put("timestamp",new Date());
            TextMessage testMsg = new TextMessage(JSON.toJSONString(msgMap));
            WebSocketSession session=USER_ONLINE.get(toId);
            session.sendMessage(testMsg);
        }
    }

    public void  sendMsgToAll(){

    }


    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
