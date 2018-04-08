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
import com.luvai.model.AdventureCards.FoeCard;
import com.luvai.model.AdventureCards.WeaponCard;
import com.luvai.model.StoryCards.EventCard;

public class EventController extends SocketHandler {
	private static final Logger logger = LogManager.getLogger(EventController.class);
	Game gameEngine;
	Player current_player;
	EventCard eventCard;
	int prosperityTracker;
	int queensFavorTracker;
	int kingsArmsTracker;

	public EventController(Game g, Player p, EventCard e) throws IOException {
		gameEngine = g;
		current_player = p;
		eventCard = e;
		prosperityTracker = 0;
		queensFavorTracker = 0;
		kingsArmsTracker = 0;
		executeEvent();

	}

	public void executeEvent() throws IOException {
		logger.info("Player {} has initiated event {}", current_player.getName(), eventCard.getName());

		if (eventCard.getName().equals("Chivalrous Deed")) {
			EventChivalrous();
		}
		if (eventCard.getName().equals("Court Called to Camelot")) {
			CourtCalledCamelot();
		}
		if (eventCard.getName().equals("King's Call to Arms")) {
			KingsCallToArms();
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
			QueensFavor();
		}

	}

	int temp_tracker_kingsArms = 0;

	public void doneKingsCallToArms() throws IOException {
		temp_tracker_kingsArms++;
		if (temp_tracker_kingsArms == gameEngine.current_event.kingsArmsTracker) {
			gameEngine.incTurn();
			gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
			logger.info("Event {} has concluded", gameEngine.storyDeck.faceUp.getName());
			logger.info("Updating GUI stats for all players");
			gameEngine.updateStats();
		}
	}

	public void KingsCallToArms() throws IOException {
		logger.info(
				"The highest ranked player(s) must place 1 weapon in the discard pile. If unable to do so, 2 Foe Cards must be discarded");
		ArrayList<Player> sortedByShields = new ArrayList<Player>();
		sortedByShields.addAll(gameEngine.players);

		Collections.sort(sortedByShields, new Comparator<Player>() {
			public int compare(Player p1, Player p2) {
				return p1.getShields() < p2.getShields() ? -1 : p1.getShields() > p2.getShields() ? 1 : 0;
			}
		});

		for (Player p : sortedByShields) {

			logger.info("{} with {} shields", p.getName(), p.getShields());

		}
		Player highestRanked = sortedByShields.get(sortedByShields.size() - 1);
		ArrayList<Player> highestRankedList = new ArrayList<Player>();
		highestRankedList.add(highestRanked);
		sortedByShields.remove(highestRanked);
		for (Player p : sortedByShields) {
			if (p.getShields() == highestRanked.getShields()) {
				highestRankedList.add(p);
			}
		}
		kingsArmsTracker = 0;
		kingsArmsTracker = highestRankedList.size();
		System.out.println("size highest ranked: " + highestRankedList.size());
		for (Player p : highestRankedList) {
			int weaponTracker = 0;
			int foeTracker = 0;
			for (AdventureCard a : p.getHand()) {
				if (a instanceof WeaponCard) {
					weaponTracker++;
				}
				if (a instanceof FoeCard) {
					foeTracker++;
				}
			}
			if (weaponTracker != 0) {
				logger.info("Player {} will now discard 1 weapon", p.getName());
				p.session.sendMessage(new TextMessage("KingsCallToArmsWeapon"));
				continue;
			}
			if (foeTracker == 0) {
				logger.info("Player {} has no foes or weapons for discard, going to next turn");
				gameEngine.incTurn();
				gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
				logger.info("Event {} has concluded", gameEngine.storyDeck.faceUp.getName());
				logger.info("Updating GUI stats for all players");
				gameEngine.updateStats();
				continue;

			}
			if (foeTracker == 1) {
				logger.info("Player {} will now discard 1 foe (not enough)", p.getName());
				p.session.sendMessage(new TextMessage("KingsCallToArms1Foe"));
				continue;

			}
			if (foeTracker >= 2) {
				logger.info("Player {} will now discard 2 foes", p.getName());
				p.session.sendMessage(new TextMessage("KingsCallToArmsFoes"));
				continue;

			}

		}

	}

	int temp_tracker_queensFavor = 0;

