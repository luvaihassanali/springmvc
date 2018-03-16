package com.luvai.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.luvai.controller.SocketHandler;

@Configuration
@EnableWebSocket
@ComponentScan("com.luvai.controller")
public class SocketConfig implements WebSocketConfigurer {

	@Autowired
	private SocketHandler GameSocketHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(GameSocketHandler, "/socketHandler");
	}

}
