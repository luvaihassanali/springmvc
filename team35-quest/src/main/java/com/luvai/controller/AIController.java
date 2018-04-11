package com.luvai.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.luvai.model.Player;
import com.luvai.model.AdventureCards.AdventureCard;
import com.luvai.model.StoryCards.QuestCard;

public class AIController extends SocketHandler {
	private static final Logger logger = LogManager.getLogger(AIController.class);

	public AIController() {
	}

	// parses commands from ai to execute certain game moves
	public void receiveAICommand(JsonObject jsonObject, WebSocketSession session) throws IOException {

		if (jsonObject.get("AICommand").getAsString().equals("AskToParticipateQuest"))
			AIQuestParticipation(jsonObject);

		if (jsonObject.get("AICommand").getAsString().equals("DiscardChoice")) {
			AIDiscard(jsonObject);
		}

		if (jsonObject.get("AICommand").getAsString().equals("SponsorQuest")) {
			AIQuestSponsor(jsonObject);
		}

		if (jsonObject.get("AICommand").getAsString().equals("nextBid")) {
			AIMakeBid(jsonObject);
		}

		if (jsonObject.get("AICommand").getAsString().equals("chooseEquipment")) {
			AIChooseEquipment(jsonObject);
		}

		if (jsonObject.get("AICommand").getAsString().equals("AskTournament")) {
			AITournamentParticipation(jsonObject, session);
		}

		if (jsonObject.get("AICommand").getAsString().equals("chooseEquipmentTournie")) {
			AIChooseEquipmentTournie(jsonObject);
		}

	}

	// gets tournament equipment
	private void AIChooseEquipmentTournie(JsonObject jsonObject) {
		Player currentPlayer = gameEngine.getPlayerFromName(jsonObject.get("name").getAsString());
		currentPlayer.getAI().chooseEquipmentTournie(jsonObject, currentPlayer);
	}

	// gets equipment for foe battles
	private void AIChooseEquipment(JsonObject jsonObject) {
		Player currentPlayer = gameEngine.getPlayerFromName(jsonObject.get("name").getAsString());
		currentPlayer.getAI().chooseEquipment(jsonObject, currentPlayer);

	}

	// ai bids for test
	private void AIMakeBid(JsonObject jsonObject) throws IOException {
		Player currentPlayer = gameEngine.getPlayerFromName(jsonObject.get("name").getAsString());
		ArrayList<AdventureCard> AIBids = currentPlayer.getAI().nextBid(jsonObject);
		String bidList = "";
		for (AdventureCard a : AIBids) {
			bidList += a.getName() + ";";
		}
		int bidToCompare = gameEngine.current_quest.currentMinBid;
		if (bidToCompare == 0)
			bidToCompare = gameEngine.current_quest.originalBid;
		if (AIBids.size() < bidToCompare + 1) {
			gameEngine.getCurrentParticipant().session.sendMessage(new TextMessage("AIDropOut"));

			gameEngine.current_quest.getCurrentParticipant().getHand()
					.remove(gameEngine.current_quest.getCurrentParticipant().getHand().size() - 1);

			if (gameEngine.current_quest.participants.size() == 1) {
				gameEngine.current_quest.participants.remove(gameEngine.current_quest.getCurrentParticipant());
				Losing();
			} else {
				gameEngine.current_quest.participants.remove(gameEngine.current_quest.getCurrentParticipant());
				if (gameEngine.current_quest.participants.size() == 1) {
					if (gameEngine.current_quest.getCurrentParticipant().testDiscardList.size() != 0) {
						sendToAllSessions(gameEngine, "incStage");
						gameEngine.current_quest.currentStage++;
					}

				}
				gameEngine.current_quest.getCurrentParticipant().session
						.sendMessage(new TextMessage("ChooseEquipment"));
			}
			return;
		}

		gameEngine.getCurrentParticipant().session.sendMessage(new TextMessage("AIBidList" + bidList));

	}

	// get answer from ai to sponsor quest
	public void AIQuestSponsor(JsonObject jsonObject) throws IOException {
		Player currentPlayer = gameEngine.getPlayerFromName(jsonObject.get("name").getAsString());
		boolean sponsorAnswer = currentPlayer.getAI().doISponsorQuest();
		if (sponsorAnswer) {
			gameEngine.newQuest(gameEngine, currentPlayer, (QuestCard) gameEngine.storyDeck.faceUp);

			currentPlayer.getAI().setupQuest();
			gameEngine.incTurn();
			gameEngine.getActivePlayer().session.sendMessage(new TextMessage("AskToParticipate"));
			String update = gameEngine.getPlayerStats();
			sendToAllSessions(gameEngine, "updateStats" + update);
			return;
		} else {
			gameEngine.incTurn();
			if (gameEngine.getActivePlayer().equals(gameEngine.roundInitiater)) {
				sendToAllSessions(gameEngine, "NoSponsors");
				logger.info("No players chose to sponsor {} quest", gameEngine.storyDeck.faceUp.getName());
				if (gameEngine.getActivePlayer().isAI())
					gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
				return;
			}
			gameEngine.getActivePlayer().session.sendMessage(new TextMessage("sponsorQuest"));
		}

	}

