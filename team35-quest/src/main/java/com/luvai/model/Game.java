package com.luvai.model;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.luvai.controller.AIController;
import com.luvai.controller.EventController;
import com.luvai.controller.QuestController;
import com.luvai.controller.SocketHandler;
import com.luvai.controller.TournamentController;
import com.luvai.model.AdventureCards.AdventureCard;
import com.luvai.model.Decks.AdventureDeck;
import com.luvai.model.Decks.StoryDeck;
import com.luvai.model.StoryCards.EventCard;
import com.luvai.model.StoryCards.QuestCard;

public class Game {
	private static final Logger logger = LogManager.getLogger(Game.class);
	public ArrayList<Player> players;
	public StoryDeck storyDeck;
	public AdventureDeck adventureDeck;
	private int winCondition;
	public int numTurns = 0;
	public QuestController current_quest;
	public EventController current_event;
	public TournamentController current_tournament;
	public AIController AIController;
	public Player roundInitiater;
	public int riggedGame = 0;
	public boolean tournamentInit = false;
	static public boolean KingsRecognition = false;

	public Game() {
		logger.info("\n\n\n\n ****************************** Initialising new game ********************************");
		players = new ArrayList<Player>();
		adventureDeck = new AdventureDeck();
		storyDeck = new StoryDeck();
		AIController = new AIController();

	}

	@SuppressWarnings("serial")
	public ArrayList<AdventureCard> mockHand1 = new ArrayList<AdventureCard>() {
		{
			add(CardList.Boar);
			add(CardList.BlackKnight);
			add(CardList.BeastTest);
			add(CardList.Dagger);
			add(CardList.Amour);
			add(CardList.Horse);
			add(CardList.Saxons);
			add(CardList.Merlin);
			add(CardList.Sword);
			add(CardList.TemptationTest);
			add(CardList.Sword);
			add(CardList.SirGawain);
		}
	};
	@SuppressWarnings("serial")
	public ArrayList<AdventureCard> mockHand2 = new ArrayList<AdventureCard>() {
		{
			add(CardList.SirTristan);
			add(CardList.Merlin);
			add(CardList.Amour);
			add(CardList.RobberKnight);
			add(CardList.Dragon);
			add(CardList.BeastTest);
			add(CardList.Dagger);
			add(CardList.Horse);
			add(CardList.Battleax);
			add(CardList.GreenKnight);
			add(CardList.Lance);
			add(CardList.Thieves);
		}
	};
	@SuppressWarnings("serial")
	public ArrayList<AdventureCard> mockHand3 = new ArrayList<AdventureCard>() {
		{
			add(CardList.ValorTest);
			add(CardList.BlackKnight);
			add(CardList.Saxons);
			add(CardList.Thieves);
			add(CardList.KingArthur);
			add(CardList.Excalibur);
			add(CardList.Amour);
			add(CardList.MorganTest);
			add(CardList.Thieves);
			add(CardList.Horse);
			add(CardList.SirGalahad);
			add(CardList.RobberKnight);
		}
	};
	@SuppressWarnings("serial")
	public ArrayList<AdventureCard> mockHand4 = new ArrayList<AdventureCard>() {
		{

			// add(CardList.Dagger);
			add(CardList.KingArthur);
			add(CardList.Thieves);
			add(CardList.Saxons);
			add(CardList.Battleax);
			add(CardList.Boar);
			add(CardList.Lance);
			add(CardList.SirGawain);
			add(CardList.Sword);
			add(CardList.Dagger);
			add(CardList.Sword);
			add(CardList.Amour);
			add(CardList.SirLancelot);
		}
	};
	@SuppressWarnings("serial")
	public ArrayList<AdventureCard> mockHand5 = new ArrayList<AdventureCard>() {
		{
			add(CardList.Boar);
			add(CardList.BlackKnight);
			add(CardList.BeastTest);
			add(CardList.Dagger);
			add(CardList.Amour);
			add(CardList.Horse);
			add(CardList.Saxons);
			add(CardList.Merlin);
			add(CardList.Sword);
			add(CardList.TemptationTest);
			add(CardList.Lance);
			add(CardList.SirGawain);
		}
	};
	@SuppressWarnings("serial")
	public ArrayList<AdventureCard> mockHand6 = new ArrayList<AdventureCard>() {
		{
			add(CardList.SirTristan);
			add(CardList.MorganTest);
			add(CardList.Amour);
			add(CardList.RobberKnight);
			add(CardList.Dragon);
			add(CardList.GreenKnight);
			add(CardList.Dagger);
			add(CardList.Horse);
			add(CardList.Battleax);
			add(CardList.GreenKnight);
			add(CardList.Lance);
			add(CardList.Thieves);
		}
	};
	@SuppressWarnings("serial")
	public ArrayList<AdventureCard> mockHand7 = new ArrayList<AdventureCard>() {
		{
			add(CardList.ValorTest);
			add(CardList.BlackKnight);
			add(CardList.Saxons);
			add(CardList.Thieves);
			add(CardList.KingArthur);
			add(CardList.QueenIseult);
			add(CardList.Amour);
			add(CardList.MorganTest);
			add(CardList.Thieves);
			add(CardList.Dragon);
			add(CardList.SirGalahad);
			add(CardList.RobberKnight);
		}
	};
	@SuppressWarnings("serial")
	public ArrayList<AdventureCard> mockHand8 = new ArrayList<AdventureCard>() {
		{

			// add(CardList.Dagger);
			add(CardList.KingArthur);
			add(CardList.Thieves);
			add(CardList.Saxons);
			add(CardList.Thieves);
			add(CardList.Boar);
			add(CardList.QueenIseult);
			add(CardList.SirGawain);
			add(CardList.Dagger);
			add(CardList.Dagger);
			add(CardList.SirPellinore);
			add(CardList.BlackKnight);
			add(CardList.SirLancelot);
		}
	};

