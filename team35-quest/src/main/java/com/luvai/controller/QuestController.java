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

	// placing bids for test
	public void placeBids(JsonObject json) throws IOException {
		JsonElement player_bids = json.get("equipment_info");
		Type listType = new TypeToken<List<String>>() {
		}.getType();
		List<String> bidList = new Gson().fromJson(player_bids, listType);
		toDiscardAfterTest = new ArrayList<String>(bidList);
		String testBidLogString = "";
		for (String s : toDiscardAfterTest) {
			testBidLogString += s + ", ";
		}
		if (testBidLogString == "") {
			logger.info("Player {} dropped out of test in quest {}", json.get("name").getAsString(),
					gameEngine.storyDeck.faceUp.getName());
		} else {
			logger.info("Player {} bid {} for test in quest {}", json.get("name").getAsString(), testBidLogString,
					gameEngine.storyDeck.faceUp.getName());
			sendToAllSessions(gameEngine, "updateMinBid" + gameEngine.current_quest.currentMinBid);

		}
		calculateNumBids(bidList);
		ArrayList<String> tempDiscards = new ArrayList<String>(bidList);
		calculateTestDiscards(tempDiscards);
	}

	// calculate bonus bids for test then discard if winner
	public void calculateTestDiscards(ArrayList<String> discardList) {
		int bonusBids = 0;
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
		for (int i = 0; i < bonusBids; i++) {
			String temp = toDiscardAfterTest.remove(0);
			gameEngine.current_quest.getCurrentParticipant().replaceBonusBidsList.add(temp);
		}
		gameEngine.current_quest.getCurrentParticipant().testDiscardList = toDiscardAfterTest;
	}

	// adjust player for points with chosen equipment for foe battle
	public void equipPlayer(JsonObject json) {
		JsonElement player_equipment = json.get("equipment_info");
		String name = json.get("name").getAsString();
		;
		Player currentPlayer = gameEngine.getPlayerFromName(name);
		Type listType = new TypeToken<List<String>>() {
		}.getType();
		List<String> equipmentList = new Gson().fromJson(player_equipment, listType);
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
	}

	// initiates quest for ai player
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

		String[] jsonArr = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			jsonArr[i] = "\"" + array[i] + "\"";
		}

		aiQuestCardList = cardToRemove;
		currentQuestInfo = array;

		String logString = "";
		for (String s : cardToRemove) {
			logString += s + ", ";
		}

		List<String> JSONlist = new ArrayList<String>(jsonArr.length);
		for (String s : jsonArr) {
			JSONlist.add(s);
		}

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

	// initiate current quest with sponsor choices
	public void initiateQuest(JsonObject json) {
		JsonElement quest_cards = json.get("questSetupCards");
		Type listType = new TypeToken<List<String>>() {
		}.getType();
		List<String> questCardList = new Gson().fromJson(quest_cards, listType);
		for (int k = 0; k < questCardList.size(); k++) {
			Card tempCard = getCardFromName(questCardList.get(k));
			if (tempCard instanceof TestCard) {
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
		calculateFoeBattlePoints();
	}

	// gets number of bids from player choices while in test
	public void calculateNumBids(List<String> bids) {
		Player currentPlayer = gameEngine.current_quest.getCurrentParticipant();
		int tempBids = bids.size();
		currentMinBid = tempBids;
		sendToAllSessions(gameEngine, "currentPlayerBids" + tempBids + ";" + currentPlayer.getName());
	}

	// calculates point for foe battles
	public int calculatePlayerPoints(String name) {
		Player currentPlayer = gameEngine.getPlayerFromName(name);
		int tempPts = currentPlayer.getBattlePoints();
		ArrayList<String> removeWeapons = new ArrayList<String>();
		if (currentPlayer.getAmourCard() != null) {
			tempPts += 10;
		}
		for (WeaponCard w : currentPlayer.getWeapons()) {
			tempPts += w.getBattlePoints();
			removeWeapons.add(w.getName());
		}
		for (AllyCard a : currentPlayer.getAllies()) {
			tempPts += a.getBattlePoints();

		}
		return tempPts;
	}

	// calculate foe points for battle
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

	// returns card object from string name
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

	// get current participant in quest
	public Player getCurrentParticipant() {
		if (participants.size() == 1)
			return participants.get(0);
		return participants.get(participantTurns % participants.size());
	}

	// remove participant from quest if loss
	public void removeParticipant(String name) {
		for (int i = 0; i < participants.size(); i++) {
			if (participants.get(i).getName().equals(name)) {
				participants.remove(i);
			}
		}
	}

	// get next participant in turn
	public Player getNextParticipant() {
		if (participants.size() == 1)
			return participants.get(0);
		return participants.get((participantTurns + 1) % participants.size());
	}

	// increase turns for quest
	public void incTurn() {
		this.participantTurns++;
	}

	// initializes quest
	public void setupQuest() throws IOException {
		logger.info("{} is setting up stages for {} quest", this.sponsor.getName(), this.currentQuest.getName());
		sendToActivePlayer(gameEngine, "questSetupInProgress" + this.currentQuest.getStages());
		logger.info("Informing players that Player {} is sponsor of {} quest", this.sponsor.getName(),
				gameEngine.storyDeck.faceUp.getName());
		QuestCard q = (QuestCard) gameEngine.storyDeck.faceUp;
		logger.info("Player {} will now choose cards for {} stages of quest", this.sponsor.getName(), q.getStages());
		sendToAllPlayersExcept(gameEngine, this.sponsor, "questIsBeingSetup" + this.sponsor.getName());
	}

	// returns all current participants playing quest
	public ArrayList<Player> getParticipants() {
		return this.participants;
	}

	// before each stage of quest participant picks up
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

	// intitialize players who accept into participants
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

	// if player accepts to participate quest
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

	// if player declines to play quest
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

	// sponsor picks up cards used to sponsor quest + extra
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

	// gets quest information from sponsor to setup quest with choices made
	public void parseQuestInfo(JsonObject jsonObject) throws IOException {
		String quest_setup_cards = jsonObject.get("questSetupCards").toString();
		initiateQuest(jsonObject);
		String[] sponsorDiscard = quest_setup_cards.split(",");
		sponsorDiscard[0] = sponsorDiscard[0].replace("[", "");
		sponsorDiscard[sponsorDiscard.length - 1] = sponsorDiscard[sponsorDiscard.length - 1].replace("]", "");
		currentQuestInfo = sponsorDiscard;
		for (String s : sponsorDiscard) {
			gameEngine.current_quest.sponsor.discard(s);
		}
		gameEngine.updateStats();
		gameEngine.incTurn();
		logger.info("Asking Player {} to participate in quest", gameEngine.getActivePlayer().getName());
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("AskToParticipate"));
	}

	// initialize player choices for quest - either battle equipment or test bids
	public void parseEquipmentInfo(JsonObject jsonObject) throws IOException {

		boolean isTest = jsonObject.get("isTest").getAsBoolean();
		if (isTest) {
			gameEngine.current_quest.placeBids(jsonObject);
		} else {
			gameEngine.current_quest.equipPlayer(jsonObject);
		}
	}

	// executes game logic for quest outcome after all participants have chosen
	// equipment
	@SuppressWarnings("unchecked")
	public void calculateStageOutcome(String playerPoints, JsonArray questInformation) throws IOException {
		int currentStage = gameEngine.current_quest.currentStage - 1;
		ArrayList<String[]> playerPointsArr = new ArrayList<String[]>();
		FoeCard currentFoe = null;
		TestCard currentTest = null;

		String[] temp = playerPoints.split(";");
		for (int i = 0; i < temp.length; i++) {
			String[] s = temp[i].split("#");
			playerPointsArr.add(s);
		}
		JsonObject quest_cards = (JsonObject) questInformation.get(0);
		JsonElement x = quest_cards.get("questSetupCards");
		Type listType = new TypeToken<List<String>>() {
		}.getType();
		questCardList = new Gson().fromJson(x, listType);
		if (gameEngine.current_quest.sponsor.isAI())
			questCardList = aiQuestCardList;

		for (int i = 0; i < questCardList.size(); i++) {
			Card tempC = getCardFromName(questCardList.get(i));
			if (tempC instanceof WeaponCard) {
				questCardList.remove(questCardList.get(i));
			}
		}
		for (int i = 0; i < questCardList.size(); i++) {
			Card currentCard = getCardFromName(questCardList.get(i));
			if (currentCard instanceof WeaponCard) {
				questCardList.remove(currentCard.getName());
				continue;
			}
		}
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
						logger.info("Clearing all weapons and amour");
						for (Player p : gameEngine.players) {
							p.getWeapons().clear();
							p.setAmourCard(null);
						}
						sponsorPickup();
						gameEngine.updateStats();
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
				logger.info("Clearing all weapons");
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
			ArrayList<ArrayList<String>> ordered = new ArrayList<ArrayList<String>>();
			ArrayList<String> tempArr = new ArrayList<String>();

			tempArr.add(currentQuestInfo[0].replaceAll("\"", ""));
			for (int i = 1; i < currentQuestInfo.length; i++) {
				String cardName = currentQuestInfo[i];
				cardName = cardName.replaceAll("\"", "");
				CardList cardFinder = new CardList();
				Card card = cardFinder.getCardFromName(cardName);
				if (card instanceof FoeCard || card instanceof TestCard) {
					ordered.add((ArrayList<String>) tempArr.clone());
					tempArr.clear();
				}

				tempArr.add(cardName);
				if (i == currentQuestInfo.length - 1)
					ordered.add((ArrayList<String>) tempArr.clone());
			}

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
