package com.luvai.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.luvai.model.Card;
import com.luvai.model.CardList;
import com.luvai.model.Game;
import com.luvai.model.Player;
import com.luvai.model.AdventureCards.AdventureCard;
import com.luvai.model.AdventureCards.AllyCard;
import com.luvai.model.AdventureCards.AmourCard;
import com.luvai.model.AdventureCards.WeaponCard;
import com.luvai.model.StoryCards.StoryCard;
import com.luvai.model.StoryCards.TournamentCard;

public class TournamentController extends SocketHandler {
	private static final Logger logger = LogManager.getLogger(TournamentController.class);
	Game gameEngine;
	ArrayList<Player> participants;
	TournamentCard currentTournament;
	boolean firstPlayer;
	Player tournamentInitiater;
	public int participantTurns = 0;
	public int tracker = 0;
	public CardList cardFinder;
	JsonArray tournie_info;
	String playerTourniePoints;

	public TournamentController(Game g, StoryCard faceUp) {
		gameEngine = g;
		participants = new ArrayList<Player>();
		currentTournament = (TournamentCard) faceUp;
		logger.info("Initiating new tournament {}", currentTournament.getName());
		cardFinder = new CardList();
		tournie_info = new JsonArray();
		playerTourniePoints = "";

	}

	public void getNewTourniePlayers(JsonObject jsonObject, WebSocketSession session) throws IOException {
		JsonElement participate_t_answer = jsonObject.get("participate_tournament");
		JsonElement name = jsonObject.get("name");

		if (participate_t_answer.getAsBoolean()) {

			acceptParticipation(name.getAsString());
			return;

		} else {
			denyParticipation(name.getAsString());
		}
	}

