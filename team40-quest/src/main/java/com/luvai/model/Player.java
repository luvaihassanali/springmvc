package com.luvai.model;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.JsonObject;
import com.luvai.model.AI.AbstractAI;
import com.luvai.model.AI.Strategy2;
import com.luvai.model.AdventureCards.AdventureCard;
import com.luvai.model.AdventureCards.AllyCard;
import com.luvai.model.AdventureCards.AmourCard;
import com.luvai.model.AdventureCards.WeaponCard;
import com.luvai.model.Decks.AdventureDeck;

public class Player {
	private static final Logger logger = LogManager.getLogger(Player.class);
	public String id;
	public String name;
	public WebSocketSession session;
	private ArrayList<AdventureCard> hand;
	public ArrayList<WeaponCard> weapons;
	ArrayList<AllyCard> allies;
	AmourCard amour;
	RankCard rank;
	int shields;
	int battlePoints;
	public int tieCheck;
	AbstractAI ai;
	public ArrayList<String> testDiscardList;
	public ArrayList<String> replaceBonusBidsList;

	public Player() {

	}

	public Player(String name, WebSocketSession session) {
		this.id = session.getId();
		this.name = name;
		this.session = session;
		this.hand = new ArrayList<AdventureCard>();
		this.weapons = new ArrayList<WeaponCard>();
		this.rank = CardList.Squire;
		this.battlePoints = 5;
		this.shields = 0;
		this.amour = null;
		this.allies = new ArrayList<AllyCard>();
		this.tieCheck = 0;
		this.testDiscardList = new ArrayList<String>();
		this.replaceBonusBidsList = new ArrayList<String>();
	}

	public Player(String name, WebSocketSession session, int isAI) {
		this.id = session.getId();
		this.name = name;
		this.session = session;
		this.hand = new ArrayList<AdventureCard>();
		this.weapons = new ArrayList<WeaponCard>();
		this.rank = CardList.Squire;
		this.battlePoints = 5;
		this.shields = 0;
		this.amour = null;
		this.allies = new ArrayList<AllyCard>();
		this.tieCheck = 0;
		this.testDiscardList = new ArrayList<String>();
		this.replaceBonusBidsList = new ArrayList<String>();
		if (isAI == 2) {
			this.ai = new Strategy2();
		}
	}

	public void equipCards(JsonObject json) {

	}

	public int getHandSize() {

		return hand.size();
	}

	public void setName(String n) {
		name = n;
	}

	public String getHandString() {
		String handString = "";
		for (AdventureCard a : this.hand) {
			handString += (a.StringFile + ":");
		}
		return handString;

	}

	public void giveShields(int numShields) {

		this.addShields(numShields);
		/*
		 * if(Controller.isRigged) { if(this.shields>=game.getWinCondition()) {
		 * System.out.println("RIGGED GAME OVER, player acheived win condition");
		 * System.exit(0); } }
		 */
		if (this.shields >= 5) {
			this.rank = CardList.Knight;
			this.battlePoints = 10;
			logger.info("Player {} has advanced rank to {}!", this.getName(), this.rank.getName());
		}
		if (this.shields >= 7) {
			this.rank = CardList.ChampionKnight;
			logger.info("Player {} has advanced rank to {}!", this.getName(), this.rank.getName());
			this.battlePoints = 20;
		}
		if (this.shields >= 10) {
			this.rank = CardList.RoundTableKnight;
			logger.info("Player {} has advanced rank to {}!", this.getName(), this.rank.getName());
			this.tieCheck = 1;
			this.battlePoints = 100;
		}

	}

	public void discard(String name) {
		name = name.replaceAll("\"", "");
		for (int i = 0; i < this.getHandSize(); i++) {
			if (this.getHand().get(i).getName().equals(name)) {
				if (this.getHand().get(i) instanceof AmourCard) {
					logger.info("Player {} equipped {} as discard", this.getName(), this.getHand().get(i).getName());
					this.setAmourCard(this.getHand().get(i));
					this.getHand().remove(i);
					break;
				}
				if (this.getHand().get(i) instanceof AllyCard) {
					AllyCard ally = (AllyCard) this.getHand().get(i);
					logger.info(
							"Player {} allied with {} as discard, will now have +{} battle points for the rest of game",
							this.getName(), ally.getName(), ally.getBattlePoints());
					this.getAllies().add(ally);
					this.getHand().remove(ally);
					break;
				} else {
					logger.info("Player {} discarded {}", this.getName(), this.getHand().get(i).getName());
					this.getHand().remove(i);
					break;
				}
			}
		}
	}

