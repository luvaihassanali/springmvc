package com.team62.spring.model;

import java.util.ArrayList;

import com.team62.spring.model.decks.AdventureDeck;
import com.team62.spring.model.decks.StoryDeck;

public class Game {
	
	public ArrayList<Player> players;
	public StoryDeck storyDeck;
    public AdventureDeck adventureDeck;
	
	public Game() {
		players = new ArrayList<Player>();
		adventureDeck = new AdventureDeck();
		adventureDeck.initAdventureDeck();
	}
}

