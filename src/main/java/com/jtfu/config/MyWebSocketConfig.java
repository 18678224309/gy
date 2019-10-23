package com.jtfu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@EnableWebSocket
public class MyWebSocketConfig implements WebSocketConfigurer {

    @Autowired
    MyWebSocketHander myWebSocketHander;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWebSocketHander,"/webSocket").addInterceptors(new MyHandShakeInterceptor()).setAllowedOrigins("*");
    }
}
