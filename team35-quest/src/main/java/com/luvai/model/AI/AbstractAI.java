package com.luvai.model.AI;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.luvai.controller.SocketHandler;
import com.luvai.model.CardList;
import com.luvai.model.Game;
import com.luvai.model.Player;
import com.luvai.model.AdventureCards.AdventureCard;
import com.luvai.model.AdventureCards.AdventureCardComparator;
import com.luvai.model.AdventureCards.AllyCard;
import com.luvai.model.AdventureCards.AmourCard;
import com.luvai.model.AdventureCards.FoeCard;
import com.luvai.model.AdventureCards.TestCard;
import com.luvai.model.AdventureCards.WeaponCard;

public abstract class AbstractAI {
	private static final Logger logger = LogManager.getLogger(AbstractAI.class);
	public String Strategy_Type;
	Game gameEngine;
	ArrayList<WeaponCard> weaponsList;
	ArrayList<AllyCard> alliesList;
	ArrayList<AmourCard> amourList;
	ArrayList<FoeCard> foeList;
	ArrayList<TestCard> testList;
	int bidTracker;
	ArrayList<AdventureCard> bids;
	CardList cardFinder;

	// constructs
	public AbstractAI() {
		logger.info("Initiating new AI player");
		gameEngine = SocketHandler.gameEngine;
		cardFinder = new CardList();
	}

	// non abstract methods
	public void sortCards(Player current_player) {
		Player p = current_player;
		AdventureCard card;
		clearList();
		for (int i = 0; i < p.getHand().size(); i++) {
			card = p.getHand().get(i);

			if (card instanceof AmourCard) {
				amourList.add((AmourCard) card);
			}

			else if (card instanceof AllyCard) {
				alliesList.add((AllyCard) card);
			}

			else if (card instanceof WeaponCard) {
				weaponsList.add((WeaponCard) card);
			}

			else if (card instanceof FoeCard) {
				foeList.add((FoeCard) card);
			}

			else if (card instanceof TestCard) {
				testList.add((TestCard) card);
			}
		}
		Collections.sort(amourList, new AdventureCardComparator());
		Collections.sort(alliesList, new AdventureCardComparator());
		Collections.sort(weaponsList, new AdventureCardComparator());
		Collections.sort(foeList, new AdventureCardComparator());
		Collections.sort(testList, new AdventureCardComparator());
	}

	// clear all lists
	public void clearList() {
		foeList.clear();
		alliesList.clear();
		amourList.clear();
		weaponsList.clear();
		testList.clear();
	}

	public void printList() {
		for (FoeCard f : foeList) {
			System.out.println(f.getName());
		}
		for (AllyCard a : alliesList) {
			System.out.println(a.getName());
		}
		for (AmourCard a : amourList) {
			System.out.println(a.getName());
		}
		for (TestCard t : testList) {
			System.out.println(t.getName());
		}
		for (WeaponCard w : weaponsList) {
			System.out.println(w.getName());
		}

	}

	// abstract methods
	abstract public void setStrategyType();

	abstract public AdventureCard[] getDiscardChoice(Player currentPlayer, int numDiscards);

	abstract public boolean doIParticipateQuest();

	abstract public boolean doISponsorQuest();

	abstract public void setupQuest();

	abstract public ArrayList<AdventureCard> nextBid(JsonObject json);

	abstract public void discardAfterWinningTest();

	abstract public void chooseEquipment(JsonObject json, Player player);

	abstract public void chooseEquipmentTournie(JsonObject json, Player player);

	abstract public boolean doIParticipateTournament();

}
