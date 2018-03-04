package com.team62.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.team62.spring.websocket.SocketHandler;

@Configuration
@EnableWebSocket
@ComponentScan("com.team62.spring.websocket")
public class SocketConfig implements WebSocketConfigurer {

   @Autowired
   private SocketHandler myWebSocketHandler;

   @Override
   public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
      registry.addHandler(myWebSocketHandler, "/socketHandler");
   }

}
