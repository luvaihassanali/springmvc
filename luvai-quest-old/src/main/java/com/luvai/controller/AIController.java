package com.luvai.controller;

import java.util.ArrayList;

import com.luvai.model.Card;
import com.luvai.model.Game;
import com.luvai.model.Player;

public class AIController {
	static Game game;

	public static void discardFromHand(ArrayList<Card> played, Player p) {
		for (Card c : played) {
			for (int i = 0; i < p.getHand().size(); i++) {
				if (c.getName().equals(p.getHand().get(i).getName())) {
					game.adventureDeck.discardPile.add(p.getHand().remove(i));
				}
			}
		}
	}

	public static void discardFromHand(Card c, Player p) {
		for (int i = 0; i < p.getHand().size(); i++) {
			if (c.getName().equals(p.getHand().get(i).getName())) {
				game.adventureDeck.discardPile.add(p.getHand().remove(i));
			}
		}
	}
}
