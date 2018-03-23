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
import com.luvai.model.Game;
import com.luvai.model.Player;
import com.luvai.model.AdventureCards.AdventureCard;
import com.luvai.model.StoryCards.EventCard;
import com.luvai.model.StoryCards.QuestCard;
import com.luvai.model.StoryCards.StoryCard;
import com.luvai.model.StoryCards.TournamentCard;

@Component
public class SocketHandler extends TextWebSocketHandler {

	List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
	private static final Logger logger = LogManager.getLogger(SocketHandler.class);
	public static Game gameEngine = new Game();
	public static boolean rankSet = true;
	public String BattleInformation = "";
	public int riggedGame = 0;

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {

		JsonObject jsonObject = (new JsonParser()).parse(message.getPayload()).getAsJsonObject();

		// send ai to controller
		if (jsonObject.has("AICommand")) {
			gameEngine.AIController.receiveAICommand(jsonObject);
		}
		// get player name and set up game players
		if (jsonObject.has("newName")) {
			JsonElement playerName = jsonObject.get("newName");
			Player newPlayer = new Player(playerName.getAsString(), session);
			gameEngine.players.add(newPlayer);
			logger.info("Player {} is enrolled in the game", playerName.getAsString());
			if (gameEngine.players.size() == 4) {
				sendToAllSessions(gameEngine, "GameReadyToStart");
				logger.info("All players have joined, starting game...");
				if (riggedGame != 0) {
					if (riggedGame == 42) {
						gameEngine.players.get(0).setHand(gameEngine.mockHand1); // pickUpNewHand()
						gameEngine.players.get(1).setHand(gameEngine.mockHand2);
						gameEngine.players.get(2).setHand(gameEngine.mockHand3);
						gameEngine.players.get(3).setHand(gameEngine.mockHand4);

					}
				} else {
					for (Player p : gameEngine.players) {
						p.pickupNewHand(gameEngine.adventureDeck);
					}
				}
				for (Player p : gameEngine.players) {
					p.session.sendMessage(new TextMessage("setHand" + p.getHandString()));
				}

				flipStoryCard();
				String temp = gameEngine.getPlayerStats();
				sendToAllSessions(gameEngine, "updateStats" + temp);

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
					gameEngine.incTurn();
					gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
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

			JsonArray foeWeapons = (JsonArray) jsonObject.get("weapons");
			for (int i = 0; i < foeWeapons.size(); i++) {
				JsonArray temp = (JsonArray) foeWeapons.get(i);
				for (int j = 0; j < temp.size(); j++) {
					cardToRemove.add(temp.get(j).getAsString() + i);
					// System.out.println(temp.get(j).getAsString());
				}

			}
			gameEngine.current_quest.sponsor.discardSponsor(cardToRemove);
			String update = gameEngine.getPlayerStats();
			sendToAllSessions(gameEngine, "updateStats" + update);
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
					gameEngine.getCurrentParticipant().getHand().add(gameEngine.adventureDeck.flipCard());
					String newCardLink = gameEngine.adventureDeck.faceUp.getStringFile();
					gameEngine.getCurrentParticipant().session.sendMessage(new TextMessage("Choose equipment"));
					gameEngine.getCurrentParticipant().session
							.sendMessage(new TextMessage("pickupBeforeStage" + newCardLink));
					String update = gameEngine.getPlayerStats();
					sendToAllSessions(gameEngine, "updateStats" + update);
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
						logger.info("No players participated, sponsor {} is replacing cards used to setup {} quest",
								gameEngine.getActivePlayer().getName(), gameEngine.storyDeck.faceUp.getName());
						String update = gameEngine.getPlayerStats();
						sendToAllSessions(gameEngine, "updateStats" + update);
						return;
					}
					gameEngine.adventureDeck.flipCard();
					gameEngine.getCurrentParticipant().getHand().add(gameEngine.adventureDeck.faceUp);
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

		if (jsonObject.has("flipStoryDeck")) {
			flipStoryCard();
		}
		// flip story deck
		if (jsonObject.has("incTurnRoundOver")) {
			gameEngine.incTurn();
			gameEngine.getActivePlayer().session.sendMessage((new TextMessage("undisableFlip")));
		}

		// json for getting participants battle equipment
		if (jsonObject.has("equipment_info")) {

			String jsonOutput = "currentParticipantInfo" + jsonObject.toString();
			sendToAllSessions(gameEngine, jsonOutput);
			logger.info("Player {} chose {} to equip for stage {} battle of {} quest", jsonObject.get("name"),
					jsonObject.get("equipment_info"), jsonObject.get("stages"), gameEngine.storyDeck.faceUp.getName());
			gameEngine.current_quest.equipPlayer(jsonObject);
			String update = gameEngine.getPlayerStats();
			sendToAllSessions(gameEngine, "updateStats" + update);
			return;
		}

		if (jsonObject.has("nextQuestTurn")) {
			if (gameEngine.getCurrentParticipant().getWeapons().size() != 0) {
				logger.info("Player {} is unequipping {} used during battle",
						gameEngine.getCurrentParticipant().getName(),
						gameEngine.getCurrentParticipant().getWeapons().toString());
				gameEngine.getCurrentParticipant().getWeapons().clear();
			}
			boolean BattleResult = jsonObject.get("nextQuestTurn").getAsBoolean();
			if (BattleResult == false) {
				String update = gameEngine.getPlayerStats();
				sendToAllSessions(gameEngine, "updateStats" + update);
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
				String update = gameEngine.getPlayerStats();
				sendToAllSessions(gameEngine, "updateStats" + update);
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
		// done events

		if (jsonObject.has("doneEventProsperity")) {
			System.out.println(gameEngine.current_event.prosperityTracker);
			gameEngine.current_event.prosperityTracker++;
			if (gameEngine.current_event.prosperityTracker == 4) {
				logger.info("Event {} has concluded", gameEngine.storyDeck.faceUp.getName());
				gameEngine.current_event.prosperityTracker = 0;
				gameEngine.incTurn();
				gameEngine.getActivePlayer().session.sendMessage((new TextMessage("undisableFlip")));
			}

		}
		// rigged game
		if (jsonObject.has("riggedGame")) {
			int version = jsonObject.get("riggedGame").getAsInt();
			if (version == 42) {
				logger.info("Setting up rigged game");
				riggedGame = 42;
				gameEngine.storyDeck.initRiggedStoryDeck(riggedGame);
			}

		}
		// print all gameEngine players for requested client
		if (jsonObject.has("print")) {
			session.sendMessage(new TextMessage("All players:\n"));
			String clientsString = "clientsString";
			for (Player p : gameEngine.players) {
				clientsString += "ID: " + p.id + " Name: " + p.getName() + "\n";
			}
			session.sendMessage(new TextMessage(clientsString));
		}
		// discard
		if (jsonObject.has("discard")) {
			String discard = jsonObject.get("discard").toString();
			Player p = getPlayerFromSession(session);
			p.discard(discard);
			String update = gameEngine.getPlayerStats();
			sendToAllSessions(gameEngine, "updateStats" + update);
		}
		// validation of connection and decks
		if (jsonObject.has("proof")) {
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

		// getting ai player
		if (jsonObject.has("AI")) {
			JsonElement playerName = jsonObject.get("AI");
			Player newPlayer = new Player(playerName.getAsString(), session, 2);
			gameEngine.players.add(newPlayer);
			logger.info("Player {} is enrolled in the game", playerName.getAsString());
			if (gameEngine.players.size() == 4) {
				sendToAllSessions(gameEngine, "GameReadyToStart");
				logger.info("All players have joined, starting game...");
				if (riggedGame != 0) {
					if (riggedGame == 42) {
						gameEngine.players.get(0).setHand(gameEngine.mockHand1); // pickUpNewHand()
						gameEngine.players.get(1).setHand(gameEngine.mockHand2);
						gameEngine.players.get(2).setHand(gameEngine.mockHand3);
						gameEngine.players.get(3).setHand(gameEngine.mockHand4);

					}
				} else {
					for (Player p : gameEngine.players) {
						p.pickupNewHand(gameEngine.adventureDeck);
					}
				}
				for (Player p : gameEngine.players) {
					p.session.sendMessage(new TextMessage("setHand" + p.getHandString()));
				}

				flipStoryCard();
				String temp = gameEngine.getPlayerStats();
				sendToAllSessions(gameEngine, "updateStats" + temp);

			}
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
							String update = gameEngine.getPlayerStats();
							sendToAllSessions(gameEngine, "updateStats" + update);
							for (Player p : gameEngine.players) {
								AdventureCard amour = p.getAmourCard();
								if (amour == null) {
								} else {

									p.unequipAmour();
									logger.info("Player {} is unequipping the amour used in last quest", p.getName());
								}
							}

						}

					}

				}
			}
		}
		String update = gameEngine.getPlayerStats();
		sendToAllSessions(gameEngine, "updateStats" + update);
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
		String update = gameEngine.getPlayerStats();
		sendToAllSessions(gameEngine, "updateStats" + update);
		for (Player p : gameEngine.players) {
			AdventureCard amour = p.getAmourCard();
			if (amour == null) {
			} else {

				p.unequipAmour();
				logger.info("Player {} is unequipping the amour used in last quest", p.getName());
			}
		}
	}

	// flip story deck

	public void flipStoryCard() throws IOException {
		String update = gameEngine.getPlayerStats();
		sendToAllSessions(gameEngine, "updateStats" + update);
		gameEngine.storyDeck.flipCard();

		logger.info("Player {} is flipping new card from story deck: {}", gameEngine.getActivePlayer().getName(),
				gameEngine.storyDeck.faceUp.getName());
		sendToAllSessions(gameEngine, ("flipStoryDeck" + gameEngine.storyDeck.faceUp.toString()));
		if (gameEngine.storyDeck.faceUp instanceof QuestCard) {
			gameEngine.roundInitiater = gameEngine.getActivePlayer();
			gameEngine.getActivePlayer().session.sendMessage(new TextMessage("sponsorQuest"));
		}
		if (gameEngine.storyDeck.faceUp instanceof TournamentCard) {
			System.out.println("its " + gameEngine.getActivePlayer().getName() + "'s turn");
		}
		if (gameEngine.storyDeck.faceUp instanceof EventCard) {
			gameEngine.newEvent(gameEngine, gameEngine.getActivePlayer(), (EventCard) gameEngine.storyDeck.faceUp);
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		if (session.getId().equals("0"))
			session.sendMessage(new TextMessage("showRigger"));
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