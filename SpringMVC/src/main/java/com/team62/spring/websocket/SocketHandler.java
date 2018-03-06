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
					logger.info("All 4 players connected, starting game -> setting player hands + flipping first story card");
					sendToAllSessions(gameEngine.players,"All players have joined, starting game...");
					gameEngine.storyDeck.flipCard();
					sendToAllSessions(gameEngine.players,"flipStoryDeck" + gameEngine.storyDeck.faceUp.StringFile);
					gameEngine.players.get(0).setHand(gameEngine.mockHand1); //pickUpNewHand()
					gameEngine.players.get(1).setHand(gameEngine.mockHand2);
					gameEngine.players.get(2).setHand(gameEngine.mockHand3);
					gameEngine.players.get(3).setHand(gameEngine.mockHand4);
					gameEngine.players.get(0).session.sendMessage(new TextMessage("setHand"+gameEngine.players.get(0).getHandString()));
					gameEngine.players.get(1).session.sendMessage(new TextMessage("setHand"+gameEngine.players.get(1).getHandString()));
					gameEngine.players.get(2).session.sendMessage(new TextMessage("setHand"+gameEngine.players.get(2).getHandString()));
					gameEngine.players.get(3).session.sendMessage(new TextMessage("setHand"+gameEngine.players.get(3).getHandString()));
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
			
			//validation of connection and decks - will segfault if players list not init
			if(clientMessage.startsWith("Proof")) {
				//
				logger.info("ADVENTURE DECK PROOF:"); int i=1;
				for(AdventureCard a: gameEngine.adventureDeck.cards) {
					logger.info("{}. {}",i,a.name);
					i++;
				}
				//
				logger.info("STORY DECK PROOF:"); int j=1;
				for(StoryCard s: gameEngine.storyDeck.cards) {
					logger.info("{}. {}",j,s.name);
					j++;				
				}
				//output player name in messageconsole
				if(getCurrentPlayer(session) ==null) {} else {
					getCurrentPlayer(session).session.sendMessage(new TextMessage("You are " + getCurrentPlayer(session).name));
				}
				
			}
			
			
		} // if session open
	} // handler end
	
	//send message to all players
	public void sendToAllSessions(ArrayList<Player> players, String message) throws IOException {

		for(Player p:gameEngine.players) {
			p.session.sendMessage(new TextMessage(message));
		}

	}
	
	//get current player 
	public Player getCurrentPlayer(WebSocketSession s) {
		for(int i=0; i<gameEngine.players.size(); i++) {
			if(s.getId().equals(gameEngine.players.get(i).session.getId())) {
				return gameEngine.players.get(i);
			}
		}
		return null;
	}
	

	
	
	
} // class end



