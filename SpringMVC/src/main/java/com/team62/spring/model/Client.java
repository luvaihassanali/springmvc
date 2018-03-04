package com.team62.spring.model;

import org.springframework.web.socket.WebSocketSession;

public class Client {
	public String id;
	public String name;
	public WebSocketSession session;
	public int sessionTracker;

	public Client(String id, String name, WebSocketSession session, int sessionTracker) {
		this.id = id;
		this.name = name;
		this.session = session;
		this.sessionTracker = sessionTracker;
	}
}