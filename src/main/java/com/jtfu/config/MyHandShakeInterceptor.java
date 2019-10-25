package com.jtfu.config;


import com.jtfu.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class MyHandShakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        System.out.println("-------------------beforeHandshake");
        if(serverHttpRequest instanceof ServletServerHttpRequest){
            ServletServerHttpRequest request = (ServletServerHttpRequest)serverHttpRequest;
            //获取参数
            String userId= request.getServletRequest().getParameter("userId");
            /*attributes.put(MyMessageHandler.USER_KEY, userId);*/
            /*map.put("session",)*/
            map.put("userId",userId);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}
