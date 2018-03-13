package com.luvai.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.luvai.model.CardList;
import com.luvai.model.Game;
import com.luvai.model.Player;
import com.luvai.model.StoryCards.EventCard;
import com.luvai.model.StoryCards.QuestCard;
import com.luvai.model.StoryCards.TournamentCard;

@Component
public class SocketHandler extends TextWebSocketHandler {

	List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
	private static final Logger logger = LogManager.getLogger(SocketHandler.class);
	static Game gameEngine = new Game();

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {

		JsonObject jsonObject = (new JsonParser()).parse(message.getPayload()).getAsJsonObject();

		@SuppressWarnings("unchecked")
		Map<String, String> clientMessage = new Gson().fromJson(message.getPayload(), Map.class);

		// json for quest setup info from sponsor
		if (jsonObject.has("foes")) {

			String jsonOutput = "currentQuestInfo" + jsonObject.toString();
			System.out.println(jsonOutput);
			sendToAllSessions(gameEngine.players, jsonOutput);
			sendToNextPlayer(gameEngine, "AskToParticipate");
			gameEngine.incTurn();
		}
		// check json for sponsor quest field
		if (jsonObject.has("sponsor_quest")) {

			JsonElement sponsor_quest_answer = jsonObject.get("sponsor_quest");
			JsonElement name = jsonObject.get("name");
			if (sponsor_quest_answer.getAsBoolean()) {
				logger.info("{} accepted to sponsor quest {}", name.getAsString(),
						gameEngine.storyDeck.faceUp.getName());
				gameEngine.newQuest(gameEngine, getPlayerFromSession(session), (QuestCard) gameEngine.storyDeck.faceUp);
				return;

			} else {
				logger.info("{} declined to sponsor quest {}", name.getAsString(),
						gameEngine.storyDeck.faceUp.getName());
				gameEngine.incTurn();
				if (gameEngine.getActivePlayer().equals(gameEngine.temp_roundInitiater)) {
					logger.info("No players accepted to sponsor quest {}", gameEngine.storyDeck.faceUp.getName());
					flipStoryCard();
					return;
				}
				gameEngine.getActivePlayer().session.sendMessage(new TextMessage("sponsorQuest"));
				return;
			}

		}
		// get client name
		if (clientMessage.get("name") != null) {
			Player clientObject = new Player(clientMessage.get("name"), session);
			logger.info("Player {} is enrolled in the quest", clientObject.getName());
			gameEngine.players.add(clientObject);
			session.sendMessage(new TextMessage("You are all set up, waiting for other players to connect.."));

			// all clients have joined
			if (gameEngine.players.size() == 4) {
				gameEngine.storyDeck.faceUp = CardList.Quest6;// gameEngine.storyDeck.flipCard();
				sendToAllSessions(gameEngine.players, "flipStoryDeck" + gameEngine.storyDeck.faceUp.toString());
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
				logger.info("Flipping first card from story deck: {}", gameEngine.storyDeck.faceUp.getName());
				sendToAllSessions(gameEngine.players, "All players have joined, starting game...");
				if (gameEngine.storyDeck.faceUp instanceof QuestCard) {
					gameEngine.temp_roundInitiater = gameEngine.players.get(0);
					gameEngine.players.get(0).session.sendMessage(new TextMessage("sponsorQuest"));
				}
				if (gameEngine.storyDeck.faceUp instanceof TournamentCard) {
				}
				if (gameEngine.storyDeck.faceUp instanceof EventCard) {
				}
			}
		}
	}

	// flip story deck
	public void flipStoryCard() throws IOException {
		gameEngine.storyDeck.flipCard();
		logger.info("Flipping new card from story deck: {}", gameEngine.storyDeck.faceUp.getName());
		sendToAllSessions(gameEngine.players, ("flipStoryDeck" + gameEngine.storyDeck.faceUp.toString()));
		if (gameEngine.storyDeck.faceUp instanceof QuestCard) {
			gameEngine.temp_roundInitiater = gameEngine.getActivePlayer();
			gameEngine.getActivePlayer().session.sendMessage(new TextMessage("sponsorQuest"));
		}
		if (gameEngine.storyDeck.faceUp instanceof TournamentCard) {
		}
		if (gameEngine.storyDeck.faceUp instanceof EventCard) {
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		gameEngine.storyDeck.faceUp = CardList.Quest6;
		Player clientObject = new Player("Bob", session);
		gameEngine.players.add(clientObject);
		gameEngine.players.get(0).setHand(gameEngine.mockHand1);
		session.sendMessage(new TextMessage("flipStoryDeck" + gameEngine.storyDeck.faceUp.toString()));
		gameEngine.players.get(0).session
				.sendMessage(new TextMessage("setHand" + gameEngine.players.get(0).getHandString()));
		String x = "currentQuestInfo{" + "\"foes\"" + ":[" + "\"Boar\"" + "," + "\"Black Knight\"" + "],"
				+ "\"weapons\"" + ":[[" + "\"Dagger\"" + "],[" + "\"Sword\"" + "," + "\"Horse\"" + "]]}";
		System.out.println(x);
		session.sendMessage(new TextMessage(x));
		session.sendMessage(new TextMessage("AskToParticipate"));
		/*
		logger.info("New player attempting to connect...");
		if (gameEngine.players.size() == 4) {
			session.sendMessage(new TextMessage("Too many players, sorry. Being disconnected."));
			logger.info("New player got denied entry to game, already full");
			session.close();
			return;
		} else {
			logger.info("Player from session#{} connected", session.getId());
			session.sendMessage(new TextMessage("Welcome, enter your nickname - then press send."));
			sessions.add(session);
		
		} */
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

	// send message to next player
	public void sendToNextPlayer(Game gameEngine, String message) throws IOException {
		Player player = gameEngine.getNextPlayer();
		player.session.sendMessage(new TextMessage(message));
	}

	// send to all players except that in current turn
	public void sendToAllSessionsExceptCurrent(Game gameEngine, WebSocketSession session, String message)
			throws IOException {
		ArrayList<Player> tempList = new ArrayList<Player>();
		for (int i = 0; i < gameEngine.players.size(); i++) {
			if (gameEngine.players.get(i).id == session.getId()) {

			} else {
				tempList.add(gameEngine.players.get(i));
			}
		}
		for (Player p : tempList) {
			p.session.sendMessage(new TextMessage(message));
		}
	}
}// end of class