	public void doneEventQueensFavor() throws IOException {
		temp_tracker_queensFavor++;
		if (temp_tracker_queensFavor == gameEngine.current_event.queensFavorTracker) {
			gameEngine.incTurn();
			gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
			logger.info("Event {} has concluded", gameEngine.storyDeck.faceUp.getName());
			logger.info("Updating GUI stats for all players");
			gameEngine.updateStats();
			temp_tracker_queensFavor = 0;
		}
	}

	public void QueensFavor() throws IOException {
		logger.info("The lowest ranked player(s) immediately receieves 2 Adventure Cards & will discard if too many");
		ArrayList<Player> sortedByShields = new ArrayList<Player>();
		sortedByShields.addAll(gameEngine.players);

		Collections.sort(sortedByShields, new Comparator<Player>() {
			public int compare(Player p1, Player p2) {
				return p1.getShields() < p2.getShields() ? -1 : p1.getShields() > p2.getShields() ? 1 : 0;
			}
		});

		for (Player p : sortedByShields) {

			logger.info("{} with {} shields", p.getName(), p.getShields());

		}
		Player lowestRanked = sortedByShields.get(0);
		ArrayList<Player> lowestRankedList = new ArrayList<Player>();
		lowestRankedList.add(lowestRanked);
		sortedByShields.remove(lowestRanked);
		for (Player p : sortedByShields) {
			if (p.getShields() == lowestRanked.getShields()) {
				lowestRankedList.add(p);
			}
		}
		queensFavorTracker = 0;
		queensFavorTracker = lowestRankedList.size();
		System.out.println("size lowest ranked: " + lowestRankedList.size());
		for (Player p : lowestRankedList) {
			logger.info("Player {} is among lowest ranked, receiving 2 adventure cards", p.getName());
		}
		String temp = "";
		for (Player p : lowestRankedList) {
			AdventureCard newCard = gameEngine.adventureDeck.flipCard();
			p.getHand().add(newCard);
			temp += newCard.getStringFile() + ";";
			AdventureCard newCard2 = gameEngine.adventureDeck.flipCard();
			p.getHand().add(newCard2);
			temp += newCard2.getStringFile() + ";";
			p.session.sendMessage(new TextMessage("PickupCardsQueensFavor" + temp));
			temp = "";
			logger.info("Player {} picked up {} and {}", p.getName(), newCard.getName(), newCard2.getName());
		}

		logger.info("Updating GUI stats for all players");
		gameEngine.updateStats();

	}

	public void CourtCalledCamelot() throws IOException {
		logger.info("All allies in play will be discarded");
		for (Player p : gameEngine.players) {
			if (p.getAllies().size() == 0) {
				logger.info("Player {} has no allies in play", p.getName());
			} else {
				for (int i = 0; i < p.getAllies().size(); i++) {
					logger.info("Player {} ally {} has been removed", p.getName(), p.getAllies().get(i).getName());
				}
				p.getAllies().clear();
			}
		}
		gameEngine.incTurn();
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
		logger.info("Event {} has concluded", gameEngine.storyDeck.faceUp.getName());
		logger.info("Updating GUI stats for all players");
		gameEngine.updateStats();
	}

	public void EventKingsRecognition() throws IOException {
		logger.info("The next player(s) to complete a Quest will receive 2 extra shields");
		Game.KingsRecognition = true;
		gameEngine.incTurn();
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
		logger.info("Event {} has concluded", gameEngine.storyDeck.faceUp.getName());
		logger.info("Updating GUI stats for all players");
		gameEngine.updateStats();
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

		Collections.sort(sortedByShields, new Comparator<Player>() {
			public int compare(Player p1, Player p2) {
				return p1.getShields() < p2.getShields() ? -1 : p1.getShields() > p2.getShields() ? 1 : 0;
			}
		});

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

	public void doneProsperity() throws IOException {
		// System.out.println(gameEngine.current_event.eventCard.getName());
		// System.out.println(gameEngine.current_event.prosperityTracker);

		gameEngine.current_event.prosperityTracker++;
		if (gameEngine.current_event.prosperityTracker == 4) {
			logger.info("Event {} has concluded", gameEngine.storyDeck.faceUp.getName());
			gameEngine.current_event.prosperityTracker = 0;

			gameEngine.incTurn();
			gameEngine.getActivePlayer().session.sendMessage((new TextMessage("undisableFlip")));
		}
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
