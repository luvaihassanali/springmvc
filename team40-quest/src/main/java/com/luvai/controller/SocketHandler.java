package com.luvai.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
	JsonArray questInformation = new JsonArray();

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
			gameEngine.setupNewPlayer(jsonObject, session);
		}
		// sponsoring quuest
		if (jsonObject.has("sponsor_quest")) {
			gameEngine.getSponsor(jsonObject);
		}

		// json for quest setup info from sponsor
		if (jsonObject.has("questSetupCards")) {
			logger.info("Player {} setup {} quest with {}", gameEngine.current_quest.sponsor.getName(),
					gameEngine.storyDeck.faceUp.getName(), jsonObject.get("questSetupCards").toString());
			questInformation.add(jsonObject);
			sendToAllSessionsExceptCurrent(gameEngine, session,
					"questSetupCards" + jsonObject.get("questSetupCards").toString());
			gameEngine.current_quest.parseQuestInfo(jsonObject);
		}

		// if out of stages
		if (jsonObject.has("outOfStages")) {
			Winning();
		}
		// json for accepting/decline participating in quest
		if (jsonObject.has("participate_quest")) {
			gameEngine.current_quest.getNewParticipants(jsonObject, session);
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
			if (jsonObject.get("isTest").getAsBoolean()) {
				gameEngine.current_quest.initialStageForTest = gameEngine.current_quest.currentStage;
				System.out.println(jsonObject.toString());
				gameEngine.current_quest.parseEquipmentInfo(jsonObject);
				questInformation.add(jsonObject);
				System.out.println("HERE FIRST TIME OVER");
				System.out.println(gameEngine.current_quest.toDiscardAfterTest.isEmpty());
				System.out.println(gameEngine.current_quest.toDiscardAfterTest.size());
				System.out.println(gameEngine.current_quest.currentStage);
				System.out.println(gameEngine.current_quest.participants.size());

				for (Player p : gameEngine.current_quest.participants) {
					System.out.println(p.getName());
				}

				if (gameEngine.current_quest.participants.size() == 1) {
					if (gameEngine.current_quest.toDiscardAfterTest.size() == 0) {
						logger.info("Player {} dropped out of {} test",
								gameEngine.current_quest.getCurrentParticipant().getName());
						gameEngine.current_quest.participants.remove(gameEngine.current_quest.getCurrentParticipant());
						Losing();
						return;
					}
					logger.info("There is only one participant left in quest test, automatic minimum bid pass");
					System.out.println("to remove after test: ");
					gameEngine.current_quest.getCurrentParticipant()
							.discardPlayer(gameEngine.current_quest.getCurrentParticipant().testDiscardList);
					gameEngine.current_quest.getCurrentParticipant().testDiscardList.clear();
					for (int i = 0; i < gameEngine.current_quest.getCurrentParticipant().testDiscardList.size(); i++) {
						System.out.println(gameEngine.current_quest.getCurrentParticipant().testDiscardList.get(i));
					}

					gameEngine.current_quest.currentStage++;
					sendToAllParticipants(gameEngine, "incStage");
					if (gameEngine.current_quest.currentStage > gameEngine.current_quest.currentQuest.getStages()) {
						Winning();
						return;
					}
					gameEngine.getCurrentParticipant().session.sendMessage(new TextMessage("ChooseEquipment"));
					return;
				}

				System.out.println("socket handler 110");
				System.out.println(gameEngine.current_quest.getCurrentParticipant().getName());
				System.out.println(gameEngine.current_quest.getCurrentParticipant().getHandSize());
				System.out.println(gameEngine.current_quest.toDiscardAfterTest.size());
				System.out.println(gameEngine.current_quest.toDiscardAfterTest.isEmpty());
				if (gameEngine.current_quest.toDiscardAfterTest.size() == 0) {
					System.out.println("player dropped out - remove");
					gameEngine.current_quest.participants.remove(gameEngine.current_quest.getCurrentParticipant());
				}
				sendToAllSessions(gameEngine, "allPlayerQuestInfo" + questInformation.toString());
				if (gameEngine.current_quest.participants.size() == 1) {
					if (gameEngine.current_quest.currentStage > gameEngine.current_quest.currentQuest.getStages()) {
						Winning();
						return;
					} else {
						sendToAllSessions(gameEngine, "incStage");
						gameEngine.current_quest.currentStage++;
					}
				}

				System.out.println("HERE TWICE OVER NOW");
				System.out.println(gameEngine.current_quest.currentStage);
				System.out.println(gameEngine.current_quest.participants.size());
				gameEngine.current_quest.incTurn();
				gameEngine.getCurrentParticipant().session
						.sendMessage(new TextMessage("updateMinBid" + gameEngine.current_quest.currentMinBid));
				gameEngine.getCurrentParticipant().session.sendMessage(new TextMessage("ChooseEquipment"));
				return;
			}
			gameEngine.current_quest.equipmentTracker++;
			System.out.println(jsonObject.toString());
			gameEngine.current_quest.parseEquipmentInfo(jsonObject);
			questInformation.add(jsonObject);
			if (gameEngine.current_quest.equipmentTracker == gameEngine.current_quest.getParticipants().size()) {
				gameEngine.current_quest.equipmentTracker = 0;
				// sendToAllSessions(gameEngine, "showStages");
				// System.out.println("got all equips");
				// System.out.println(questInformation.toString());
				logger.info("All players have chosen equipment for stage {} of {} quest which is a test: {}",
						gameEngine.current_quest.currentStage, gameEngine.storyDeck.faceUp.getName(),
						jsonObject.get("isTest").getAsBoolean());
				sendToAllSessions(gameEngine, "allPlayerQuestInfo" + questInformation.toString());
				String playerPoints = "";
				for (int i = 0; i < gameEngine.current_quest.participants.size(); i++) {
					playerPoints += gameEngine.current_quest.participants.get(i).getName() + "#"
							+ gameEngine.current_quest.calculatePlayerPoints(
									gameEngine.current_quest.participants.get(i).getName())
							+ ";";
				}
				sendToAllSessions(gameEngine, "playerPointString" + playerPoints);
				gameEngine.current_quest.calculateStageOutcome(playerPoints, questInformation);
				return;
			}
		}

		if (jsonObject.has("nextQuestTurn")) {
			gameEngine.current_quest.goToNextTurn(jsonObject);
		}

		// done events

		if (jsonObject.has("doneEventProsperity")) {
			gameEngine.AIController.doneProsperity();
		}
		// rigged game
		if (jsonObject.has("riggedGame")) {
			int version = jsonObject.get("riggedGame").getAsInt();
			if (version == 42) {
				logger.info("Setting up rigged game");
				gameEngine.riggedGame = 42;
				gameEngine.storyDeck.initRiggedStoryDeck(gameEngine.riggedGame);
			}

		}
		// print all gameEngine players for requested client
		if (jsonObject.has("print")) {
			printPlayers(session);
		}
		// discard

		if (jsonObject.has("discard")) {
			String discard = jsonObject.get("discard").toString();
			Player p = getPlayerFromSession(session);
			p.discard(discard);
		}

		// validation of connection and decks
		if (jsonObject.has("proof")) {
			validateDecks(session);
		}

		// getting ai player
		if (jsonObject.has("AI")) {
			gameEngine.AIController.setupNewAIPlayer(jsonObject, session);
		}
	}

	private void printPlayers(WebSocketSession session) throws IOException {
		session.sendMessage(new TextMessage("All players:\n"));
		String clientsString = "clientsString";
		for (Player p : gameEngine.players) {
			clientsString += "ID: " + p.id + " Name: " + p.getName() + "\n";
		}
		session.sendMessage(new TextMessage(clientsString));

	}

	private void validateDecks(WebSocketSession session) throws IOException {

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

					System.out.println("to remove after test: ");
					gameEngine.current_quest.getCurrentParticipant()
							.discardPlayer(gameEngine.current_quest.getCurrentParticipant().testDiscardList);
					gameEngine.current_quest.getCurrentParticipant().testDiscardList.clear();
					for (int k = 0; k < gameEngine.current_quest.getCurrentParticipant().testDiscardList.size(); k++) {
						System.out.println(gameEngine.current_quest.getCurrentParticipant().testDiscardList.get(k));
					}

				} else {
					gameEngine.players.get(i).session.sendMessage(new TextMessage("QuestOverWaitForSponsor"));
					if (gameEngine.players.get(i).equals(gameEngine.current_quest.sponsor)) {
						String temp = "";
						String tempNames = "";
						int cardTracker = 0;

						for (int k = gameEngine.current_quest.sponsor.getHandSize(); k < 12
								+ gameEngine.current_quest.currentQuest.getStages(); k++) {
							cardTracker++;
							AdventureCard newCard = gameEngine.adventureDeck.flipCard();
							temp += newCard.getStringFile() + ";";
							tempNames += newCard.getName() + " ";
							gameEngine.current_quest.sponsor.getHand().add(newCard);
						}
						if (sendOnce) {
							logger.info(
									"Player {} who sponsored {} quest is receiving {} card ({}) due to"
											+ " sponsoring quest",
									gameEngine.current_quest.sponsor.getName(), gameEngine.storyDeck.faceUp.getName(),
									cardTracker, tempNames);
							gameEngine.current_quest.sponsor.session
									.sendMessage(new TextMessage("SponsorPickup" + temp));
							cardTracker = 0;
							sendOnce = false;
							gameEngine.updateStats();
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

	public static void flipStoryCard() throws IOException {
		gameEngine.updateStats();
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

	// send to current active player
	public static void sendToActivePlayer(Game gameEngine, String message) throws IOException {
		Player temp = gameEngine.getActivePlayer();
		temp.session.sendMessage(new TextMessage(message));
	}

	// send message to all players
	public static void sendToAllSessions(Game gameEngine, String message) {
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

	// send to non participants
	public void sendToNonParticipants(Game gameEngine, String message) throws IOException {
		ArrayList<Player> temp = new ArrayList<Player>();
		for (int i = 0; i < gameEngine.players.size(); i++) {
			temp.add(gameEngine.players.get(i));
		}
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i).equals(gameEngine.current_quest.sponsor))
				temp.remove(i);
		}
		for (int i = 0; i < temp.size(); i++) {
			for (int j = 0; j < gameEngine.current_quest.participants.size(); j++) {
				if (temp.get(i).equals(gameEngine.current_quest.participants.get(j)))
					temp.remove(i);
				if (temp.isEmpty())
					return;
			}
		}

		for (int i = 0; i < temp.size(); i++) {
			// System.out.println(temp.get(i).getName());
			temp.get(i).session.sendMessage(new TextMessage(message));
		}

	}

	// send to all participants
	public void sendToAllParticipants(Game gameEngine, String message) throws IOException {
		for (Player p : gameEngine.current_quest.participants) {
			p.session.sendMessage(new TextMessage(message));
		}
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

	public void sendToSponsor(Game gameEngine, String message) throws IOException {
		Player player = gameEngine.current_quest.sponsor;
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

}// end of class