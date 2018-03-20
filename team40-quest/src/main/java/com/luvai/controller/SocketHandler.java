package com.luvai.controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.luvai.model.CardList;
import com.luvai.model.Game;
import com.luvai.model.Player;
import com.luvai.model.AdventureCards.AdventureCard;
import com.luvai.model.StoryCards.EventCard;
import com.luvai.model.StoryCards.QuestCard;
import com.luvai.model.StoryCards.TournamentCard;

@Component
public class SocketHandler extends TextWebSocketHandler {

	List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
	private static final Logger logger = LogManager.getLogger(SocketHandler.class);
	static Game gameEngine = new Game();
	public static boolean rankSet = true;
	public String BattleInformation = "";

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {

		JsonObject jsonObject = (new JsonParser()).parse(message.getPayload()).getAsJsonObject();
		// get player name and set up game players
		if (jsonObject.has("newName")) {
			JsonElement playerName = jsonObject.get("newName");
			Player newPlayer = new Player(playerName.getAsString(), session);
			gameEngine.players.add(newPlayer);
			logger.info("Player {} is enrolled in the game", playerName.getAsString());
			if (gameEngine.players.size() == 4) {
				sendToAllSessions(gameEngine, "GameReadyToStart");
				logger.info("All players have joined, starting game...");
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
				flipStoryCard();
			}
		}
		// sponsoring quuest
		if (jsonObject.has("sponsor_quest")) {
			JsonElement sponsor_quest_answer = jsonObject.get("sponsor_quest");
			JsonElement name = jsonObject.get("name");
			if (sponsor_quest_answer.getAsBoolean()) {
				logger.info("{} accepted to sponsor quest {}", name.getAsString(),
						gameEngine.storyDeck.faceUp.getName());
				gameEngine.newQuest(gameEngine, gameEngine.getActivePlayer(), (QuestCard) gameEngine.storyDeck.faceUp);

			} else {
				logger.info("{} declined to sponsor quest {}", name.getAsString(),
						gameEngine.storyDeck.faceUp.getName());
				gameEngine.incTurn();
				if (gameEngine.getActivePlayer().equals(gameEngine.roundInitiater)) {
					sendToAllSessions(gameEngine, "NoSponsors");
					logger.info("No players chose to sponsor {} quest", gameEngine.storyDeck.faceUp.getName());
					setTimeout(() -> {
						try {
							flipStoryCard();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}, 2000);
					return;
				}
				gameEngine.getActivePlayer().session.sendMessage(new TextMessage("sponsorQuest"));
			}
		}

		// json for quest setup info from sponsor
		if (jsonObject.has("foes")) {
			gameEngine.current_quest.initiateFoes(jsonObject);
			// System.out.println(jsonObject.toString());
			logger.info("Player {} has setup {} quest with foes: {} and weapons: {}",
					gameEngine.current_quest.sponsor.getName(), gameEngine.storyDeck.faceUp.getName(),
					jsonObject.get("foes"), jsonObject.get("weapons"));
			String jsonOutput = "currentQuestInfo" + jsonObject.toString();
			sendToAllSessions(gameEngine, jsonOutput);
			sendToNextPlayer(gameEngine, "AskToParticipate");
			gameEngine.incTurn();
			ArrayList<String> cardToRemove = new ArrayList<String>();
			JsonElement Foes = jsonObject.get("foes");
			Type listType = new TypeToken<List<String>>() {
			}.getType();
			List<String> foeList = new Gson().fromJson(Foes, listType);
			int tracker = 0;
			for (String i : foeList) {
				cardToRemove.add(i + tracker);
				tracker++;
			}
			tracker = 0;
			// System.out.println(cardToRemove);
			JsonArray foeWeapons = (JsonArray) jsonObject.get("weapons");
			for (int i = 0; i < foeWeapons.size(); i++) {
				JsonArray temp = (JsonArray) foeWeapons.get(i);
				for (int j = 0; j < temp.size(); j++) {
					cardToRemove.add(temp.get(j).getAsString() + i);
					// System.out.println(temp.get(j).getAsString());
				}

			}
			// System.out.println(cardToRemove);
			gameEngine.current_quest.sponsor.discardSponsor(cardToRemove);

			return;
		}

		// json for accepting/decline participating in quest
		if (jsonObject.has("participate_quest")) {
			JsonElement participate_quest_answer = jsonObject.get("participate_quest");
			JsonElement name = jsonObject.get("name");

			if (participate_quest_answer.getAsBoolean()) {
				logger.info("Player {} accepted to participate in {} quest sponsored by {}", name.getAsString(),
						gameEngine.storyDeck.faceUp.getName(), gameEngine.current_quest.sponsor.getName());
				gameEngine.current_quest.participants.add(gameEngine.getActivePlayer());
				if (gameEngine.current_quest.participants.size() == 1) {
					gameEngine.current_quest.firstQuestPlayer = gameEngine.getCurrentParticipant();
				}
				gameEngine.incTurn();
				if (gameEngine.getActivePlayer().equals(gameEngine.current_quest.sponsor)) {
					gameEngine.current_quest.initialPSize = gameEngine.current_quest.participants.size();
					gameEngine.getActivePlayer().session.sendMessage(new TextMessage("ReadyToStartQuest"));
					gameEngine.adventureDeck.flipCard();
					String newCardLink = gameEngine.adventureDeck.faceUp.getStringFile();
					gameEngine.getCurrentParticipant().session.sendMessage(new TextMessage("Choose equipment"));
					gameEngine.getCurrentParticipant().session
							.sendMessage(new TextMessage("pickupBeforeStage" + newCardLink));

					return;
				}
				gameEngine.getActivePlayer().session.sendMessage(new TextMessage("AskToParticipate"));
			} else {
				logger.info("Player {} denied to participate in {} quest sponsored by {}", name.getAsString(),
						gameEngine.storyDeck.faceUp.getName(), gameEngine.current_quest.sponsor.getName());
				gameEngine.incTurn();
				if (gameEngine.getActivePlayer().equals(gameEngine.current_quest.sponsor)) {
					gameEngine.current_quest.initialPSize = gameEngine.current_quest.participants.size();
					if (gameEngine.current_quest.participants.size() == 0) {

						sendToAllSessions(gameEngine, "NoParticipants");
						String temp = "";
						for (int i = gameEngine.getActivePlayer().getHandSize(); i < 12
								+ gameEngine.current_quest.currentQuest.getStages(); i++) {
							AdventureCard newCard = gameEngine.adventureDeck.flipCard();
							temp += newCard.getStringFile() + ";";
							gameEngine.getActivePlayer().getHand().add(newCard);
						}
						gameEngine.getActivePlayer().session.sendMessage(new TextMessage("SponsorPickup" + temp));
						logger.info("No players participated, {} is replacing cards used to setup {} quest",
								gameEngine.getActivePlayer().getName(), gameEngine.storyDeck.faceUp.getName());
						return;
					}
					gameEngine.adventureDeck.flipCard();
					String newCardLink = gameEngine.adventureDeck.faceUp.getStringFile();
					gameEngine.getCurrentParticipant().session.sendMessage(new TextMessage("Choose equipment"));
					gameEngine.getCurrentParticipant().session
							.sendMessage(new TextMessage("pickupBeforeStage" + newCardLink));
					gameEngine.current_quest.firstQuestPlayer = gameEngine.getCurrentParticipant();
					return;
				}
				gameEngine.getActivePlayer().session.sendMessage(new TextMessage("AskToParticipate"));
			}
		}

		// flip story deck
		if (jsonObject.has("flipStoryCard")) {
			gameEngine.incTurn();
			flipStoryCard();
		}

		// json for getting participants battle equipment
		if (jsonObject.has("equipment_info")) {

			String jsonOutput = "currentParticipantInfo" + jsonObject.toString();
			sendToAllSessions(gameEngine, jsonOutput);
			logger.info("Player {} chose {} to equip for stage {} battle of {} quest", jsonObject.get("name"),
					jsonObject.get("equipment_info"), jsonObject.get("stages"), gameEngine.storyDeck.faceUp.getName());
			gameEngine.current_quest.equipPlayer(jsonObject);
			return;
		}

		if (jsonObject.has("nextQuestTurn")) {
			gameEngine.getCurrentParticipant().getWeapons().clear();

			boolean BattleResult = jsonObject.get("nextQuestTurn").getAsBoolean();
			if (BattleResult == false) {
				logger.info("Player {} was defeated in {} quest battle",
						gameEngine.current_quest.getCurrentParticipant().getName(),
						gameEngine.storyDeck.faceUp.getName());

				if (gameEngine.current_quest.getNextParticipant().equals(gameEngine.current_quest.firstQuestPlayer)) {
					gameEngine.current_quest.firstQuestPlayer = gameEngine.current_quest.getNextParticipant();
					sendToAllSessions(gameEngine, "incStage");
					gameEngine.current_quest.currentStage++;
					if (gameEngine.current_quest.currentStage > gameEngine.current_quest.currentQuest.getStages()) {
						gameEngine.current_quest.participants.remove(gameEngine.current_quest.getCurrentParticipant());

						Winning();
						return;
					}
				}
				if (gameEngine.current_quest.getCurrentParticipant()
						.equals(gameEngine.current_quest.firstQuestPlayer)) {
					gameEngine.current_quest.firstQuestPlayer = gameEngine.current_quest.getNextParticipant();
					gameEngine.current_quest.participants.remove(gameEngine.current_quest.getCurrentParticipant());
					if (gameEngine.current_quest.participants.isEmpty()) {
						Losing();
						return;
					}
					gameEngine.current_quest.incTurn();

					gameEngine.adventureDeck.flipCard();
					gameEngine.getCurrentParticipant().getHand().add(gameEngine.adventureDeck.faceUp);
					String newCardLink = gameEngine.adventureDeck.faceUp.getStringFile();
					gameEngine.current_quest.getCurrentParticipant().session
							.sendMessage(new TextMessage("Choose equipment"));
					gameEngine.current_quest.getCurrentParticipant().session
							.sendMessage(new TextMessage("pickupBeforeStage" + newCardLink));

					return;

				}
				gameEngine.current_quest.participants.remove(gameEngine.current_quest.getCurrentParticipant());
			}
			if (gameEngine.current_quest.participants.size() == 0) {
				Losing();
				return;
			}

			if (BattleResult == true) {

				logger.info("Player {} was victorious in {} quest battle",
						gameEngine.current_quest.getCurrentParticipant().getName(),
						gameEngine.current_quest.QuestFoes.get(gameEngine.current_quest.currentStage - 1).getName(),
						gameEngine.storyDeck.faceUp.getName());
				if (gameEngine.current_quest.participants.size() == 1) {
					sendToAllSessions(gameEngine, "incStage");
					gameEngine.current_quest.currentStage++;
					if (gameEngine.current_quest.currentStage > gameEngine.current_quest.currentQuest.getStages()) {
						Winning();
						return;
					}
					gameEngine.adventureDeck.flipCard();
					gameEngine.getCurrentParticipant().getHand().add(gameEngine.adventureDeck.faceUp);
					String newCardLink = gameEngine.adventureDeck.faceUp.getStringFile();
					gameEngine.getCurrentParticipant().session.sendMessage(new TextMessage("Choose equipment"));
					gameEngine.getCurrentParticipant().session
							.sendMessage(new TextMessage("pickupBeforeStage" + newCardLink));

					return;
				}

				if (gameEngine.current_quest.getNextParticipant().equals(gameEngine.current_quest.firstQuestPlayer)) {
					sendToAllSessions(gameEngine, "incStage");
					gameEngine.current_quest.currentStage++;
					if (gameEngine.current_quest.currentStage > gameEngine.current_quest.currentQuest.getStages()) {
						Winning();
						return;
					}
				}
				gameEngine.current_quest.incTurn();

			}

			gameEngine.adventureDeck.flipCard();
			gameEngine.getCurrentParticipant().getHand().add(gameEngine.adventureDeck.faceUp);
			String newCardLink = gameEngine.adventureDeck.faceUp.getStringFile();
			gameEngine.getCurrentParticipant().session.sendMessage(new TextMessage("Choose equipment"));
			gameEngine.getCurrentParticipant().session.sendMessage(new TextMessage("pickupBeforeStage" + newCardLink));
		}
	}

	public void Winning() throws IOException {
		boolean sendOnce = true;
		String winners = "";
		for (Player p : gameEngine.current_quest.participants) {

			winners += p.getName() + ",";
		}
		if (winners.equals("")) {
			Losing();
			return;
		}
		logger.info("Player(s) {} are the winners of {} quest sponsored by {}, receiving {} shields", winners,
				gameEngine.storyDeck.faceUp.getName(), gameEngine.current_quest.sponsor.getName(),
				gameEngine.current_quest.currentQuest.getStages());
		for (int i = 0; i < gameEngine.players.size(); i++) {
			for (int j = 0; j < gameEngine.current_quest.participants.size(); j++) {

				if (gameEngine.players.get(i).getName()
						.equals(gameEngine.current_quest.participants.get(j).getName())) {
					gameEngine.current_quest.participants.get(j)
							.giveShields(gameEngine.current_quest.currentQuest.getStages());
					gameEngine.current_quest.participants.get(j).session.sendMessage(new TextMessage("Getting "
							+ gameEngine.current_quest.currentQuest.getStages() + " shields for winning quest"));
					logger.info("Giving shields to {}", gameEngine.current_quest.participants.get(j).getName());
				} else {
					gameEngine.players.get(i).session.sendMessage(new TextMessage("QuestOverWaitForSponsor"));
					if (gameEngine.players.get(i).equals(gameEngine.current_quest.sponsor)) {
						String temp = "";
						int cardTracker = 0;
						for (int k = gameEngine.current_quest.sponsor.getHandSize(); k < 12
								+ gameEngine.current_quest.currentQuest.getStages(); k++) {
							cardTracker++;
							AdventureCard newCard = gameEngine.adventureDeck.flipCard();
							temp += newCard.getStringFile() + ";";
							gameEngine.current_quest.sponsor.getHand().add(newCard);
						}
						if (sendOnce) {
							logger.info(
									"Player {} who sponsored {} quest is receiving {} card due to"
											+ " sponsoring quest",
									gameEngine.current_quest.sponsor.getName(), gameEngine.storyDeck.faceUp.getName(),
									cardTracker);
							gameEngine.current_quest.sponsor.session
									.sendMessage(new TextMessage("SponsorPickup" + temp));
							cardTracker = 0;
							sendOnce = false;
						}

					}

				}
			}
		}
		sendOnce = true;
		return;
	}

	public void Losing() throws IOException {
		logger.info("All players defeated in {} quest sponsored by {}", gameEngine.storyDeck.faceUp.getName(),
				gameEngine.current_quest.sponsor.getName());
		sendToAllSessions(gameEngine, "QuestOverWaitForSponsor");
		String temp = "";
		int cardTracker = 0;
		for (int i = gameEngine.current_quest.sponsor.getHandSize(); i < 12
				+ gameEngine.current_quest.currentQuest.getStages(); i++) {
			cardTracker++;
			AdventureCard newCard = gameEngine.adventureDeck.flipCard();
			temp += newCard.getStringFile() + ";";
			gameEngine.current_quest.sponsor.getHand().add(newCard);
		}
		logger.info("Player {} who sponsored {} quest is receiving {} card due to" + " sponsoring quest",
				gameEngine.current_quest.sponsor.getName(), gameEngine.storyDeck.faceUp.getName(), cardTracker);
		gameEngine.current_quest.sponsor.session.sendMessage(new TextMessage("SponsorPickup" + temp));
		cardTracker = 0;
	}

	// flip story deck
	int tracker = 0;

	public void flipStoryCard() throws IOException {

		if (tracker == 0)
			gameEngine.storyDeck.faceUp = CardList.Quest6;
		if (tracker == 1)
			gameEngine.storyDeck.faceUp = CardList.Event1;
		if (tracker == 2)
			gameEngine.storyDeck.faceUp = CardList.Event8;
		// gameEngine.storyDeck.flipCard();

		logger.info("Flipping new card from story deck: {}", gameEngine.storyDeck.faceUp.getName());
		sendToAllSessions(gameEngine, ("flipStoryDeck" + gameEngine.storyDeck.faceUp.toString()));
		if (gameEngine.storyDeck.faceUp instanceof QuestCard) {
			gameEngine.roundInitiater = gameEngine.getActivePlayer();
			gameEngine.getActivePlayer().session.sendMessage(new TextMessage("sponsorQuest"));
		}
		if (gameEngine.storyDeck.faceUp instanceof TournamentCard) {

		}
		if (gameEngine.storyDeck.faceUp instanceof EventCard) {
			System.out.println("its " + gameEngine.getActivePlayer().getName() + "'s turn");
		}
		tracker++;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

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

		}
	}

