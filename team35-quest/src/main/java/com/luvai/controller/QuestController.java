package com.luvai.controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.luvai.model.Card;
import com.luvai.model.CardList;
import com.luvai.model.Game;
import com.luvai.model.Player;
import com.luvai.model.PointArray;
import com.luvai.model.AdventureCards.AdventureCard;
import com.luvai.model.AdventureCards.AllyCard;
import com.luvai.model.AdventureCards.AmourCard;
import com.luvai.model.AdventureCards.FoeCard;
import com.luvai.model.AdventureCards.TestCard;
import com.luvai.model.AdventureCards.WeaponCard;
import com.luvai.model.StoryCards.QuestCard;

public class QuestController extends SocketHandler {
	private static final Logger logger = LogManager.getLogger(QuestController.class);
	Game gameEngine;
	public Player sponsor;
	public ArrayList<Player> participants;
	public QuestCard currentQuest;
	public ArrayList<FoeCard> QuestFoes;
	public ArrayList<TestCard> QuestTests;
	public String[] currentQuestInfo;
	public int currentMinBid = 0;
	public int participantTurns = 0;
	public int currentStage = 1;
	public int initialPSize = 0;
	public Player firstQuestPlayer;
	ArrayList<String> toDiscardAfterTest;
	public int equipmentTracker = 0;
	public PointArray foePoints = null;
	public int initialStageForTest = 0;
	ArrayList<String> aiQuestCardList;
	ArrayList<String> questCardList;
	public boolean shieldSent = false;
	public int originalBid = 0;

	public QuestController(Game g, Player s, QuestCard q) throws IOException {
		logger.info("Initiating new quest {} sponsored by {}", q.getName(), s.getName());
		participants = new ArrayList<Player>();
		QuestFoes = new ArrayList<FoeCard>();
		QuestTests = new ArrayList<TestCard>();
		gameEngine = g;
		sponsor = s;
		currentQuest = q;
		setupQuest();
	}

	public void placeBids(JsonObject json) throws IOException {
		JsonElement player_bids = json.get("equipment_info");
		Type listType = new TypeToken<List<String>>() {
		}.getType();
		List<String> bidList = new Gson().fromJson(player_bids, listType);
		toDiscardAfterTest = new ArrayList<String>(bidList);
		String testBidLogString = "";
		for (String s : toDiscardAfterTest) {
			// System.out.println(s + ";");
			testBidLogString += s + ", ";
		}
		if (testBidLogString == "") {
			logger.info("Player {} dropped out of test in quest {}", json.get("name").getAsString(),
					gameEngine.storyDeck.faceUp.getName());
		} else {
			logger.info("Player {} bid {} for test in quest {}", json.get("name").getAsString(), testBidLogString,
					gameEngine.storyDeck.faceUp.getName());
			// Player currPlayer =
			// gameEngine.getPlayerFromName(json.get("name").getAsString());
			// logger.info("Sending Player {} bid to sponsor and other participants",
			// currPlayer.getName());
			// sendToAllParticipants(gameEngine,
			// "whoBidded" + currPlayer.getName() + "#" +
			// gameEngine.current_quest.currentMinBid);
			// sendToSponsor(gameEngine,
			// "whoBidded" + currPlayer.getName() + "#" +
			// gameEngine.current_quest.currentMinBid);
			sendToAllSessions(gameEngine, "updateMinBid" + gameEngine.current_quest.currentMinBid);

		}
		calculateNumBids(bidList);
		ArrayList<String> tempDiscards = new ArrayList<String>(bidList);
		calculateTestDiscards(tempDiscards);
	}

	// get test discards - bids
	public void calculateTestDiscards(ArrayList<String> discardList) {
		int bonusBids = 0;
		// System.out.println(discardList);
		for (int i = 0; i < discardList.size(); i++) {
			AdventureCard tempCard = getCardFromName(discardList.get(i));
			if (tempCard instanceof AmourCard) {
				bonusBids += 1;
			}
			if (tempCard instanceof AllyCard) {
				AllyCard current_ally = (AllyCard) tempCard;
				bonusBids += current_ally.getBid();
			}
		}
		// System.out.println(toDiscardAfterTest);
		// System.out.println("Bonus bids" + bonusBids);

		for (int i = 0; i < bonusBids; i++) {
			String temp = toDiscardAfterTest.remove(0);
			gameEngine.current_quest.getCurrentParticipant().replaceBonusBidsList.add(temp);
		}
		gameEngine.current_quest.getCurrentParticipant().testDiscardList = toDiscardAfterTest;
	}