	// execute AI discards
	public void AIDiscard(JsonObject jsonObject) throws IOException {
		int numCards = jsonObject.get("numCards").getAsInt();
		Player currentPlayer = gameEngine.getPlayerFromName(jsonObject.get("name").getAsString());

		AdventureCard[] AIdiscard = currentPlayer.getAI().discardAfterWinningTest(currentPlayer, numCards);

		String discards = "";

		for (AdventureCard a : AIdiscard) {
			discards += a.getName() + ";";
		}
		String message = "AIremoveFromHand";
		if (gameEngine.storyDeck.faceUp.getName().equals(("Prosperity Throughout the Realm")))
			message += "Prosperity";
		Random rand = new Random();
		int value = rand.nextInt(2000 - 1000) + 1000;

		setTimeout(() -> {

		}, value);
		currentPlayer.session.sendMessage(new TextMessage(message + discards));

	}

	// get ai tournament participation answer
	private void AITournamentParticipation(JsonObject jsonObject, WebSocketSession session) throws IOException {
		boolean answer = gameEngine.getActivePlayer().getAI().doIParticipateTournament();
		String name = gameEngine.getActivePlayer().getName();
		if (answer) {
			logger.info("Player {} accepted to participate in Tournament {}", name,
					gameEngine.storyDeck.faceUp.getName());
			gameEngine.current_tournament.acceptParticipation(name);
			logger.info("Informing other players that {} accepted to participate in Tournament {}", name,
					gameEngine.storyDeck.faceUp.getName());
			sendToAllSessionsExceptCurrent(gameEngine, session, "AcceptedTournie" + name);
		} else {
			logger.info("Player {} declined to participate in Tournament {}", name,
					gameEngine.storyDeck.faceUp.getName());
			gameEngine.current_tournament.denyParticipation(name);
			logger.info("Informing other players that {} declined to participate in Tournament {}", name,
					gameEngine.storyDeck.faceUp.getName());
			sendToAllSessionsExceptCurrent(gameEngine, session, "DeclinedTournie" + name);
		}

	}

	// get ai quest participation answer
	public void AIQuestParticipation(JsonObject jsonObject) throws IOException {
		if (gameEngine.getActivePlayer().getAI().doIParticipateQuest()) {
			gameEngine.current_quest.acceptParticipation(gameEngine.getActivePlayer().getName());
			return;
		} else {
			gameEngine.current_quest.denyParticipation(gameEngine.getActivePlayer().getName());
		}
	}

	// used to space out ai messages -> seg fault if two recieved at same time
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

	// initializes new AI player
	public void setupNewAIPlayer(JsonObject jsonObject, WebSocketSession session) throws IOException {
		JsonElement playerName = jsonObject.get("AI");
		Player newPlayer = new Player(playerName.getAsString(), session, 2);
		gameEngine.players.add(newPlayer);
		logger.info("Player {} is enrolled in the game", playerName.getAsString());
		int freeSpots = 4 - gameEngine.players.size();
		logger.info("There are {} spots available for AI players", freeSpots);
		if (gameEngine.players.size() == 4) {
			sendToAllSessions(gameEngine, "GameReadyToStart");
			logger.info("All players have joined, starting game...");
			if (gameEngine.riggedGame != 0) {
				if (gameEngine.riggedGame == 42) {
					gameEngine.players.get(0).setHand(gameEngine.mockHand1); // pickUpNewHand()
					gameEngine.players.get(1).setHand(gameEngine.mockHand2);
					gameEngine.players.get(2).setHand(gameEngine.mockHand3);
					gameEngine.players.get(3).setHand(gameEngine.mockHand4);
				}
				if (gameEngine.riggedGame == 43) {
					gameEngine.players.get(0).setHand(gameEngine.mockHand5); // pickUpNewHand()
					gameEngine.players.get(1).setHand(gameEngine.mockHand6);
					gameEngine.players.get(2).setHand(gameEngine.mockHand7);
					gameEngine.players.get(3).setHand(gameEngine.mockHand8);
				}
			} else {
				for (Player p : gameEngine.players) {
					p.pickupNewHand(gameEngine.adventureDeck);
				}
			}
			for (Player p : gameEngine.players) {
				String handString = "";
				for (AdventureCard a : p.getHand())
					handString += a.getName() + ", ";
				p.session.sendMessage(new TextMessage("setHand" + p.getHandString()));
				p.session.sendMessage(new TextMessage("currentRank" + p.getRank().getStringFile()));
				logger.info("Player {} was just dealt a new hand consisting of {}", p.getName(), handString);
			}
			// adds 2 second delay before flipping first card
			setTimeout(() -> {
				try {
					flipStoryCard();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}, 2000);
			String temp = gameEngine.getPlayerStats();
			sendToAllSessions(gameEngine, "updateStats" + temp);
		}
	}
}
