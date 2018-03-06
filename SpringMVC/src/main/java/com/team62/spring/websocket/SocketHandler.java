package com.team62.spring.websocket;


import java.io.IOException;
import java.util.ArrayList;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.team62.spring.model.Player;
//import com.team62.spring.model.AI.AI;
import com.team62.spring.model.adventureCards.AdventureCard;
import com.team62.spring.model.storyCards.StoryCard;
import com.team62.spring.model.Game;

@Component
public class SocketHandler extends TextWebSocketHandler {
	private static final Logger logger = LogManager.getLogger(SocketHandler.class);
	public Game gameEngine = new Game();
	int sessionTracker = 0;

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		if (session.isOpen()) {
			String clientMessage = message.getPayload();

			// first time connection

			if (clientMessage.equals("Player attempting to connect")) {
				logger.info("Player attempting to connect...");
				
                if(gameEngine.players.size() == 4) {
                	session.sendMessage(new TextMessage("Too many players, sorry. Being disconnected."));
                	logger.info("A player got denied entry to game, already full");
                	session.close();
                	return;
                } else {
                	logger.info("Player #{} connected", session.getId());
                	session.sendMessage(new TextMessage("Welcome, enter your name"));
                }

			}
			
			//flip story deck
			if(clientMessage.equals("flipStoryDeck")) {
				gameEngine.storyDeck.flipCard();
				session.sendMessage(new TextMessage("flipStoryDeck" + gameEngine.storyDeck.faceUp.StringFile));
				logger.info("Flipping card from story deck");
			}
			
			// initialize client
			if (clientMessage.startsWith("Name")) {
				String Name = clientMessage.substring(clientMessage.indexOf(':')+1);
				Player clientObject = new Player(session.getId(), Name, session, sessionTracker);
				logger.info("Player {} is enrolled in the quest", Name);
				gameEngine.players.add(clientObject);
				sessionTracker++;
				session.sendMessage(new TextMessage("You are all set up, waiting for other players to connect.."));
				//all clients have joined
				if(gameEngine.players.size()==4) {
					logger.info("All 4 players connected, starting game -> flipping first story card");
					sendToAllSessions(gameEngine.players,"All players have joined, starting game...");
					gameEngine.storyDeck.flipCard();
					sendToAllSessions(gameEngine.players,"flipStoryDeck" + gameEngine.storyDeck.faceUp.StringFile);
				}
			}

			//print all gameEngine.players for requested client
			if(clientMessage.equals("Print")) {
				session.sendMessage(new TextMessage("All players:\n"));
				String clientsString = "";
				for(Player p:gameEngine.players) {
					clientsString += "ID: " + p.id + " Name: " + p.name + "\n";
				}
				session.sendMessage(new TextMessage(clientsString));
			}
			
			//validation of connection
			if(clientMessage.startsWith("Proof")) {
				for(int i=0; i<gameEngine.players.size(); i++) {
					if(session.getId().equals(gameEngine.players.get(i).session.getId())) {
						gameEngine.players.get(i).session.sendMessage(new TextMessage("You are " + gameEngine.players.get(i).name));
					}
				}
				
				logger.info("ADVENTURE DECK PROOF:"); int i=1;
				for(AdventureCard a: gameEngine.adventureDeck.cards) {
					logger.info("{}. {}",i,a.name);
					i++;
				}
				logger.info("STORY DECK PROOF:"); int j=1;
				for(StoryCard s: gameEngine.storyDeck.cards) {
					logger.info("{}. {}",j,s.name);
					j++;				
				}
			}
			
			
		} // if session open
	} // handler end
	
	public void sendToAllSessions(ArrayList<Player> players, String message) throws IOException {

		for(Player p:gameEngine.players) {
			p.session.sendMessage(new TextMessage(message));
		}

	}
} // class end