	public void newQuest(Game g, Player s, QuestCard q) throws IOException {
		current_quest = new QuestController(g, s, q);
	}

	public void newEvent(Game g, Player s, EventCard e) throws IOException {
		current_event = new EventController(g, s, e);
	}

	public void incTurn() {
		this.numTurns++;
	}

	public void setName(Player p, String s) {
		p.setName(s);
	}

	public Player getNextPlayer() {
		return players.get((numTurns + 1) % players.size());
	}

	public Player getActivePlayer() {
		return players.get(numTurns % players.size());
	}

	public Player getPrevPlayer() {
		return players.get(((numTurns - 1) % players.size()));
	}

	public Player getPlayerFromName(String name) {
		for (int i = 0; i < this.players.size(); i++) {
			if (name.equals(this.players.get(i).getName())) {
				return this.players.get(i);
			}
		}
		return null;
	}

	public Player getCurrentParticipant() {
		if (current_quest.participants.size() == 1)
			return current_quest.participants.get(0);
		return current_quest.participants.get(current_quest.participantTurns % current_quest.participants.size());
	}

	// convenience method because I think this will get called a lot
	public boolean isActiveAI() {
		return (this.getActivePlayer().isAI());
	}

	public int getWinCondition() {
		return winCondition;
	}

	public void setWinCondition(int winCondition) {
		this.winCondition = winCondition;
	}

	public void checkForTie() {
		// loop through player list checking for tieCheck = 1 (player var set after
		// champ knight achieved)
		// keep track of # of tieCheck and if 1 ---> end of game screen sole winner
		// if +1 then ---> end of game screen tie?
	}

	public String getPlayerStats() {
		String temp = "";
		for (int i = 0; i < players.size(); i++) {
			temp += players.get(i).getName() + ";" + players.get(i).getRank().getName() + ";"
					+ (players.get(i).getHandSize()) + ";" + players.get(i).shields + "#";
		}

		return temp;
	}

