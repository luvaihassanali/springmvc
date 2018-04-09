package com.luvai.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
import com.luvai.model.TournamentPlayer;
import com.luvai.model.TournamentPlayerComparator;
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
	boolean tieBreaker;
	boolean roundOne;
	int og_participant_size;

	public TournamentController(Game g, StoryCard faceUp) {
		gameEngine = g;
		participants = new ArrayList<Player>();
		currentTournament = (TournamentCard) faceUp;
		logger.info("Initiating new tournament {}", currentTournament.getName());
		cardFinder = new CardList();
		tournie_info = new JsonArray();
		playerTourniePoints = "";
		tieBreaker = false;
		roundOne = true;
		og_participant_size = 0;

	}

	public void tieBreaker(Game g, StoryCard faceUp, ArrayList<Player> participants, String s) throws IOException {
		logger.info("Initiating tie breaker Tournament {} with {}", faceUp.getName(), s);
		tieBreaker = true;
		gameEngine.current_tournament.participants = participants;
		tournie_info = new JsonArray();
		playerTourniePoints = "";
		pickUpBeforeTournie();
		String logStringP = "";
		for (Player p : gameEngine.current_tournament.participants) {
			p.session.sendMessage(new TextMessage("ChooseEquipmentTournie"));
			logStringP += p.getName() + ", ";
		}
		logger.info("Participants: {} are choosing weapons concurrently for Tournament {}", logStringP,
				faceUp.getName());
	}

	public void getNewTourniePlayers(JsonObject jsonObject, WebSocketSession session) throws IOException {
		JsonElement participate_t_answer = jsonObject.get("participate_tournament");
		JsonElement name = jsonObject.get("name");

		if (participate_t_answer.getAsBoolean()) {
			logger.info("Player {} accepted to participate in Tournament {}", name.getAsString(),
					gameEngine.storyDeck.faceUp.getName());
			acceptParticipation(name.getAsString());
			logger.info("Informing other players that {} accepted to participate in Tournament {}", name.getAsString(),
					gameEngine.storyDeck.faceUp.getName());
			sendToAllSessionsExceptCurrent(gameEngine, session, "AcceptedTournie" + name.getAsString());

		} else {
			logger.info("Player {} declined to participate in Tournament {}", name.getAsString(),
					gameEngine.storyDeck.faceUp.getName());
			denyParticipation(name.getAsString());
			logger.info("Informing other players that {} declined to participate in Tournament {}", name.getAsString(),
					gameEngine.storyDeck.faceUp.getName());
			sendToAllSessionsExceptCurrent(gameEngine, session, "DeclinedTournie" + name.getAsString());
		}
	}

	public void denyParticipation(String n) throws IOException {
		gameEngine.incTurn();
		// System.out.println(gameEngine.getActivePlayer().getName());
		if (gameEngine.getActivePlayer().equals(gameEngine.roundInitiater)) {
			logger.info("All player decisions accepted, starting Tournament {}", gameEngine.storyDeck.faceUp.getName());
			startTournament();
			return;
		}
		logger.info("Asking Player {} to participate in Tournament {}", gameEngine.getActivePlayer().getName(),
				gameEngine.storyDeck.faceUp.getName());
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("participateTournament"));
	}

	public void acceptParticipation(String n) throws IOException {
		String playerName = n;
		// System.out.println(playerName);
		Player p = gameEngine.getPlayerFromName(playerName);
		// System.out.println(p.getName());
		gameEngine.current_tournament.participants.add(p);
		gameEngine.incTurn();
		// System.out.println(gameEngine.getActivePlayer().getName());
		if (gameEngine.getActivePlayer().equals(gameEngine.roundInitiater)) {
			logger.info("All player decisions accepted, starting Tournament {}", gameEngine.storyDeck.faceUp.getName());
			startTournament();
			return;
		}
		logger.info("Asking Player {} to participate in Tournament {}", gameEngine.getActivePlayer().getName(),
				gameEngine.storyDeck.faceUp.getName());
		gameEngine.getActivePlayer().session.sendMessage(new TextMessage("participateTournament"));

	}

	private void startTournament() throws IOException {
		if (gameEngine.current_tournament.participants.size() == 0) {
			logger.info("No players accepted to participate in Tournament {}", gameEngine.storyDeck.faceUp.getName());
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
		String loggerPlayers = "";

		for (int i = 0; i < gameEngine.current_tournament.participants.size(); i++) {

			// System.out.println(gameEngine.current_tournament.participants.get(i).getName());
			loggerPlayers += gameEngine.current_tournament.participants.get(i).getName() + ", ";
		}
		logger.info("Tournament {} starting with participants: {}", gameEngine.storyDeck.faceUp.getName(),
				loggerPlayers);
		if (roundOne) {
			gameEngine.current_tournament.og_participant_size = gameEngine.current_tournament.participants.size();
			// System.out.println("OG SIZE: " + og_participant_size);
			roundOne = false;
		}
		pickUpBeforeTournie();
		logger.info("All tournament participants are choosing equipment concurrently");
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
		for (Player p : gameEngine.current_tournament.participants) {
			p.getHand().add(gameEngine.adventureDeck.flipCard());
			String newCardLink = gameEngine.adventureDeck.faceUp.getStringFile();
			p.session.sendMessage(new TextMessage("pickupBeforeStage" + newCardLink));
			logger.info("Player {} receiving one card: {} before choosing equipment", p.getName(),
					gameEngine.adventureDeck.faceUp.getName());
		}

		gameEngine.updateStats();
	}

	public void parseTournieInfo(JsonObject jsonObject) throws IOException {
		tournie_info.add(jsonObject);
		tracker++;
		Player currentPlayer = gameEngine.getPlayerFromName(jsonObject.get("name").getAsString());
		JsonArray temp = (JsonArray) jsonObject.get("tournament_info");
		ArrayList<String> tournieEquip = new ArrayList<String>();
		String loggerWeapons = "";
		for (int i = 0; i < temp.size(); i++) {
			JsonElement x = temp.get(i);
			String s = x.getAsString();
			loggerWeapons += s + ", ";
			tournieEquip.add(s);
		}
		if (loggerWeapons.equals(""))
			loggerWeapons = "nothing";
		logger.info("Player {} has equipped {} for Tournament {}", currentPlayer.getName(), loggerWeapons,
				gameEngine.storyDeck.faceUp.getName());
		equipTournie(currentPlayer, tournieEquip);
		if (tracker == gameEngine.current_tournament.participants.size()) {
			logger.info("Recieved all tournament challenges");
			sendToAllSessions(gameEngine, "tournieInfo" + tournie_info);
			calculateOutcome();
			tracker = 0;
		}
	}

	private void calculateOutcome() throws IOException {
		if (tieBreaker) {
			logger.info("calculating outcome of Tie-breaker of Tournament {}", gameEngine.storyDeck.faceUp.getName());
		}
		String[] x = playerTourniePoints.split(";");
		String[][] y = new String[x.length][2];
		for (int i = 0; i < x.length; i++) {
			y[i] = x[i].split("#");
		}
		TournamentPlayer[] contestants = new TournamentPlayer[x.length];
		String logStringTieBreaker = "";
		for (int i = 0; i < y.length; i++) {
			TournamentPlayer t = new TournamentPlayer(y[i]);
			contestants[i] = t;
			logStringTieBreaker += contestants[i].name + ", ";
		}
		if (tieBreaker)
			logger.info("{} participated in tie breaker", logStringTieBreaker);
		Arrays.sort(contestants, new TournamentPlayerComparator());
		String send = "";
		for (TournamentPlayer s : contestants) {
			// System.out.println("TOURNAMENT PLAYER FOR S");
			// System.out.println(s.toString());
			String[] logInfo = s.toString().split("#");
			logger.info("Player {} has {} battle points at rank {}", logInfo[0], logInfo[1],
					gameEngine.getPlayerFromName(logInfo[0]).getRank().getName());
			send += s.toString() + ";";
		}
		sendToAllSessions(gameEngine, "contestantInfo" + send);

		logger.info("Displaying tournament screen");

		ArrayList<TournamentPlayer> winners = new ArrayList<TournamentPlayer>();
		winners.add(contestants[0]);
		String logTieBreak = "";
		for (int i = 1; i < contestants.length; i++) {
			if (winners.get(0).points == contestants[i].points) {
				winners.add(contestants[i]);
				logTieBreak += contestants[i].name + ", ";
			}
		}
		if (winners.size() == 1) {
			TournamentCard t = (TournamentCard) gameEngine.storyDeck.faceUp;
			int bonus = t.getBonus();
			int participants = gameEngine.current_tournament.og_participant_size;
			int shieldTotal = bonus + participants;
			if (tieBreaker)
				logger.info("Tie breaker complete - one winner");
			logger.info("Player {} is the winner of the tournament", winners.get(0).name);
			logger.info(
					"Player {} receiving {} shields, {} for # of participants and {} as bonus shields per Tournament {} (tie-breaker)",
					winners.get(0).name, shieldTotal, participants, bonus, gameEngine.storyDeck.faceUp.getName());
			Player p = gameEngine.getPlayerFromName(winners.get(0).name);
			p.giveShields(shieldTotal);
			gameEngine.updateStats();
			gameEngine.incTurn();
			gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
			logger.info("Removing all amour/weapon equipped during tournament by participants");
		} else {
			if (tieBreaker)
				logger.info("Tie breaker complete - still a tie...");
			logger.info("There has been a tie between {} and {}", winners.get(0).name, logTieBreak);
			if (tieBreaker) {
				for (int i = 0; i < winners.size(); i++) {
					TournamentCard t = (TournamentCard) gameEngine.storyDeck.faceUp;
					int bonus = t.getBonus();
					int participants = gameEngine.current_tournament.og_participant_size;
					int shieldTotal = bonus + participants;
					Player p = gameEngine.getPlayerFromName(winners.get(i).name);
					logger.info(
							"Player {} receiving {} shields, {} for # of participants (orginial # - before tie-breaker) and {} as bonus shields per Tournament {} (tie-breaker)",
							p.name, shieldTotal, participants, bonus);
					p.giveShields(shieldTotal);
				}
				logger.info("Removing all amour/weapon equipped during tournament by participants");
				for (Player winner : gameEngine.players) {
					winner.getWeapons().clear();
					winner.clearAmourCard();
				}
				logger.info("Updating GUI stats for all players");
				gameEngine.updateStats();
				gameEngine.incTurn();
				gameEngine.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
				return;
			}
			logger.info("Clearing weapons and going into tiebreaker");
			ArrayList<Player> ties = new ArrayList<Player>();
			String logString = "";
			for (TournamentPlayer t : winners) {
				Player p = gameEngine.getPlayerFromName(t.name);
				ties.add(p);
				logString += p.getName() + ", ";
			}
			logger.info("Removing all amour/weapon equipped during tournament by participants");
			tieBreaker(gameEngine, gameEngine.storyDeck.faceUp, ties, logString);
		}

		for (Player p : gameEngine.players) {
			p.getWeapons().clear();
			p.unequipAmour();
		}

	}

	private void equipTournie(Player currentParticipant, ArrayList<String> tournieEquip) {
		currentParticipant.discardPlayer(tournieEquip);
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
		ArrayList<String> p_points = new ArrayList<String>();
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
			String temp = p.getName() + "#" + points + "#" + p.getRank().getStringFile() + ";";
			p_points.add(temp);
		}
		String send = "";
		for (String s : p_points) {
			// System.out.println(s);
			send += s;
		}
		playerTourniePoints = send;

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