	private void denyParticipation(String n) throws IOException {
		gameEngine.incTurn();
		// System.out.println(gameEngine.getActivePlayer().getName());
		if (gameEngine.getActivePlayer().equals(gameEngine.roundInitiater)) {
			System.out.println("gone thru everyone decline");
			startTournament();
			return;
		}
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("participateTournament"));
	}

	private void acceptParticipation(String n) throws IOException {
		String playerName = n;
		// System.out.println(playerName);
		Player p = gameEngine.getPlayerFromName(playerName);
		// System.out.println(p.getName());
		gameEngine.current_tournament.participants.add(p);
		gameEngine.incTurn();
		// System.out.println(gameEngine.getActivePlayer().getName());
		if (gameEngine.getActivePlayer().equals(gameEngine.roundInitiater)) {
			// System.out.println("gone thru everyone accept");
			startTournament();
			return;
		}
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("participateTournament"));

	}

	private void startTournament() throws IOException {
		if (gameEngine.current_tournament.participants.size() == 0) {
			System.out.println("no tournament particpants");
			gameEngine.incTurn();
			gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
			return;
		}
		if (gameEngine.current_tournament.participants.size() == 1) {
			TournamentCard t = (TournamentCard) gameEngine.storyDeck.faceUp;
			int bonus = t.getBonus();
			Player p = gameEngine.current_tournament.participants.get(0);
			logger.info(
					"Only 1 participant in Tournament {}, Player {} gets 1 shield for entering and {} bonus shields",
					t.getName(), p.getName(), bonus);
			p.giveShields(1 + bonus);
			gameEngine.updateStats();
			gameEngine.incTurn();
			gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
			return;
		}
		for (int i = 0; i < gameEngine.current_tournament.participants.size(); i++) {
			System.out.println(gameEngine.current_tournament.participants.get(i).getName());
		}
		pickUpBeforeTournie();
		sendToAllParticipants(gameEngine, "ChooseEquipmentTournie");
	}

	// send to non participants
	public void sendToNonParticipants(Game gameEngine, String message) throws IOException {
		ArrayList<Player> temp = new ArrayList<Player>();
		for (int i = 0; i < gameEngine.players.size(); i++) {
			temp.add(gameEngine.players.get(i));
		}

		for (int j = 0; j < gameEngine.current_tournament.participants.size(); j++) {
			for (int i = 0; i < temp.size(); i++) {
				if (temp.isEmpty())
					return;
				if (temp.get(i).equals(gameEngine.current_tournament.participants.get(j)))
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
		for (Player p : gameEngine.current_tournament.participants) {
			p.session.sendMessage(new TextMessage(message));
		}
	}

	// send to current participant
	public void sendToCurrentParticipant(Game gameEngine, String message) throws IOException {
		Player player = gameEngine.current_tournament.getCurrentParticipant();
		player.session.sendMessage((new TextMessage(message)));
	}

	private Player getCurrentParticipant() {
		if (participants.size() == 1)
			return participants.get(0);
		return participants.get(participantTurns % participants.size());
	}

	// send to next participant
	public void sendToNextParticipant(Game gameEngine, String message) throws IOException {
		Player player = gameEngine.current_tournament.getCurrentParticipant();
		player.session.sendMessage(new TextMessage(message));
	}

	public void pickUpBeforeTournie() throws IOException {
		for (Player p : participants) {
			p.getHand().add(gameEngine.adventureDeck.flipCard());
			String newCardLink = gameEngine.adventureDeck.faceUp.getStringFile();
			p.session.sendMessage(new TextMessage("pickupBeforeStage" + newCardLink));
		}

		gameEngine.updateStats();
	}

	public void parseTournieInfo(JsonObject jsonObject) {
		tournie_info.add(jsonObject);
		tracker++;
		Player currentPlayer = gameEngine.getPlayerFromName(jsonObject.get("name").getAsString());
		JsonArray temp = (JsonArray) jsonObject.get("tournament_info");
		ArrayList<String> tournieEquip = new ArrayList<String>();
		for (int i = 0; i < temp.size(); i++) {
			JsonElement x = temp.get(i);
			String s = x.getAsString();
			tournieEquip.add(s);
		}
		equipTournie(currentPlayer, tournieEquip);
		if (tracker == gameEngine.current_tournament.participants.size()) {
			logger.info("Recieved all tournament challenges");
			sendToAllSessions(gameEngine, "tournieInfo" + tournie_info);
			sendToAllSessions(gameEngine, "tournieInfoPts" + playerTourniePoints);
			calculateOutcome();
		}
	}

	private void calculateOutcome() {
		// System.out.println(playerTourniePoints);
		String[] x = playerTourniePoints.split(";");
		ArrayList<String> playerTournamentInfo = new ArrayList<String>();
		ArrayList<String[]> y = new ArrayList<String[]>();
		for (int i = 0; i < x.length; i++) {
			y.add(x[i].split("#"));
		}
		for (int i = 0; i < y.size(); i++) {
			for (int j = 0; j < y.get(i).length; j++) {
				// System.out.println(y.get(i)[j]);
				playerTournamentInfo.add(y.get(i)[j]);
			}
		}
		for (String s : playerTournamentInfo)
			System.out.println(s);

	}

	private void equipTournie(Player currentParticipant, ArrayList<String> tournieEquip) {
		// System.out.println("equipping: " + currentParticipant.getName());
		for (String s : tournieEquip) {
			Card temp_card = cardFinder.getCardFromName(s);
			if (temp_card instanceof WeaponCard) {
				// System.out.println("got weapon");
				currentParticipant.getWeapons().add((WeaponCard) temp_card);
			}
			if (temp_card instanceof AllyCard) {
				// System.out.println("got ally");
				currentParticipant.getAllies().add((AllyCard) temp_card);
			}
			if (temp_card instanceof AmourCard) {
				// System.out.println("got amour");
				currentParticipant.setAmourCard((AdventureCard) temp_card);
			}
		}
		calculateTourniePoints();
	}

	private void calculateTourniePoints() {
		ArrayList<String> p_points = new ArrayList();
		for (Player p : gameEngine.current_tournament.participants) {
			int points = p.getBattlePoints();
			ArrayList<String> removeWeapons = new ArrayList<String>();
			// String cardNames = "";
			if (p.getAmourCard() != null) {
				points += 10;
				// cardNames += p.getAmourCard().getName() + "#";
				removeWeapons.add("Amour");
			}
			for (WeaponCard w : p.getWeapons()) {
				points += w.getBattlePoints();
				// cardNames += w.getName() + "#";
				removeWeapons.add(w.getName());
			}
			for (AllyCard a : p.getAllies()) {
				points += a.getBattlePoints();
				// cardNames += a.getName() + "#";
				// ally bonus points
			}
			// System.out.println(points);
			// System.out.println(p.getName());
			String temp = p.getName() + "#" + points + ";";
			p_points.add(temp);
		}
		String send = "";
		for (String s : p_points) {
			// System.out.println(s);
			send += s;
		}
		playerTourniePoints = send;

	}
}