	// adjust player for points with chosen equipment
	public void equipPlayer(JsonObject json) {
		JsonElement player_equipment = json.get("equipment_info");
		String name = json.get("name").getAsString();
		// System.out.println(name);
		Player currentPlayer = gameEngine.getPlayerFromName(name);
		// System.out.println(currentPlayer.getName());
		Type listType = new TypeToken<List<String>>() {
		}.getType();
		List<String> equipmentList = new Gson().fromJson(player_equipment, listType);
		// System.out.println(equipmentList);
		for (int i = 0; i < equipmentList.size(); i++) {
			AdventureCard tempCard = getCardFromName(equipmentList.get(i));
			if (tempCard instanceof AllyCard) {

				currentPlayer.getAllies().add((AllyCard) tempCard);
			}
			if (tempCard instanceof WeaponCard) {
				currentPlayer.getWeapons().add((WeaponCard) tempCard);
			}
			if (tempCard instanceof AmourCard) {
				currentPlayer.setAmourCard(tempCard);
			}
		}
		ArrayList<String> remove = new ArrayList<String>(equipmentList);
		String logString = "";
		for (String s : equipmentList) {
			logString += s + ", ";
		}
		if (logString.equals(""))
			logString = "nothing";
		logger.info("Player {} chose {} for stage {} of {} quest", currentPlayer.getName(), logString,
				gameEngine.current_quest.currentStage, gameEngine.storyDeck.faceUp.getName());
		currentPlayer.discardPlayer(remove);
		// gameEngine.updateStats();
		// gameEngine.current_quest.incTurn();
		// calculatePlayerPoints();
		// gameEngine.getCurrentParticipant().getWeapons().clear();
		// String update = gameEngine.getPlayerStats();
		// sendToAllSessions(gameEngine, "updateStats" + update);
	}

	public void initiateQuestAI(ArrayList<Card> aiQuestcards) {
		ArrayList<String> cardToRemove = new ArrayList<String>();
		for (int i = 0; i < aiQuestcards.size(); i++) {
			cardToRemove.add(aiQuestcards.get(i).getName());

		}
		for (int i = 0; i < aiQuestcards.size(); i++) {
			if (aiQuestcards.get(i) instanceof FoeCard) {
				FoeCard f = (FoeCard) aiQuestcards.get(i);
				for (int j = 0; j < f.getWeapons().size(); j++) {
					cardToRemove.add(f.getWeapons().get(j).getName());
				}
			}

		}
		String[] array = cardToRemove.toArray(new String[cardToRemove.size()]);

		// System.out.println("F171----------------------------------------------------");
		String[] jsonArr = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			// System.out.println(array[i]);
			jsonArr[i] = "\"" + array[i] + "\"";
		}

		aiQuestCardList = cardToRemove;
		currentQuestInfo = array;

		String logString = "";
		for (String s : cardToRemove) {
			logString += s + ", ";
		}
		// System.out.println("QC 180");
		// System.out.println(aiQuestCardList);
		// System.out.println(cardToRemove);
		for (int i = 0; i < jsonArr.length; i++) {
			// System.out.println(jsonArr[i]);
		}
		List<String> JSONlist = new ArrayList<String>(jsonArr.length);
		for (String s : jsonArr) {
			JSONlist.add(s);
		}
		// System.out.println(JSONlist);
		logger.info("Player {} setup {} quest with {}", gameEngine.getActivePlayer().getName(),
				gameEngine.storyDeck.faceUp.getName(), logString);
		String jsonOutput = "questSetupCards" + JSONlist;
		sendToAllSessions(gameEngine, jsonOutput);

		for (Card c : aiQuestcards) {
			if (c instanceof FoeCard)
				QuestFoes.add((FoeCard) c);
			if (c instanceof TestCard) {
				QuestTests.add((TestCard) c);
				TestCard t = (TestCard) c;

				originalBid = t.getMinBid();
			}
		}

