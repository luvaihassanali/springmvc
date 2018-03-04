package com.team62.spring.websocket;


import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.team62.spring.model.Client;
import com.team62.spring.model.Game;

@Component
public class SocketHandler extends TextWebSocketHandler {

	Game gameEngine = new Game();
	int sessionTracker = 0;

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		if (session.isOpen()) {
			String clientMessage = message.getPayload();

			// first time connection
			if (clientMessage.equals("Client attempting to connect")) {
				System.out.println(clientMessage);
				System.out.println("Client #" + session.getId() + " connected");
                if(gameEngine.clients.size() == 4) {
                	session.sendMessage(new TextMessage("Too many players, sorry. Being disconnected."));
                	session.close();
                	return;
                } else {
                	session.sendMessage(new TextMessage("Welcome, enter your name"));
                }

			}
			
			// initialize client
			if (clientMessage.startsWith("Name")) {
				String Name = clientMessage.substring(clientMessage.indexOf(':')+1);
				Client clientObject = new Client(session.getId(), Name, session, sessionTracker);
				gameEngine.clients.add(clientObject);
				sessionTracker++;
				session.sendMessage(new TextMessage("You are all set up"));

			}

			//print all gameEngine.clients
			if(clientMessage.equals("Print")) {
				session.sendMessage(new TextMessage("All players:\n"));
				String clientsString = "";
				for(Client c:gameEngine.clients) {
					clientsString += "ID: " + c.id + " Name: " + c.name + "\n";
				}
				session.sendMessage(new TextMessage(clientsString));
			}
			
			//validation 
			if(clientMessage.startsWith("Proof")) {
				for(int i=0; i<gameEngine.clients.size(); i++) {
					if(session.getId().equals(gameEngine.clients.get(i).session.getId())) {
						gameEngine.clients.get(i).session.sendMessage(new TextMessage("You are " + gameEngine.clients.get(i).name));
					}
				}
			}
			
			
		} // if session open
	} // handler end
} // class end

