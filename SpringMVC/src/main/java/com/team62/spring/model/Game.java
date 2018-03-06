package com.team62.spring.model;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.team62.spring.model.decks.AdventureDeck;
import com.team62.spring.model.decks.StoryDeck;

public class Game {
	private static final Logger logger = LogManager.getLogger(Game.class);
	public ArrayList<Player> players;
	public StoryDeck storyDeck;
    public AdventureDeck adventureDeck;
	
	public Game() {
		logger.info("Initialising new game");
		players = new ArrayList<Player>();
		adventureDeck = new AdventureDeck();
		storyDeck = new StoryDeck();
	}
}