		calculateFoeBattlePoints();
	}

	// initiate foes for current quest
	public void initiateQuest(JsonObject json) {
		JsonElement quest_cards = json.get("questSetupCards");
		Type listType = new TypeToken<List<String>>() {
		}.getType();
		List<String> questCardList = new Gson().fromJson(quest_cards, listType);
		// System.out.println(questCardList);
		for (int k = 0; k < questCardList.size(); k++) {
			// System.out.println("in loop");
			Card tempCard = getCardFromName(questCardList.get(k));
			// System.out.println(tempCard.getName());
			if (tempCard instanceof TestCard) {
				// System.out.println("adding test");
				QuestTests.add((TestCard) tempCard);
				TestCard t = (TestCard) tempCard;
				originalBid = t.getMinBid();
			}
			if (tempCard instanceof FoeCard) {
				QuestFoes.add((FoeCard) tempCard);
			}
			if (tempCard instanceof WeaponCard) {
				QuestFoes.get(QuestFoes.size() - 1).getWeapons().add((WeaponCard) tempCard);
			}

		}

		// for (FoeCard f : QuestFoes) {
		// System.out.println(f.getName());
		// for (WeaponCard w : f.getWeapons()) {
		// System.out.println(w.getName());
		// }
		// }
		calculateFoeBattlePoints();
	}

	public void calculateNumBids(List<String> bids) {
		Player currentPlayer = gameEngine.current_quest.getCurrentParticipant();
		int tempBids = bids.size();
		currentMinBid = tempBids;
		sendToAllSessions(gameEngine, "currentPlayerBids" + tempBids + ";" + currentPlayer.getName());
	}

	public int calculatePlayerPoints(String name) {
		Player currentPlayer = gameEngine.getPlayerFromName(name);
		//// System.out.println("LINE 258 QC CALCULATE PLAYER POINTS
		//// ---------------------");
		// for (WeaponCard w : currentPlayer.getWeapons()) {
		// System.out.println(w.getName());
		// }
		// for (AllyCard a : currentPlayer.getAllies()) {
		// System.out.println(a.getName());
		// }
		int tempPts = currentPlayer.getBattlePoints();
		// System.out.println(tempPts);
		ArrayList<String> removeWeapons = new ArrayList<String>();
		// String cardNames = "";
		if (currentPlayer.getAmourCard() != null) {
			tempPts += 10;
			// cardNames += currentPlayer.getAmourCard().getName() + "#";
			// System.out.println("in amour");
		}
		for (WeaponCard w : currentPlayer.getWeapons()) {
			// System.out.println("in weps");
			tempPts += w.getBattlePoints();
			// cardNames += w.getName() + "#";
			removeWeapons.add(w.getName());
		}
		for (AllyCard a : currentPlayer.getAllies()) {
			// System.out.println("in ally");
			tempPts += a.getBattlePoints();
			// cardNames += a.getName() + "#";
			// ally bonus points
		}
		// System.out.println("LEAVING CALC PLAYER PTS " + tempPts);
		return tempPts;
	}

	public void calculateFoeBattlePoints() {
		int tempPts;
		foePoints = new PointArray(currentQuest.getStages() - 1);
		for (FoeCard f : QuestFoes) {
			tempPts = 0;
			if (currentQuest.getFoe().equals(f.getName()) || currentQuest.getFoe2().equals(f.getName())) {
				tempPts = f.getBonusBattlePoints();
			} else {
				tempPts = f.getBattlePoints();
			}
			for (WeaponCard w : f.getWeapons()) {
				tempPts += w.getBattlePoints();
			}
			foePoints.names.add(f.getName());
			foePoints.points.add(tempPts);
		}
		String temp = foePoints.toString();
		sendToAllSessions(gameEngine, "FoeInfo" + temp);
		PointArray testPoints = new PointArray(currentQuest.getStages() - 1);
		for (TestCard t : QuestTests) {
			testPoints.names.add(t.getName());
			testPoints.points.add(t.getMinBid());
		}
		String temp2 = testPoints.toString();
		sendToAllSessions(gameEngine, "TestInfo" + temp2);
	}

	public AdventureCard getCardFromName(String name) {
		AdventureCard card = null;
		CardList cardList = new CardList();
		for (Card a : cardList.adventureTypes) {
			if (a.getName().equals(name)) {
				card = (AdventureCard) a;
				break;
			}
		}
		return card;

	}

	public Player getCurrentParticipant() {
		if (participants.size() == 1)
			return participants.get(0);
		return participants.get(participantTurns % participants.size());
	}

	public void removeParticipant(String name) {
		for (int i = 0; i < participants.size(); i++) {
			if (participants.get(i).getName().equals(name)) {
				participants.remove(i);
			}
		}
	}

	public Player getNextParticipant() {
		if (participants.size() == 1)
			return participants.get(0);
		return participants.get((participantTurns + 1) % participants.size());
	}

	public void incTurn() {
		this.participantTurns++;
	}

	public void setupQuest() throws IOException {
		logger.info("{} is setting up stages for {} quest", this.sponsor.getName(), this.currentQuest.getName());
		sendToActivePlayer(gameEngine, "questSetupInProgress" + this.currentQuest.getStages());
		logger.info("Informing players that Player {} is sponsor of {} quest", this.sponsor.getName(),
				gameEngine.storyDeck.faceUp.getName());
		QuestCard q = (QuestCard) gameEngine.storyDeck.faceUp;
		logger.info("Player {} will now choose cards for {} stages of quest", this.sponsor.getName(), q.getStages());
		sendToAllPlayersExcept(gameEngine, this.sponsor, "questIsBeingSetup" + this.sponsor.getName());
	}

	public ArrayList<Player> getParticipants() {
		return this.participants;
	}

	public void pickupBeforeStage() throws IOException {
		gameEngine.current_quest.getCurrentParticipant().getHand().add(gameEngine.adventureDeck.flipCard());
		String newCardLink = gameEngine.adventureDeck.faceUp.getStringFile();
		logger.info("Player {} getting new card {}", gameEngine.current_quest.getCurrentParticipant().getName(),
				gameEngine.adventureDeck.faceUp.getName());
		gameEngine.current_quest.getCurrentParticipant().session
				.sendMessage(new TextMessage("pickupBeforeStage" + newCardLink));
		logger.info("Updating GUI stats for all players");
		String update = gameEngine.getPlayerStats();
		sendToAllSessions(gameEngine, "updateStats" + update);
	}

	public void getNewParticipants(JsonObject jsonObject, WebSocketSession session) throws IOException {
		JsonElement participate_quest_answer = jsonObject.get("participate_quest");
		JsonElement name = jsonObject.get("name");

		if (participate_quest_answer.getAsBoolean()) {
			acceptParticipation(name.getAsString());
			return;

		} else {
			denyParticipation(name.getAsString());
		}
	}

	public void acceptParticipation(String name) throws IOException {
		logger.info("Player {} accepted to participate in {} quest sponsored by {}", name,
				gameEngine.storyDeck.faceUp.getName(), gameEngine.current_quest.sponsor.getName());

		gameEngine.current_quest.participants.add(gameEngine.getActivePlayer());
		logger.info("Informing other players that {} has accepted to participate in {} quest",
				gameEngine.getActivePlayer().getName(), gameEngine.storyDeck.faceUp.getName());
		sendToAllSessionsExceptCurrent(gameEngine, gameEngine.getActivePlayer().session,
				"AcceptedToParticipate" + gameEngine.getActivePlayer().getName());

		gameEngine.getActivePlayer().getHand().add(gameEngine.adventureDeck.flipCard());
		String newCardLink = gameEngine.adventureDeck.faceUp.getStringFile();
		logger.info("Player {} getting new card {}", gameEngine.getActivePlayer().getName(),
				gameEngine.adventureDeck.faceUp.getName());
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("pickupBeforeStage" + newCardLink));
		logger.info("Updating GUI stats for all players");
		gameEngine.updateStats();

		gameEngine.incTurn();
		if (gameEngine.getActivePlayer().equals(gameEngine.current_quest.sponsor)) {
			for (int i = 0; i < currentQuestInfo.length; i++) {
				// System.out.println(currentQuestInfo[i]);
			}
			// System.out.println("current stage:" + currentStage);
			if (currentQuestInfo[currentStage - 1].contains("Test")) {
				// is test - one at a time
				gameEngine.getCurrentParticipant().session.sendMessage(new TextMessage("ChooseEquipment"));
			} else { // is battle choose concurrently
				sendToAllParticipants(gameEngine, "ChooseEquipment");
			}
			String logString = "";
			for (Player p : gameEngine.current_quest.participants) {
				logString += p.getName() + ", ";
			}
			logger.info("{} quest setup by {} is commencing, participant(s): {} are choosing equipment CONCURRENTLY",
					gameEngine.storyDeck.faceUp.getName(), gameEngine.current_quest.sponsor.getName(), logString);
			sendToSponsor(gameEngine, "ParticipantsChoosing");
			if (gameEngine.current_quest.participants.size() < 3) {
				sendToNonParticipants(gameEngine, "wait");
			}
			return;
		}
		logger.info("Asking Player {} to participate in quest", gameEngine.getActivePlayer().getName());
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("AskToParticipate"));

	}

	public void denyParticipation(String name) throws IOException {
		logger.info("Player {} denied to participate in {} quest sponsored by {}", name,
				gameEngine.storyDeck.faceUp.getName(), gameEngine.current_quest.sponsor.getName());
		logger.info("Informing other players that {} has declined to participate in {} quest",
				gameEngine.getActivePlayer().getName(), gameEngine.storyDeck.faceUp.getName());
		sendToAllSessionsExceptCurrent(gameEngine, gameEngine.getActivePlayer().session,
				"DeclinedToParticipate" + gameEngine.getActivePlayer().getName());
		gameEngine.incTurn();
		if (gameEngine.getActivePlayer().equals(gameEngine.current_quest.sponsor)) {
			if (gameEngine.current_quest.participants.size() == 0) {
				logger.info("No players participated, sponsor {} is replacing cards used to setup {} quest",
						gameEngine.getActivePlayer().getName(), gameEngine.storyDeck.faceUp.getName());
				sendToAllSessions(gameEngine, "NoParticipants");
				String temp = "";
				String cardNames = "";
				for (int i = gameEngine.getActivePlayer().getHandSize(); i < 12
						+ gameEngine.current_quest.currentQuest.getStages(); i++) {
					AdventureCard newCard = gameEngine.adventureDeck.flipCard();
					temp += newCard.getStringFile() + ";";
					cardNames += newCard.getName() + ", ";
					gameEngine.getActivePlayer().getHand().add(newCard);

				}
				logger.info("Player {} is receiving {} for sponsoring", gameEngine.getActivePlayer().getName(),
						cardNames);
				logger.info("Player {} has {} cards, will be prompted to discard",
						gameEngine.getActivePlayer().getName(), gameEngine.getActivePlayer().getHandSize());
				gameEngine.getActivePlayer().session.sendMessage(new TextMessage("SponsorPickup" + temp));

				if (gameEngine.getNextPlayer().isAI())
					sendToNextPlayer(gameEngine, "undisableFlip");

				return;
			} else {

				if (currentQuestInfo[currentStage - 1].contains("Test")) {
					// is test - one at a time
					gameEngine.getCurrentParticipant().session.sendMessage(new TextMessage("ChooseEquipment"));
				} else { // is battle choose concurrently
					sendToAllParticipants(gameEngine, "ChooseEquipment");
				}
				String logString = "";
				for (Player p : gameEngine.current_quest.participants) {
					logString += p.getName() + ", ";
				}
				logger.info("{} quest setup by {} is commencing with participant(s): {} choosing weapons CONCURRENTLY",
						gameEngine.storyDeck.faceUp.getName(), gameEngine.current_quest.sponsor.getName(), logString);
				sendToSponsor(gameEngine, "ParticipantsChoosing");
				if (gameEngine.current_quest.participants.size() < 3) {
					sendToNonParticipants(gameEngine, "wait");
				}
				return;
			}
		}
		logger.info("Asking Player {} to participate in quest", gameEngine.getActivePlayer().getName());
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("AskToParticipate"));

	}

	public void sponsorPickup() throws IOException {

		String temp = "";
		String tempNames = "";
		int cardTracker = 0;
		for (int i = gameEngine.getActivePlayer().getHandSize(); i < 12
				+ gameEngine.current_quest.currentQuest.getStages(); i++) {
			cardTracker++;
			AdventureCard newCard = gameEngine.adventureDeck.flipCard();
			temp += newCard.getStringFile() + ";";
			tempNames += newCard.getName() + ", ";
			gameEngine.getActivePlayer().getHand().add(newCard);
		}
		logger.info("Player {} who sponsored {} quest is receiving {} card ({}) due to sponsoring quest",
				gameEngine.getActivePlayer().getName(), gameEngine.storyDeck.faceUp.getName(), cardTracker, tempNames);
		logger.info("Player {} has {} cards, will be prompted to discard", gameEngine.getActivePlayer().getName(),
				gameEngine.getActivePlayer().getHandSize());
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("SponsorPickup" + temp));
	}

	public void parseQuestInfo(JsonObject jsonObject) throws IOException {
		String quest_setup_cards = jsonObject.get("questSetupCards").toString();
		initiateQuest(jsonObject);
		String[] sponsorDiscard = quest_setup_cards.split(",");
		sponsorDiscard[0] = sponsorDiscard[0].replace("[", "");
		sponsorDiscard[sponsorDiscard.length - 1] = sponsorDiscard[sponsorDiscard.length - 1].replace("]", "");
		currentQuestInfo = sponsorDiscard;
		for (String s : sponsorDiscard) {
			// System.out.println(s);
			gameEngine.current_quest.sponsor.discard(s);
		}
		gameEngine.updateStats();
		gameEngine.incTurn();
		logger.info("Asking Player {} to participate in quest", gameEngine.getActivePlayer().getName());
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("AskToParticipate"));
	}

	public void parseEquipmentInfo(JsonObject jsonObject) throws IOException {
		// String playerName = jsonObject.get("name").getAsString();
		// int currStage = jsonObject.get("stages").getAsInt();
		boolean isTest = jsonObject.get("isTest").getAsBoolean();

		// System.out.println(playerName);
		// System.out.println(currStage);
		// System.out.println(isTest);

		if (isTest) {
			gameEngine.current_quest.placeBids(jsonObject);
		} else {
			gameEngine.current_quest.equipPlayer(jsonObject);
		}
	}

	public void goToNextTurn(JsonObject jsonObject) throws IOException {
		System.out.println(jsonObject.toString());
		if (jsonObject.has("battleOutcome")) {
			parseBattleResult(jsonObject);
		}

	}

	private void parseBattleResult(JsonObject jsonObject) throws IOException {
		boolean battleResult = jsonObject.get("battleOutcome").getAsBoolean();
		if (battleResult) {
			System.out.println(gameEngine.current_quest.getCurrentParticipant().getName());
			if (participants.size() == 1) {
				System.out.println("go to next stage");
				sendToAllSessions(gameEngine, "incStage");
				gameEngine.current_quest.currentStage++;
				pickupBeforeStage();
				gameEngine.current_quest.getCurrentParticipant().session
						.sendMessage(new TextMessage("ChooseEquipment"));
				sendToSponsor(gameEngine, "ParticipantsChoosing");
			} else {
				System.out.println("same stage, next player");
				gameEngine.current_quest.incTurn();
				logger.info("Asking Player {} to participate in quest", gameEngine.getActivePlayer().getName());
				gameEngine.current_quest.getCurrentParticipant().session
						.sendMessage(new TextMessage("AskToParticipate"));
			}
		} else {
			System.out.println(gameEngine.current_quest.getCurrentParticipant().getName());
			gameEngine.current_quest.participants.remove(gameEngine.current_quest.getCurrentParticipant());
			if (gameEngine.current_quest.participants.isEmpty()) {
				System.out.println("no participants");
			}
			if (gameEngine.current_quest.participants.size() == 1) {
				if (gameEngine.current_quest.firstQuestPlayer
						.equals(gameEngine.current_quest.getCurrentParticipant())) {
					System.out.println("is first player.. inc stage");
				} else {
					System.out.println("is not first first player");
				}
			} else {

			}

		}

	}

	@SuppressWarnings("unchecked")
	public void calculateStageOutcome(String playerPoints, JsonArray questInformation) throws IOException {
		// System.out.println("LINE 571 QC PLAYER POINTS");
		// System.out.println(playerPoints);
		int currentStage = gameEngine.current_quest.currentStage - 1;
		ArrayList<String[]> playerPointsArr = new ArrayList<String[]>();
		FoeCard currentFoe = null;
		TestCard currentTest = null;

		String[] temp = playerPoints.split(";");
		for (int i = 0; i < temp.length; i++) {
			String[] s = temp[i].split("#");
			playerPointsArr.add(s);
		}
		for (int i = 0; i < playerPointsArr.size(); i++) {
			// System.out.println(playerPointsArr.get(i)[0] + " " +
			// playerPointsArr.get(i)[1]);
		}

		for (int i = 0; i < questInformation.size(); i++) {
			// System.out.println(questInformation.get(i));
		}

		// String foePoints = foePoints.toString();
		// System.out.println(foePointsd);

		JsonObject quest_cards = (JsonObject) questInformation.get(0);
		JsonElement x = quest_cards.get("questSetupCards");
		Type listType = new TypeToken<List<String>>() {
		}.getType();
		questCardList = new Gson().fromJson(x, listType);
		if (gameEngine.current_quest.sponsor.isAI())
			questCardList = aiQuestCardList;
		// System.out.println("for s");
		// for (String s : questCardList) {
		// System.out.println(s);
		// }
		// System.out.println("for i");
		for (int i = 0; i < questCardList.size(); i++) {
			// System.out.println(questCardList.get(i));
			Card tempC = getCardFromName(questCardList.get(i));
			// System.out.println(tempC.getName());
			if (tempC instanceof WeaponCard) {
				// System.out.println("true");
				questCardList.remove(questCardList.get(i));
			}
		}
		// System.out.println(questCardList);
		// System.out.println(questCardList.size());
		for (int i = 0; i < questCardList.size(); i++) {
			Card currentCard = getCardFromName(questCardList.get(i));
			// System.out.println(currentCard.getName());
			if (currentCard instanceof WeaponCard) {
				// System.out.println("removing this card");
				// System.out.println(currentCard.getName() + ";");
				questCardList.remove(currentCard.getName());
				continue;
			}
		}
		// System.out.println("removing should be");
		// for (String s : questCardList) {
		// System.out.println(s);
		// }
		// System.out.println(currentStage);
		// System.out.println(gameEngine.current_quest.currentStage - 1);
		Card tempCard = getCardFromName(questCardList.get(gameEngine.current_quest.currentStage - 1));
		if (tempCard instanceof FoeCard) {
			logger.info("{} quest stage {} is a foe battle", gameEngine.current_quest.currentQuest.getName(),
					currentStage + 1);
			currentFoe = (FoeCard) getCardFromName(questCardList.get(gameEngine.current_quest.currentStage - 1));
			for (int i = 0; i < foePoints.points.size(); i++) {

			}
			int currentFoePoints = 0;
			for (int i = 0; i < foePoints.names.size(); i++) {
				if (currentFoe.getName().equals(foePoints.names.get(i))) {
					currentFoePoints = foePoints.points.get(i);
				}
			}
			logger.info("Participant battles commencing against foe {} with {} battle points", currentFoe.getName(),
					currentFoePoints);
			for (int i = 0; i < playerPointsArr.size(); i++) {
				logger.info(
						"Battle against participant {} with {} battle points is starting - versus {} with {} points",
						playerPointsArr.get(i)[0], playerPointsArr.get(i)[1], currentFoe.getName(), currentFoePoints);
				if (Integer.parseInt(playerPointsArr.get(i)[1]) >= currentFoePoints) {
					shieldSent = true;
					logger.info("Player {} has WON battle", playerPointsArr.get(i)[0]);
					if (gameEngine.current_quest.currentStage == gameEngine.current_quest.currentQuest.getStages()) {
						Player shieldGetter = gameEngine.getPlayerFromName(playerPointsArr.get(i)[0]);
						shieldGetter.getWeapons().clear();
						shieldGetter.session.sendMessage(new TextMessage("Getting "
								+ gameEngine.current_quest.currentQuest.getStages() + " shields for winning quest"));
						shieldGetter.giveShields(gameEngine.current_quest.currentQuest.getStages());
						logger.info("Giving {} shields to {}", gameEngine.current_quest.currentQuest.getStages(),
								shieldGetter.getName());
						if (Game.KingsRecognition) {
							logger.info("Event card King's Recognition is in play, player {} gets 2 extra shields",
									shieldGetter.getName());
							shieldGetter.giveShields(2);
							Game.KingsRecognition = false;
						}
						sponsorPickup();
						return;
					}
				} else {
					logger.info("Player {} has LOST battle", playerPointsArr.get(i)[0]);
					Player toRemove = gameEngine.getPlayerFromName(playerPointsArr.get(i)[0]);
					toRemove.getWeapons().clear();
					logger.info("Informing players of player {} loss in battle", toRemove.getName());
					sendToAllSessions(gameEngine, "LostBattle" + toRemove.getName());
					gameEngine.current_quest.participants.remove(toRemove);
					logger.info("Player {} has been removed from quest", toRemove.getName());
				}
			}

			logger.info("Stage {} is over", currentStage + 1);
			if (currentTest == null) {
				// System.out.println("this was not a test");
				for (Player p : gameEngine.current_quest.participants) {
					p.getWeapons().clear();
				}
			}
			logger.info("Updating GUI stats for all players");
			gameEngine.updateStats();
			for (Player p : gameEngine.players) {
				AdventureCard amour = p.getAmourCard();
				if (amour == null) {
				} else {

					p.unequipAmour();
					logger.info("Player {} is unequipping the amour used in last quest", p.getName());
				}
			}
			if (gameEngine.current_quest.participants.isEmpty()) {
				Losing();
				return;
			}
			if (gameEngine.current_quest.currentStage == gameEngine.current_quest.currentQuest.getStages()) {
				System.out.println("HERE 751");
				Winning();
				return;
			}
			sendToAllSessions(gameEngine, "incStage");
			gameEngine.current_quest.currentStage++;
			int test = currentStage + 1;
			// System.out.println(gameEngine.current_quest.currentStage);
			// System.out.println("in quest: " + gameEngine.current_quest.currentStage);
			// System.out.println("current stage: " + currentStage);
			// System.out.println("num participants: " +
			// gameEngine.current_quest.participants.size());

			ArrayList<ArrayList<String>> ordered = new ArrayList<ArrayList<String>>();
			ArrayList<String> tempArr = new ArrayList<String>();

			tempArr.add(currentQuestInfo[0].replaceAll("\"", ""));
			for (int i = 1; i < currentQuestInfo.length; i++) {
				String cardName = currentQuestInfo[i];
				cardName = cardName.replaceAll("\"", "");
				CardList cardFinder = new CardList();
				Card card = cardFinder.getCardFromName(cardName);
				// System.out.println(card.getName());
				if (card instanceof FoeCard || card instanceof TestCard) {
					// System.out.println("new row");
					ordered.add((ArrayList<String>) tempArr.clone());
					// System.out.println(ordered);
					// System.out.println(tempArr);
					tempArr.clear();
					// System.out.println(tempArr);
				}

				tempArr.add(cardName);
				// System.out.println(tempArr);
				if (i == currentQuestInfo.length - 1)
					ordered.add((ArrayList<String>) tempArr.clone());
			}
			// System.out.println(ordered);

			if (ordered.get(test).get(0).contains("Test")) {
				// is test - one at a time
				gameEngine.current_quest.pickupBeforeStage();
				gameEngine.getCurrentParticipant().session.sendMessage(new TextMessage("ChooseEquipment"));
			} else { // is battle choose concurrently
				logger.info("All participants are choosing equipment for quest CONCURRENTLY");
				String logStringParticipants = "";
				for (Player p : gameEngine.current_quest.participants) {
					p.getHand().add(gameEngine.adventureDeck.flipCard());
					String newCardLink = gameEngine.adventureDeck.faceUp.getStringFile();
					logger.info("Player {} getting new card {}", p.getName(),
							gameEngine.adventureDeck.faceUp.getName());
					p.session.sendMessage(new TextMessage("pickupBeforeStage" + newCardLink));
					logStringParticipants += p.getName() + " has " + p.getHandSize() + " cards in hand";
					if (p.getHandSize() > 12)
						logStringParticipants += ", will be prompted to discard ";

				}
				logger.info("Player(s): {}", logStringParticipants);
				logger.info("Updating GUI stats for all players");
				gameEngine.updateStats();
				sendToAllParticipants(gameEngine, "ChooseEquipment");
			}
		} else {
			logger.info("{} quest upcoming stage is a test", gameEngine.current_quest.currentQuest.getName());
			currentTest = (TestCard) getCardFromName(questCardList.get(currentStage));
			System.out.println("in test");
			gameEngine.current_quest.pickupBeforeStage();
			gameEngine.current_quest.getCurrentParticipant().session.sendMessage(new TextMessage("ChooseEquipment"));
		}
	}

}
