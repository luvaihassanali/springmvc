package com.luvai.controller;

import java.io.IOException;
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
		System.out.println("AI CONTROLLER INIT");
	}

	public void receiveAICommand(JsonObject jsonObject) throws IOException {

		if (jsonObject.get("AICommand").getAsString().equals("AskToParticipateQuest"))
			AIQuestParticipation(jsonObject);

		if (jsonObject.get("AICommand").getAsString().equals("DiscardChoice")) {
			AIDiscard(jsonObject);
		}

		if (jsonObject.get("AICommand").getAsString().equals("SponsorQuest")) {
			AIQuestSponsor(jsonObject);
		}

	}

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

	public void AIDiscard(JsonObject jsonObject) throws IOException {
		int numCards = jsonObject.get("numCards").getAsInt();
		Player currentPlayer = gameEngine.getPlayerFromName(jsonObject.get("name").getAsString());

		AdventureCard[] AIdiscard = currentPlayer.getAI().getDiscardChoice(currentPlayer, numCards);
		// for (AdventureCard a : currentPlayer.getHand()) {
		// System.out.println(a.getName());
		// }
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
			// System.out.println("getting prosperity discards");
		}, value);
		currentPlayer.session.sendMessage(new TextMessage(message + discards));

		// String update = gameEngine.getPlayerStats();
		// sendToAllSessions(gameEngine, "updateStats" + update);

	}

	public void AIQuestParticipation(JsonObject jsonObject) throws IOException {
		if (gameEngine.getActivePlayer().getAI().doIParticipateQuest()) {
			gameEngine.current_quest.acceptParticipation(gameEngine.getActivePlayer().getName());
			return;
		} else {
			gameEngine.current_quest.denyParticipation(gameEngine.getActivePlayer().getName());
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

	public void setupNewAIPlayer(JsonObject jsonObject, WebSocketSession session) throws IOException {
		JsonElement playerName = jsonObject.get("AI");
		Player newPlayer = new Player(playerName.getAsString(), session, 2);
		gameEngine.players.add(newPlayer);
		logger.info("Player {} is enrolled in the game", playerName.getAsString());
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

	public void doneProsperity() throws IOException {
		System.out.println(gameEngine.current_event.eventCard.getName());
		System.out.println(gameEngine.current_event.prosperityTracker);

		gameEngine.current_event.prosperityTracker++;
		if (gameEngine.current_event.prosperityTracker == 4) {
			logger.info("Event {} has concluded", gameEngine.storyDeck.faceUp.getName());
			gameEngine.current_event.prosperityTracker = 0;

			gameEngine.incTurn();
			gameEngine.getActivePlayer().session.sendMessage((new TextMessage("undisableFlip")));
		}
	}
}