	public void discardPlayer(ArrayList<String> toRemove) {
		for (int i = 0; i < toRemove.size(); i++) {
			for (int j = 0; j < this.getHandSize(); j++) {
				if (toRemove.get(i).equals(this.getHand().get(j).getName())) {
					logger.info("Player {} used {}", this.getName(), this.getHand().get(j).getName());
					this.getHand().remove(j);
					break;
				}
			}
		}
	}

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

	public void discardSponsor(ArrayList<String> cards) { // adjust for more stages

		ArrayList<String> names1 = new ArrayList<String>();
		ArrayList<String> names2 = new ArrayList<String>();
		ArrayList<String> names3 = new ArrayList<String>();
		ArrayList<String> names4 = new ArrayList<String>();
		ArrayList<String> names5 = new ArrayList<String>();

		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).endsWith(Integer.toString(0))) {

				names1.add(removeLastChar(cards.get(i)));
			}
		}
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).endsWith(Integer.toString(1))) {

				names2.add(removeLastChar(cards.get(i)));
			}
		}

		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).endsWith(Integer.toString(2))) {

				names3.add(removeLastChar(cards.get(i)));
			}
		}
		for (int i = 0; i < names1.size(); i++) {
			for (int j = 0; j < this.getHandSize(); j++) {
				if (this.hand.get(j).getName().equals(names1.get(i))) {
					logger.info("Player {} used {} in quest setup", this.getName(), this.getHand().get(j).getName());
					this.hand.remove(j);
					break;
				}
			}
		}

		for (int i = 0; i < names2.size(); i++) {
			for (int j = 0; j < this.getHandSize(); j++) {
				if (this.hand.get(j).getName().equals(names2.get(i))) {
					logger.info("Player {} used {} in quest setup", this.getName(), this.getHand().get(j).getName());
					this.hand.remove(j);
					break;
				}
			}
		}

		for (int i = 0; i < names3.size(); i++) {
			for (int j = 0; j < this.getHandSize(); j++) {
				if (this.hand.get(j).getName().equals(names3.get(i))) {
					logger.info("Player {} used {} in quest setup", this.getName(), this.getHand().get(j).getName());
					this.hand.remove(j);
					break;
				}
			}
		}

		for (int i = 0; i < names4.size(); i++) {
			for (int j = 0; j < this.getHandSize(); j++) {
				if (this.hand.get(j).getName().equals(names4.get(i))) {
					logger.info("Player {} used {} in quest setup", this.getName(), this.getHand().get(j).getName());
					this.hand.remove(j);
					break;
				}
			}
		}

		for (int i = 0; i < names5.size(); i++) {
			for (int j = 0; j < this.getHandSize(); j++) {
				if (this.hand.get(j).getName().equals(names5.get(i))) {
					logger.info("Player {} used {} in quest setup", this.getName(), this.getHand().get(j).getName());
					this.hand.remove(j);
					break;
				}
			}
		}
	}

	public String removeLastChar(String s) {
		if (s == null || s.length() == 0) {
			return s;
		}
		return s.substring(0, s.length() - 1);
	}

	public void addShields(int s) {
		shields += s;
	}

	public int getShields() {
		return shields;
	}

	public String getName() {
		return name;
	}

	public RankCard getRank() {
		return rank;
	}

	public int getBattlePoints() {
		return battlePoints;
	}

	public ArrayList<WeaponCard> getWeapons() {
		return weapons;
	}

	public void setAmourCard(AdventureCard a_card) {
		this.amour = (AmourCard) a_card;
	}

	public void unequipAmour() {
		this.amour = null;
	}

	public AmourCard getAmourCard() {
		return amour;
	}

	public void clearAmourCard() {
		amour = null;
	}

	public ArrayList<AllyCard> getAllies() {
		return allies;
	}

	public void pickupCards(int numCards, AdventureDeck deck) {
		for (int i = 0; i < numCards; i++) {
			this.getHand().add(deck.faceUp);
			if (deck.cards.isEmpty()) {
				deck.fillEmptyDeck();
			}
		}
	}

	public void pickupNewHand(AdventureDeck a) {
		for (int i = 0; i < 12; i++) {
			this.getHand().add(a.cards.pop());
			if (a.cards.isEmpty()) {
				a.initAdventureDeck();
			}
		}
	}

	public void setHand(ArrayList<AdventureCard> a) {
		this.hand = a;
	}

	public ArrayList<AdventureCard> getHand() {
		return hand;
	}

	public boolean isAI() {
		return !(this.ai == null);
	}

	public AbstractAI getAI() {
		return this.ai;
	}

	public void setAI(int type) {
		switch (type) {
		case 2:
			this.ai = new Strategy2();
			break;
		}
	}
}