	public void setupNewPlayer(JsonObject jsonObject, WebSocketSession session) throws IOException {
		JsonElement playerName = jsonObject.get("newName");
		Player newPlayer = new Player(playerName.getAsString(), session);
		this.players.add(newPlayer);
		logger.info("Player {} is enrolled in the game", playerName.getAsString());
		if (this.players.size() == 4) {
			SocketHandler.sendToAllSessions(this, "GameReadyToStart");
			logger.info("All players have joined, starting game...");
			if (riggedGame != 0) {
				if (riggedGame == 42) {
					this.players.get(0).setHand(this.mockHand1); // pickUpNewHand()
					this.players.get(1).setHand(this.mockHand2);
					this.players.get(2).setHand(this.mockHand3);
					this.players.get(3).setHand(this.mockHand4);
				}
				if (riggedGame == 43) {
					this.players.get(0).setHand(this.mockHand5); // pickUpNewHand()
					this.players.get(1).setHand(this.mockHand6);
					this.players.get(2).setHand(this.mockHand7);
					this.players.get(3).setHand(this.mockHand8);
				}
			} else {
				for (Player p : this.players) {
					p.pickupNewHand(this.adventureDeck);
				}
			}
			for (Player p : this.players) {
				p.session.sendMessage(new TextMessage("setHand" + p.getHandString()));
				String handString = "";
				for (AdventureCard a : p.getHand())
					handString += a.getName() + ", ";
				p.session.sendMessage(new TextMessage("setHand" + p.getHandString()));
				p.session.sendMessage(new TextMessage("currentRank" + p.getRank().getStringFile()));
				logger.info("Player {} was just dealt a new hand consisting of {}", p.getName(), handString);
			}
			setTimeout(() -> {
				try {
					SocketHandler.flipStoryCard();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}, 2000);
			logger.info("Updating GUI stats for all players");
			this.updateStats();

		}
	}

	public void getSponsor(JsonObject jsonObject) throws IOException {
		JsonElement sponsor_quest_answer = jsonObject.get("sponsor_quest");
		JsonElement name = jsonObject.get("name");
		if (sponsor_quest_answer.getAsBoolean()) {
			logger.info("{} accepted to sponsor quest {}", name.getAsString(), this.storyDeck.faceUp.getName());
			this.newQuest(this, this.getActivePlayer(), (QuestCard) this.storyDeck.faceUp);
			SocketHandler.sendToAllSessions(this, "resetStageTracker");
		} else {
			logger.info("{} declined to sponsor quest {}", name.getAsString(), this.storyDeck.faceUp.getName());
			logger.info("Informing other players that Player {} declined to sponsor {} quest",
					this.getActivePlayer().getName(), this.storyDeck.faceUp.getName());
			SocketHandler.sendToAllSessionsExceptCurrent(this, this.getActivePlayer().session,
					"declinedToSponsor" + this.getActivePlayer().getName());
			this.incTurn();
			if (this.getActivePlayer().equals(this.roundInitiater)) {
				SocketHandler.sendToAllSessions(this, "NoSponsors");
				logger.info("No players chose to sponsor {} quest", this.storyDeck.faceUp.getName());
				this.incTurn();
				this.getActivePlayer().session.sendMessage(new TextMessage("undisableFlip"));
				return;
			}
			logger.info("Asking Player {} to sponsor quest {}", this.getActivePlayer().getName(),
					this.storyDeck.faceUp.getName());
			this.getActivePlayer().session.sendMessage(new TextMessage("sponsorQuest"));
		}

	}

	public void updateStats() {
		String update = this.getPlayerStats();
		SocketHandler.sendToAllSessions(this, "updateStats" + update);

	}

	public void getLogInfo(JsonObject jsonObject) {
		String playerName = jsonObject.get("name").getAsString();
		String logInfo = jsonObject.get("logInfo").getAsString();
		if (logInfo.equals("RepeatWeaponSponsor")) {
			logger.info("Player {} attempted to choose a repeat weapon during {} quest sponsor", playerName,
					this.storyDeck.faceUp.getName());
		}
		if (logInfo.equals("FoePointHigher")) {
			logger.info("Player {} attempted to choose a foe higher than ones already chosing during {} quest sponsor",
					playerName, this.storyDeck.faceUp.getName());
		}
		if (logInfo.equals("RepeatWeaponQuestP")) {
			logger.info("Player {} attempted to choose a repeat weapon during {} quest", playerName,
					this.storyDeck.faceUp.getName());
		}
		if (logInfo.equals("RepeatWeaponTournieP")) {
			logger.info("Player {} attempted to choose a repeat weapon during Tournament {}", playerName,
					this.storyDeck.faceUp.getName());
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

	public void execMerlin(JsonObject jsonObject) {
		Player player = this.getPlayerFromName(jsonObject.get("name").getAsString());
		if (jsonObject.has("revealedCards")) {
			logger.info("JSON example: {}", jsonObject.toString());
			logger.info("Player {} was revealed these cards as part of stage: {}", player.getName(),
					jsonObject.get("revealedCards").toString());
			return;
		}
		if (jsonObject.has("accepted")) {
			int stage = jsonObject.get("accepted").getAsInt();
			logger.info("Player {} chose to preview stage {}", player.getName(), stage);
			return;
		}
		logger.info("Player {} has Merlin in hand, being offered to preview stages", player.getName());

	}

}
