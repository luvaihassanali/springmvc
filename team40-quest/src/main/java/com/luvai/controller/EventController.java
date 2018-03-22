package com.luvai.controller;

import java.io.IOException;

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
	int prosperityTracker = 0;

	public EventController(Game g, Player p, EventCard e) throws IOException {
		gameEngine = g;
		current_player = p;
		eventCard = e;
		executeEvent();
	}

	public void executeEvent() throws IOException {
		logger.info("Player {} has initiated event {}", current_player.getName(), eventCard.getName());

		if (eventCard.getName().equals("Chivalrous Deed")) {

		}
		if (eventCard.getName().equals("Court Called Camelot")) {

		}
		if (eventCard.getName().equals("King's Call to Arms")) {

		}
		if (eventCard.getName().equals("King's Recognition ")) {

		}
		if (eventCard.getName().equals("Plague")) {

		}
		if (eventCard.getName().equals("Pox")) {

		}
		if (eventCard.getName().equals("Prosperity Throughout the Realm")) {
			EventProsperity();
		}
		if (eventCard.getName().equals("Queen's Favor")) {

		}

	}

	public void EventProsperity() throws IOException {
		String temp = "";
		logger.info("All players immediately 2 adventure cards");
		for (Player p : gameEngine.players) {
			AdventureCard newCard = gameEngine.adventureDeck.flipCard();
			p.getHand().add(newCard);
			temp += newCard.getStringFile() + ";";
			AdventureCard newCard2 = gameEngine.adventureDeck.flipCard();
			p.getHand().add(newCard2);
			temp += newCard2.getStringFile() + ";";
			p.session.sendMessage(new TextMessage("PickupCardsProsperity" + temp));
			temp = "";
		}
		String update = gameEngine.getPlayerStats();
		sendToAllSessions(gameEngine, "updateStats" + update);
	}
}