	// send message to all players
	public void sendToAllSessions(Game gameEngine, String message) {
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

	// send to current participant
	public void sendToCurrentParticipant(Game gameEngine, String message) throws IOException {
		Player player = gameEngine.current_quest.getCurrentParticipant();
		player.session.sendMessage((new TextMessage(message)));
	}

	// send to next participant
	public void sendToNextParticipant(Game gameEngine, String message) throws IOException {
		Player player = gameEngine.current_quest.getNextParticipant();
		player.session.sendMessage(new TextMessage(message));
	}

	// send message to next player
	public void sendToNextPlayer(Game gameEngine, String message) throws IOException {
		Player player = gameEngine.getNextPlayer();
		player.session.sendMessage(new TextMessage(message));
	}

	public void sendToAllPlayersExcept(Game gameEngine, Player p, String message) throws IOException {
		ArrayList<Player> tempList = new ArrayList<Player>();
		for (int i = 0; i < gameEngine.players.size(); i++) {
			if (gameEngine.players.get(i).equals(p)) {
			} else {
				tempList.add(gameEngine.players.get(i));
			}
		}
		for (Player player : tempList) {
			player.session.sendMessage(new TextMessage(message));
		}
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

	public static void setTimeout(Runnable runnable, int delay) {
		new Thread(() -> {
			try {
				Thread.sleep(delay);
				runnable.run();
			} catch (Exception e) {
				System.err.println(e);
			}
		}).start();
	}
}// end of class