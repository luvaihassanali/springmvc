package com.luvai.model;

import java.util.ArrayList;

import org.springframework.web.socket.WebSocketSession;

import com.google.gson.JsonObject;
import com.luvai.model.AI.AI;
import com.luvai.model.AI.Strategy2;
import com.luvai.model.AdventureCards.AdventureCard;
import com.luvai.model.AdventureCards.AllyCard;
import com.luvai.model.AdventureCards.AmourCard;
import com.luvai.model.AdventureCards.WeaponCard;
import com.luvai.model.Decks.AdventureDeck;

public class Player {
	public String id;
	public String name;
	public WebSocketSession session;
	private ArrayList<AdventureCard> hand;
	ArrayList<WeaponCard> weapons;
	ArrayList<AllyCard> allies;
	AmourCard amour;
	RankCard rank;
	int shields;
	int battlePoints;
	public int tieCheck;
	AI ai;

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
	}

	public Player(String name) {
		this.name = name;
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
		}
		if (this.shields >= 7) {
			this.rank = CardList.Knight;
		}
		if (this.shields >= 10) {
			this.rank = CardList.ChampionKnight;
			this.tieCheck = 1;
		}

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

	public AI getAI() {
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