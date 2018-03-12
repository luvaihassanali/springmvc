package com.luvai.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.luvai.model.Card;
import com.luvai.model.CardList;
import com.luvai.model.Game;
import com.luvai.model.Player;
//import com.luvai.model.AI.AI;
import com.luvai.model.AdventureCards.AdventureCard;
import com.luvai.model.StoryCards.EventCard;
import com.luvai.model.StoryCards.QuestCard;
import com.luvai.model.StoryCards.StoryCard;
import com.luvai.model.StoryCards.TournamentCard;

@Component
public class SocketHandler extends TextWebSocketHandler {
	private static final Logger logger = LogManager.getLogger(SocketHandler.class);
	public Game gameEngine = new Game();
	public static int numTurns = 0;
	public static int turnTracker = 0;
	public QuestController newQuest;
	public WebSocketSession sponsorSession;
	public String BattleInformation = "";
	public int foeTracker = 1;
	public int weaponTracker = 0;
	public String currentFoe = "";
	public static boolean rankSet = true;

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		if (session.isOpen()) {

			String clientMessage = message.getPayload();

			// get participants battle equipment
			if (clientMessage.startsWith("equipmentID")) {
				if (rankSet)
					BattleInformation += "player_rank" + getPlayerFromSession(session).getRank().getStringFile() + ";";
				rankSet = false;
				Card card = getCardFromLink(clientMessage);
				logger.info("{} chose {} for stage {}", getPlayerFromSession(session).getName(), card.getName(),
						weaponTracker - 1);
				BattleInformation += clientMessage + ";";
			}
			// get quest setup foes

			if (clientMessage.startsWith("quest_foeID")) {
				currentFoe = getCardFromLink(clientMessage).name;
				logger.info("{} chose {} for stage {}", gameEngine.getActivePlayer().getName(),
						getCardFromLink(clientMessage).getName(), foeTracker);
				BattleInformation += foeTracker + clientMessage + ";";
				foeTracker++;
				weaponTracker++;

			}
			// get quest setup weapons
			if (clientMessage.startsWith("quest_weaponID")) {
				BattleInformation += weaponTracker + clientMessage + ";";
				logger.info("{} equipped {} for {} on stage {}", gameEngine.getActivePlayer().getName(),
						getCardFromLink(clientMessage).name, currentFoe, foeTracker - 1);

			}
			// show battle
			if (clientMessage.equals("Show battle")) {
				// System.out.println(BattleInformation);
				if (rankSet)
					BattleInformation += "player_rank" + getPlayerFromSession(session).getRank().getStringFile() + ";";
				logger.info("{} is going into stage {} battle for quest {}", gameEngine.getActivePlayer().getName(),
						weaponTracker - 1, gameEngine.storyDeck.faceUp.getName());
				sendToAllSessions(gameEngine.players, "Battle info" + BattleInformation);
				// BattleInformation = "";

			}
			// accept to participate quest
			if (clientMessage.equals("Accept participation")) {
				logger.info("{} chose to participate in quest: {}, sponsored by: {}",
						getPlayerFromSession(session).getName(), gameEngine.storyDeck.faceUp.getName(),
						getPlayerFromSession(sponsorSession).getName());
				session.sendMessage(new TextMessage("Choose equipment"));
				gameEngine.participants.add(getPlayerFromSession(session));
				newQuest.sendToAllSessionsExceptCurrent(gameEngine, session,
						"Player: " + getPlayerFromSession(session).getName() + " is going through quest");
			}

			// deny to participate quest
			if (clientMessage.equals("Deny participation")) {
				logger.info("{} chose to decline to participate in quest: {}, sponsored by: {}",
						getPlayerFromSession(session).getName(), gameEngine.storyDeck.faceUp.getName(),
						getPlayerFromSession(sponsorSession).getName());
				turnTracker++;
				if (turnTracker == 3) {
					turnTracker = 0;
					sponsorSession.sendMessage(new TextMessage("No participants"));
					logger.info("No one chose to participate in current quest: {}, sponsored by: {}",
							gameEngine.storyDeck.faceUp.getName(), getPlayerFromSession(sponsorSession).getName());
					sendTurnNotification(gameEngine.getNextPlayer().session);
				}
				// System.out.println(turnTracker);
				// System.out.println(gameEngine.getNextPlayer().getName());

			}

			// invitation to participate quest
			if (clientMessage.equals(("Ask to participate"))) {
				newQuest.sendToNextPlayer(gameEngine, "Ask to participate");
				logger.info("{} has completed setting up quest: {}", gameEngine.getActivePlayer().getName(),
						gameEngine.storyDeck.faceUp.getName());
			}
			// accept sponsor quest
			if (clientMessage.equals("acceptSponsorQuest")) {
				newQuest = new QuestController();
				newQuest.setupQuest(gameEngine, session);
				sponsorSession = session;
			}
			// first time connection
			if (clientMessage.equals("Player attempting to connect")) {
				logger.info("Player attempting to connect...");

				if (gameEngine.players.size() == 4) {
					session.sendMessage(new TextMessage("Too many players, sorry. Being disconnected."));
					logger.info("A player got denied entry to game, already full");
					session.close();
					return;
				} else {
					logger.info("Player from session#{} connected", session.getId());
					String BattleInfo = "Battle info1quest_foeIDhttp://localhost:8080/resources/images/F%20Boar.jpg;1quest_weaponIDhttp://localhost:8080/resources/images/W%20Sword.jpg;1quest_weaponIDhttp://localhost:8080/resources/images/W%20Dagger.jpg;2quest_foeIDhttp://localhost:8080/resources/images/F%20Black%20Knight.jpg;2quest_weaponIDhttp://localhost:8080/resources/images/W%20Sword.jpg;2quest_weaponIDhttp://localhost:8080/resources/images/W%20Horse.jpg;player_rank/resources/images/R Squire.jpg;equipmentIDhttp://localhost:8080/resources/images/A%20Sir%20Tristan.jpg;";
					session.sendMessage(new TextMessage(BattleInfo));
					// session.sendMessage(new TextMessage("Welcome, enter your name"));
				}

			}

			// flip story deck
			if (clientMessage.equals("flipStoryDeck")) {
				gameEngine.storyDeck.flipCard();
				logger.info("Flipped card from Story Deck: {}", gameEngine.storyDeck.faceUp.getName());
				sendToAllSessions(gameEngine.players, "flipStoryDeck" + gameEngine.storyDeck.faceUp.getStringFile());
			}

			// increase turn
			if (clientMessage.equals("incTurn")) {
				numTurns++;
				logger.info("It is {}'s turn", gameEngine.getActivePlayer().getName());
				sendToAllSessions(gameEngine.players, "turnTracker" + numTurns);
			}

			// ask next player to sponsor if current player says no
			if (clientMessage.equals("askNextPlayerToSponsor")) {
				logger.info("{} denied to sponsor quest", gameEngine.getPrevPlayer().getName());
				gameEngine.getActivePlayer().session.sendMessage(new TextMessage("sponsorQuest"));
			}
			if (clientMessage.equals("askNextPlayerToSponsorFinishLog")) {
				logger.info("Last player in queue denied to sponsor the quest, next round");
				gameEngine.storyDeck.flipCard();
				sendToAllSessions(gameEngine.players, "flipStoryDeck" + gameEngine.storyDeck.faceUp.getStringFile());
			}
			// initialize client
			if (clientMessage.startsWith("Name")) {
				String Name = clientMessage.substring(clientMessage.indexOf(':') + 1);
				Player clientObject = new Player(session.getId(), Name, session);
				logger.info("Player {} is enrolled in the quest", Name);
				gameEngine.players.add(clientObject);
				session.sendMessage(new TextMessage("You are all set up, waiting for other players to connect.."));
				// all clients have joined
				if (gameEngine.players.size() == 4) {

					gameEngine.storyDeck.faceUp = CardList.Quest6;// gameEngine.storyDeck.flipCard();
					logger.info("Flipping first card from story deck: {}", gameEngine.storyDeck.faceUp.getName());
					sendToAllSessions(gameEngine.players,
							"flipStoryDeck" + gameEngine.storyDeck.faceUp.getStringFile());
					gameEngine.players.get(0).setHand(gameEngine.mockHand1); // pickUpNewHand()
					gameEngine.players.get(1).setHand(gameEngine.mockHand2);
					gameEngine.players.get(2).setHand(gameEngine.mockHand3);
					gameEngine.players.get(3).setHand(gameEngine.mockHand4);
					gameEngine.players.get(0).session
							.sendMessage(new TextMessage("setHand" + gameEngine.players.get(0).getHandString()));
					gameEngine.players.get(1).session
							.sendMessage(new TextMessage("setHand" + gameEngine.players.get(1).getHandString()));
					gameEngine.players.get(2).session
							.sendMessage(new TextMessage("setHand" + gameEngine.players.get(2).getHandString()));
					gameEngine.players.get(3).session
							.sendMessage(new TextMessage("setHand" + gameEngine.players.get(3).getHandString()));
					logger.info("All 4 players connected, starting game....");
					sendToAllSessions(gameEngine.players, "All players have joined, starting game...");
					if (gameEngine.storyDeck.faceUp instanceof QuestCard) {
						gameEngine.players.get(0).session.sendMessage(new TextMessage("sponsorQuest"));
					}
					if (gameEngine.storyDeck.faceUp instanceof TournamentCard) {
					}
					if (gameEngine.storyDeck.faceUp instanceof EventCard) {
					}
				}
			}

			// print all gameEngine players for requested client
			if (clientMessage.equals("Print")) {
				session.sendMessage(new TextMessage("All players:\n"));
				String clientsString = "clientsString";
				for (Player p : gameEngine.players) {
					clientsString += "ID: " + p.id + " Name: " + p.getName() + "\n";
				}
				session.sendMessage(new TextMessage(clientsString));
			}

			// validation of connection and decks
			if (clientMessage.startsWith("Proof")) {
				//
				logger.info("ADVENTURE DECK PROOF:");
				int i = 1;
				for (AdventureCard a : gameEngine.adventureDeck.cards) {
					logger.info("{}. {}", i, a.getName());
					i++;
				}
				//
				logger.info("STORY DECK PROOF:");
				int j = 1;
				for (StoryCard s : gameEngine.storyDeck.cards) {
					logger.info("{}. {}", j, s.getName());
					j++;
				}

				// output player name in console
				if (getPlayerFromSession(session) == null) {
				} else {
					getPlayerFromSession(session).session
							.sendMessage(new TextMessage("You are " + getPlayerFromSession(session).getName()));
				}
			} // validation of decks
		} // if session open
	} // handler end

	public String calculateBattlePoints(String BattleInformation) {
		String points = "";
		return points;
	}

	public void sendTurnNotification(WebSocketSession session) throws IOException {
		// send card info
		session.sendMessage(new TextMessage("It is your turn"));
	}

	// send message to all players
	public void sendToAllSessions(ArrayList<Player> players, String message) {

		for (Player p : gameEngine.players) {
			try {
				p.session.sendMessage(new TextMessage(message));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// get current player
	public Player getPlayerFromSession(WebSocketSession s) {
		for (int i = 0; i < gameEngine.players.size(); i++) {
			if (s.getId().equals(gameEngine.players.get(i).session.getId())) {
				return gameEngine.players.get(i);
			}
		}
		return null;
	}

	public Card getCardFromLink(String ImageLink) {

		CardList cardList = new CardList();
		String cleanLink = "";
		if (ImageLink.contains("quest_foe")) {
			cleanLink = ImageLink.replace("quest_foeIDhttp://localhost:8080", "");
		}
		if (ImageLink.contains("quest_weapon")) {
			cleanLink = ImageLink.replace("quest_weaponIDhttp://localhost:8080", "");
		}
		if (ImageLink.contains("equipmentID")) {
			cleanLink = ImageLink.replace("equipmentIDhttp://localhost:8080", "");
		}

		cleanLink = cleanLink.replaceAll("%20", " ");
		for (int i = 0; i < cardList.allTypes.size(); i++) {
			if (cardList.allTypes.get(i).StringFile.equals(cleanLink)) {
				return cardList.allTypes.get(i);
			}
		}
		return null;
	}

} // class end
