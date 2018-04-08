package com.luvai.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.TextMessage;

import com.luvai.model.Game;
import com.luvai.model.Player;
import com.luvai.model.AdventureCards.AdventureCard;
import com.luvai.model.StoryCards.EventCard;

public class EventController extends SocketHandler {
	private static final Logger logger = LogManager.getLogger(EventController.class);
	Game gameEngine;
	Player current_player;
	EventCard eventCard;
	int prosperityTracker;

	public EventController(Game g, Player p, EventCard e) throws IOException {
		gameEngine = g;
		current_player = p;
		eventCard = e;
		executeEvent();
		prosperityTracker = 0;
	}

	public void executeEvent() throws IOException {
		logger.info("Player {} has initiated event {}", current_player.getName(), eventCard.getName());

		if (eventCard.getName().equals("Chivalrous Deed")) {
			EventChivalrous();
		}
		if (eventCard.getName().equals("Court Called Camelot")) {

		}
		if (eventCard.getName().equals("King's Call to Arms")) {

		}
		if (eventCard.getName().equals("King's Recognition")) {
			EventKingsRecognition();
		}
		if (eventCard.getName().equals("Plague")) {
			EventPlague();
		}
		if (eventCard.getName().equals("Pox")) {
			EventPox();
		}
		if (eventCard.getName().equals("Prosperity Throughout the Realm")) {
			EventProsperity();
		}
		if (eventCard.getName().equals("Queen's Favor")) {

		}

	}

	public void EventKingsRecognition() throws IOException {
		logger.info("The next player(s) to complete a Quest will receive 2 extra shields");
		Game.KingsRecognition = true;
		gameEngine.incTurn();
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
	}

	public void EventPlague() throws IOException {
		logger.info("If applicable drawer: {} loses 2 shields", current_player.getName());
		logger.info("Player {} currently has {} shields", current_player.getName(), current_player.getShields());
		if (current_player.getShields() == 0) {
			logger.info("No shields to remove for player {}", current_player.getName());
		}
		if (current_player.getShields() == 1) {
			logger.info("Removing the only shield player {} has", current_player.getName());
			current_player.removeShield();
		}
		if (current_player.getShields() >= 2) {
			logger.info("Removing two shields from player {}", current_player.getName());
			current_player.removeShield();
			current_player.removeShield();
		}
		logger.info("Player {} left with {} shields", current_player.getName(), current_player.getShields());
		gameEngine.incTurn();
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
		logger.info("Event {} has concluded", gameEngine.storyDeck.faceUp.getName());
		logger.info("Updating GUI stats for all players");
		gameEngine.updateStats();
	}

	public void EventPox() throws IOException {
		ArrayList<Player> sortedByShields = new ArrayList<Player>();
		sortedByShields.addAll(gameEngine.players);

		for (Player p : sortedByShields) {
			if (p.equals(current_player)) {
			} else {
				logger.info("{} with {} shields", p.getName(), p.getShields());
			}
		}
		logger.info("Remove one shield (if applicable) from all players except event initiater");
		for (Player p : gameEngine.players) {
			if (p.equals(current_player)) {
			} else {
				if (p.getShields() == 0) {
					logger.info("No shields to remove for player {}", p.getName());
				} else {
					p.removeShield();
					logger.info("Removing shield from player {}", p.getName());
				}
			}
		}
		for (Player p : sortedByShields) {
			if (p.equals(current_player)) {
			} else {
				logger.info("{} now with {} shields", p.getName(), p.getShields());
			}
		}
		gameEngine.incTurn();
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
		logger.info("Event {} has concluded", gameEngine.storyDeck.faceUp.getName());
		logger.info("Updating GUI stats for all players");
		gameEngine.updateStats();
	}

	public void EventChivalrous() throws IOException {
		ArrayList<Player> sortedByShields = new ArrayList<Player>();
		sortedByShields.addAll(gameEngine.players);

		for (Player p : sortedByShields) {
			logger.info("{} with {} shields", p.getName(), p.getShields());
		}

		// custom comparator. sorts by increasing shield count;
		Collections.sort(sortedByShields, new Comparator<Player>() {
			public int compare(Player p1, Player p2) {
				return p1.getShields() < p2.getShields() ? -1 : p1.getShields() > p2.getShields() ? 1 : 0;
			}
		});

		for (int i = 1; i < sortedByShields.size(); i++) {
			if (sortedByShields.get(i).getShields() > sortedByShields.get(i - 1).getShields()) {
				sortedByShields.subList(i, sortedByShields.size()).clear();
				break;
			}
		}

		for (int i = 0; i < sortedByShields.size(); i++) {
			sortedByShields.get(i).giveShields(3);
			logger.info(" {} is awarded with 3 shields for a total of {}", sortedByShields.get(i).getName(),
					sortedByShields.get(i).getShields());
		}
		sendToAllSessions(gameEngine, "wait");
		gameEngine.incTurn();
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
		logger.info("Event {} has concluded", gameEngine.storyDeck.faceUp.getName());
		logger.info("Updating GUI stats for all players");
		gameEngine.updateStats();
	}

	public void EventProsperity() throws IOException {
		String temp = "";
		logger.info("All players draw immediately 2 adventure cards & discard if too many");
		for (Player p : gameEngine.players) {
			AdventureCard newCard = gameEngine.adventureDeck.flipCard();
			p.getHand().add(newCard);
			temp += newCard.getStringFile() + ";";
			AdventureCard newCard2 = gameEngine.adventureDeck.flipCard();
			p.getHand().add(newCard2);
			temp += newCard2.getStringFile() + ";";
			p.session.sendMessage(new TextMessage("PickupCardsProsperity" + temp));
			temp = "";
			logger.info("Player {} picked up {} and {}", p.getName(), newCard.getName(), newCard2.getName());
		}
		logger.info("Updating GUI stats for all players");
		gameEngine.updateStats();
	}
}
