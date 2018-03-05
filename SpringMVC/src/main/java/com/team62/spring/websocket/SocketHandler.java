package com.team62.spring.websocket;


import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.team62.spring.model.Player;
import com.team62.spring.model.adventureCards.AdventureCard;
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
			if (clientMessage.equals("Player attempting to connect")) {
				System.out.println(clientMessage);
				System.out.println("Player #" + session.getId() + " connected");
                if(gameEngine.players.size() == 4) {
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
				Player clientObject = new Player(session.getId(), Name, session, sessionTracker);
				gameEngine.players.add(clientObject);
				sessionTracker++;
				session.sendMessage(new TextMessage("You are all set up, waiting for other players to connect.."));
				if(gameEngine.players.size()==4) {
					for(Player p:gameEngine.players) {
						p.session.sendMessage(new TextMessage("All players have joined, starting game..."));
					}
				}
			}

			//print all gameEngine.players
			if(clientMessage.equals("Print")) {
				session.sendMessage(new TextMessage("All players:\n"));
				String clientsString = "";
				for(Player p:gameEngine.players) {
					clientsString += "ID: " + p.id + " Name: " + p.name + "\n";
				}
				session.sendMessage(new TextMessage(clientsString));
			}
			
			//validation 
			if(clientMessage.startsWith("Proof")) {
				for(int i=0; i<gameEngine.players.size(); i++) {
					if(session.getId().equals(gameEngine.players.get(i).session.getId())) {
						gameEngine.players.get(i).session.sendMessage(new TextMessage("You are " + gameEngine.players.get(i).name));
					}
				}
				for(AdventureCard a: gameEngine.adventureDeck.cards) {
					System.out.println(a.name);					
				}
			}
			
			
		} // if session open
	} // handler end
} // class end

