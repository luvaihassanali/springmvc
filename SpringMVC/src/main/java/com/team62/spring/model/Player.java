package com.team62.spring.model;

import org.springframework.web.socket.WebSocketSession;
import com.team62.spring.model.storyCards.*;
import com.team62.spring.model.AI.AI;
import com.team62.spring.model.AI.Strategy2;
import com.team62.spring.model.adventureCards.*;
import com.team62.spring.model.decks.AdventureDeck;

import java.util.ArrayList;

public class Player {
	public String id;
	public String name;
	public WebSocketSession session;
	public int sessionTracker;
	private ArrayList<AdventureCard> hand;
	ArrayList<WeaponCard> weapons;
	ArrayList<AllyCard> allies;
	AmourCard amour;
	RankCard rank;
	int shields;
	int battlePoints;
	public int tieCheck;
    AI ai;
    
	public Player(String id, String name, WebSocketSession session, int sessionTracker) {
		this.id = id;
		this.name = name;
		this.session = session;
		this.sessionTracker = sessionTracker;
		this.hand = new ArrayList<AdventureCard>();
		this.weapons = new ArrayList<WeaponCard>();
		this.rank = CardList.Squire;
		this.battlePoints = 5;
		this.shields = 0;
		this.amour = null;
		this.allies = new ArrayList<AllyCard>();
		this.tieCheck = 0;
	}

	public int getHandSize() {

		return hand.size();
	}

	public void setName(String n) {
		name = n;
	}

	public void checkCards() {
		if (this.getHand().size() > 11) {
			System.out.println("DO SOMETHING");
		} else {
			System.out.println("YOU GOOD");
		}
	}

	public void giveShields(int numShields) {

		this.addShields(numShields);
		/*if(Controller.isRigged) {
			if(this.shields>=game.getWinCondition()) {
				System.out.println("RIGGED GAME OVER, player acheived win condition");
				System.exit(0);
			} 
		} */
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
	
	public boolean isAI(){
		return !(this.ai == null);
	}
	
	public AI getAI(){
		return this.ai;
	}
	
	public void setAI(int type){
	    switch(type){
            case 2:
                this.ai = new Strategy2();
                break;
        }
    } 
} 
