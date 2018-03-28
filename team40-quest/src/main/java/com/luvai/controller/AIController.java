package com.luvai.controller;

import java.io.IOException;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.TextMessage;

import com.google.gson.JsonObject;
import com.luvai.model.Player;
import com.luvai.model.AdventureCards.AdventureCard;
import com.luvai.model.StoryCards.QuestCard;

public class AIController extends SocketHandler {
	private static final Logger logger = LogManager.getLogger(AIController.class);

	public AIController() {

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
			sendToNextPlayer(gameEngine, "AskToParticipate");
			gameEngine.incTurn();
			// gameEngine.current_quest.sponsor.discardSponsor(cardToRemove);
			String update = gameEngine.getPlayerStats();
			sendToAllSessions(gameEngine, "updateStats" + update);
			return;
		} else {
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
		int value = rand.nextInt(3000 - 1500) + 1500;

		setTimeout(() -> {
			System.out.println("getting prosperity discards");
		}, value);
		currentPlayer.session.sendMessage(new TextMessage(message + discards));

		// String update = gameEngine.getPlayerStats();
		// sendToAllSessions(gameEngine, "updateStats" + update);

	}

	public void AIQuestParticipation(JsonObject jsonObject) throws IOException {
		if (gameEngine.getActivePlayer().getAI().doIParticipateQuest()) {
			logger.info("Player {} accepted to participate in quest {} sponsored by {}",
					gameEngine.getActivePlayer().getName(), gameEngine.storyDeck.faceUp.getName(),
					gameEngine.current_quest.sponsor.getName());
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
			logger.info("Player {} denied to participate in quest {} sponsored by {}",
					gameEngine.getActivePlayer().getName(), gameEngine.storyDeck.faceUp.getName(),
					gameEngine.current_quest.sponsor.getName());
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
			// System.out.println(gameEngine.getActivePlayer().getName());
			gameEngine.getActivePlayer().session.sendMessage(new TextMessage("AskToParticipate"));
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

}
