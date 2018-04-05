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
import com.google.gson.JsonElement;
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
	public int bonusTestCardControl = 0;
	public boolean sendOnce = true;
	public boolean sentAlready = true;

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
		// sponsoring quest
		if (jsonObject.has("sponsor_quest")) {
			JsonElement sponsor_quest_answer = jsonObject.get("sponsor_quest");
			if (sponsor_quest_answer.getAsBoolean()) {
				questInformation = new JsonArray();
			}
			gameEngine.getSponsor(jsonObject);
		}

		// json for quest setup info from sponsor
		if (jsonObject.has("questSetupCards")) {
			logger.info("Player {} setup {} quest with {}", gameEngine.current_quest.sponsor.getName(),
					gameEngine.storyDeck.faceUp.getName(), jsonObject.get("questSetupCards").toString());
			questInformation.add(jsonObject);
			sendToAllSessions(gameEngine, "questSetupCards" + jsonObject.get("questSetupCards").toString());
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

		// json for accepting/decline participating in tournament
		if (jsonObject.has("participate_tournament")) {

			gameEngine.current_tournament.getNewTourniePlayers(jsonObject, session);
		}

		// flip new card
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
					String aiRemove = "";
					for (int i = 0; i < gameEngine.current_quest.getCurrentParticipant().testDiscardList.size(); i++) {
						System.out.println(gameEngine.current_quest.getCurrentParticipant().testDiscardList.get(i));
						aiRemove += gameEngine.current_quest.getCurrentParticipant().testDiscardList.get(i) + ";";
					}
					if (gameEngine.getCurrentParticipant().isAI()) {
						gameEngine.getCurrentParticipant().session
								.sendMessage(new TextMessage("AIRemoveFromScreen" + aiRemove));
					}
					gameEngine.current_quest.getCurrentParticipant().testDiscardList.clear();
					System.out.println("REPLACE THESSE CARDS ON SCREEN");
					bonusTestCardControl = 1;
					String testBonusReplacement = "";
					for (String s : gameEngine.current_quest.getCurrentParticipant().replaceBonusBidsList) {
						System.out.println(s);
						testBonusReplacement += s + ";";
					}
					testBonusReplacement += "null";
					gameEngine.current_quest.getCurrentParticipant().session
							.sendMessage(new TextMessage("PickupCardsTestBonus" + testBonusReplacement));
					gameEngine.updateStats();

					gameEngine.current_quest.currentStage++;
					sendToAllParticipants(gameEngine, "incStage");
					logger.info("Going to stage {}", gameEngine.current_quest.currentStage);
					if (gameEngine.current_quest.currentStage > gameEngine.current_quest.currentQuest.getStages()) {
						Winning();
						return;
					}
					gameEngine.current_quest.pickupBeforeStage();
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

					if (jsonObject.has("oldBids")) {
						for (AdventureCard a : gameEngine.getCurrentParticipant().getHand()) {
							a.getName();
						}
						System.out.println("REPLACE TEST CARDS USED FOR LOSING PLAYER >>> "
								+ gameEngine.getCurrentParticipant().getName());
						JsonArray oldBids = (JsonArray) jsonObject.get("oldBids");
						String replaceTestCards = "";
						for (int i = 0; i < oldBids.size(); i++) {
							JsonElement temp = oldBids.get(i);
							System.out.println(temp.toString());
							replaceTestCards += temp.toString() + ";";
						}
						gameEngine.getCurrentParticipant().session
								.sendMessage(new TextMessage("replaceTestCards" + replaceTestCards));
						System.out.println("remove card");
						System.out.println(replaceTestCards);
						String[] replaceTestCardArr = replaceTestCards.split(";");
						String removeExtra = replaceTestCardArr[0].replaceAll(";", "");
						removeExtra = removeExtra.replaceAll("\"", "");
						System.out.println(removeExtra + " REMOVE EXTRA HERE ");
						for (AdventureCard a : gameEngine.getCurrentParticipant().getHand()) {
							if (a.getName().equals(removeExtra)) {
								gameEngine.getCurrentParticipant().getHand().remove(a);
								break;
							}
						}

					}

					gameEngine.current_quest.participants.remove(gameEngine.current_quest.getCurrentParticipant());

					gameEngine.updateStats();
				}
				sendToAllSessions(gameEngine, "allPlayerQuestInfo" + questInformation.toString());
				if (gameEngine.current_quest.participants.size() == 1) {
					if (gameEngine.current_quest.currentStage > gameEngine.current_quest.currentQuest.getStages()) {
						Winning();
						return;
					} else {
						sendToAllSessions(gameEngine, "incStage");
						gameEngine.current_quest.currentStage++;
						System.out.println("to remove after test: ");

						gameEngine.current_quest.getCurrentParticipant()
								.discardPlayer(gameEngine.current_quest.getCurrentParticipant().testDiscardList);
						String aiRemove = "";
						for (int i = 0; i < gameEngine.current_quest.getCurrentParticipant().testDiscardList
								.size(); i++) {
							System.out.println(gameEngine.current_quest.getCurrentParticipant().testDiscardList.get(i));
							aiRemove += gameEngine.current_quest.getCurrentParticipant().testDiscardList.get(i) + ";";
						}
						if (gameEngine.getCurrentParticipant().isAI()) {
							gameEngine.getCurrentParticipant().session
									.sendMessage(new TextMessage("AIRemoveFromScreen" + aiRemove));
						}
						gameEngine.current_quest.getCurrentParticipant().testDiscardList.clear();
						System.out.println("REPLACE THESSE CARDS ON SCREEN");
						bonusTestCardControl = 1;
						String testBonusReplacement = "";
						for (String s : gameEngine.current_quest.getCurrentParticipant().replaceBonusBidsList) {
							System.out.println(s);
							testBonusReplacement += s + ";";
						}
						testBonusReplacement += "null";
						gameEngine.current_quest.getCurrentParticipant().session
								.sendMessage(new TextMessage("PickupCardsTestBonus" + testBonusReplacement));
						gameEngine.updateStats();

					}
				}

				System.out.println("HERE TWICE OVER NOW");
				System.out.println(gameEngine.current_quest.currentStage);
				System.out.println(gameEngine.current_quest.participants.size());
				System.out.println(gameEngine.getCurrentParticipant().getName());
				gameEngine.current_quest.incTurn();
				sendToAllParticipants(gameEngine,
						"whoBidded" + gameEngine.current_quest.getCurrentParticipant().getName() + "#"
								+ gameEngine.current_quest.currentMinBid);
				sendToSponsor(gameEngine, "whoBidded" + gameEngine.current_quest.getCurrentParticipant().getName() + "#"
						+ gameEngine.current_quest.currentMinBid);
				sendToAllSessions(gameEngine, "updateMinBid" + gameEngine.current_quest.currentMinBid);

				for (int i = 0; i < gameEngine.current_quest.currentQuestInfo.length; i++) {
					System.out.println(gameEngine.current_quest.currentQuestInfo[i]);
				}
				if (gameEngine.current_quest.currentQuestInfo[gameEngine.current_quest.currentStage - 1]
						.contains("Test")) {
					System.out.println("in contains test " + gameEngine.getCurrentParticipant().getName());
				} else {
					gameEngine.current_quest.pickupBeforeStage();
				}
				System.out.println(
						"in outside contains test - it is turn of: " + gameEngine.getCurrentParticipant().getName());
				if (gameEngine.current_quest.participants.size() == 1) {
					logger.info("Player {} won test in {} quest, advancing to stage {}",
							gameEngine.getCurrentParticipant().getName(), gameEngine.storyDeck.faceUp.getName(),
							gameEngine.current_quest.currentStage);
				}
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
							+ gameEngine.current_quest
									.calculatePlayerPoints(gameEngine.current_quest.participants.get(i).getName())
							+ "#" + gameEngine.current_quest.participants.get(i).getRank().getStringFile() + ";";
				}
				sendToAllSessions(gameEngine, "playerPointString" + playerPoints);
				logger.info("Displaying battle screen");
				gameEngine.current_quest.calculateStageOutcome(playerPoints, questInformation);
				return;
			}
		}

		// remove stage card when test is first
		if (jsonObject.has("removeStageCardFromTest")) {
			System.out.println(jsonObject.toString());
			Player p = gameEngine.getPlayerFromName(jsonObject.get("name").getAsString());
			String cardName = jsonObject.get("removeStageCardFromTest").getAsString();
			String trimmedName = cardName.replace("http://localhost:8080/resources/images/", "");
			if (trimmedName.contains("all.png"))
				return;
			trimmedName = trimmedName.substring(4);
			trimmedName = trimmedName.substring(0, trimmedName.length() - 4);
			trimmedName = trimmedName.replaceAll("%20", " ");
			System.out.println(trimmedName);
			System.out.println(p.getName());
			p.discard(trimmedName);
			gameEngine.updateStats();
		}
		if (jsonObject.has("nextQuestTurn")) {
			gameEngine.current_quest.goToNextTurn(jsonObject);
		}

		// done events

		if (jsonObject.has("doneEventProsperity")) {
			gameEngine.AIController.doneProsperity();
		}
		// get tournie info
		if (jsonObject.has("tournament_info")) {
			gameEngine.current_tournament.parseTournieInfo(jsonObject);
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
			System.out.println("FOR i LOOP 327 SH");
			for (int j = 0; j < gameEngine.current_quest.participants.size(); j++) {
				System.out.println("FOR j LOOP 329 SH");
				if (gameEngine.players.get(i).getName()
						.equals(gameEngine.current_quest.participants.get(j).getName())) {
					if (gameEngine.current_quest.shieldSent) {
					} else {
						gameEngine.current_quest.participants.get(j)
								.giveShields(gameEngine.current_quest.currentQuest.getStages());
						gameEngine.current_quest.participants.get(j).session.sendMessage(new TextMessage("Getting "
								+ gameEngine.current_quest.currentQuest.getStages() + " shields for winning quest"));
						logger.info("Giving {} shields to {}", gameEngine.current_quest.currentQuest.getStages(),
								gameEngine.current_quest.participants.get(j).getName());
					}

					System.out.println("to remove after test: ");
					gameEngine.current_quest.getCurrentParticipant()
							.discardPlayer(gameEngine.current_quest.getCurrentParticipant().testDiscardList);

					for (int k = 0; k < gameEngine.current_quest.getCurrentParticipant().testDiscardList.size(); k++) {
						System.out.println(gameEngine.current_quest.getCurrentParticipant().testDiscardList.get(k));
					}
					gameEngine.current_quest.getCurrentParticipant().testDiscardList.clear();
					System.out.println("REPLACE THESSE CARDS ON SCREEN");
					String testBonusReplacement = "";
					for (String s : gameEngine.current_quest.getCurrentParticipant().replaceBonusBidsList) {
						System.out.println(s);
						testBonusReplacement += s + ";";
					}
					testBonusReplacement += "null";
					if (bonusTestCardControl == 1) {
					} else {
						gameEngine.current_quest.getCurrentParticipant().session
								.sendMessage(new TextMessage("PickupCardsTestBonus" + testBonusReplacement));
						bonusTestCardControl = 0;
					}
					gameEngine.updateStats();

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
							sentAlready = false;
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
			if (gameEngine.current_quest.participants.size() == 1) {
				if (sentAlready) {
					gameEngine.current_quest.sponsorPickup();
					sentAlready = false;
				}
			}
			gameEngine.current_quest.shieldSent = false;
			String update = gameEngine.getPlayerStats();
			sendToAllSessions(gameEngine, "updateStats" + update);
			sendOnce = true;
			return;
		}
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
			gameEngine.current_tournament = new TournamentController(gameEngine, gameEngine.storyDeck.faceUp);
			gameEngine.roundInitiater = gameEngine.getActivePlayer();
			gameEngine.getActivePlayer().session.sendMessage(new TextMessage("participateTournament"));
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

		for (int j = 0; j < gameEngine.current_quest.participants.size(); j++) {
			for (int i = 0; i < temp.size(); i++) {
				if (temp.isEmpty())
					return;
				if (temp.get(i).equals(gameEngine.current_quest.participants.get(j)))
					temp.remove(i);

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