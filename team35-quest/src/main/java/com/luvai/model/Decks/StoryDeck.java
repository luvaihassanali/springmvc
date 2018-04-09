package com.luvai.model.Decks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.luvai.controller.SocketHandler;
import com.luvai.model.CardList;
import com.luvai.model.StoryCards.StoryCard;

public class StoryDeck extends Decks {
	private static final Logger logger = LogManager.getLogger(StoryDeck.class);
	public Stack<StoryCard> cards = new Stack<StoryCard>();
	public StoryCard faceUp;
	public ArrayList<StoryCard> discardPile = new ArrayList<StoryCard>();

	public StoryDeck() {
		logger.info("Shuffling story deck...\n");
		this.initStoryDeck();
	}

	public void fillRiggedDeck(StoryCard[] s) {
		cardsLeft = 28;
		for (int i = 0; i < s.length; i++) {
			cards.push(s[i]);
		}
	}

	public void fillDeck(StoryCard[] s) {
		cardsLeft = 28;
		for (int i = 0; i < s.length; i++) {
			cards.push(s[i]);
		}
	}

	public void fillEmptyDeck() {
		logger.info("Story card deck has run out");
		logger.info("Shuffling story card discard pile and replacing story deck");
		Collections.shuffle(discardPile);
		System.out.println(discardPile.size());
		System.out.println(cards.size());
		cards.addAll(discardPile);
		discardPile.clear();
		System.out.println(discardPile.size());
		System.out.println(cards.size());
		//
		logger.info("STORY DECK PROOF:");
		int j = 1;
		for (StoryCard s : cards) {
			logger.info("{}. {}", j, s.getName());
			j++;
		}

	}

	public StoryDeck initStoryDeck() {
		StoryCard[] s_arr = new StoryCard[28];
		// mimic story array for freq/randomization
		s_arr[0] = CardList.Quest9;
		s_arr[1] = CardList.Quest10;
		s_arr[2] = CardList.Quest2;
		s_arr[3] = CardList.Quest4;
		s_arr[4] = CardList.Quest8;
		s_arr[5] = CardList.Quest1;
		s_arr[6] = CardList.Quest3;
		s_arr[7] = CardList.Quest3;
		s_arr[8] = CardList.Quest7;
		s_arr[9] = CardList.Quest6;
		s_arr[10] = CardList.Quest6;
		s_arr[11] = CardList.Quest5;
		s_arr[12] = CardList.Quest5;
		s_arr[13] = CardList.Tournament1;
		s_arr[14] = CardList.Tournament2;
		s_arr[15] = CardList.Tournament3;
		s_arr[16] = CardList.Tournament4;
		s_arr[17] = CardList.Event7;
		s_arr[18] = CardList.Event7;
		s_arr[19] = CardList.Event3;
		s_arr[20] = CardList.Event3;
		s_arr[21] = CardList.Event2;
		s_arr[22] = CardList.Event2;
		s_arr[23] = CardList.Event4;
		s_arr[24] = CardList.Event5;
		s_arr[25] = CardList.Event1;
		s_arr[26] = CardList.Event8;
		s_arr[27] = CardList.Event6;
		// randomise then populate stack
		Collections.shuffle(Arrays.asList(s_arr));
		this.fillDeck(s_arr); // final story deck set here
		return this;
	}

	public StoryDeck initRiggedStoryDeck(int deckId) {
		logger.info("Initiating rigged story deck");
		cards.clear();
		if (deckId == 42 || deckId == 43) {
			StoryCard[] s_arr = new StoryCard[23];
			// mimic story array for freq/randomization
			s_arr[0] = CardList.Quest9;
			s_arr[1] = CardList.Quest10;
			s_arr[2] = CardList.Quest2;
			s_arr[3] = CardList.Quest4;
			s_arr[4] = CardList.Quest8;
			s_arr[5] = CardList.Quest1;
			s_arr[6] = CardList.Quest3;
			s_arr[7] = CardList.Quest3;
			s_arr[8] = CardList.Quest6;
			s_arr[9] = CardList.Quest5;
			s_arr[10] = CardList.Quest5;
			s_arr[11] = CardList.Tournament2;
			s_arr[12] = CardList.Tournament3;
			s_arr[13] = CardList.Tournament4;
			s_arr[14] = CardList.Event7;
			s_arr[15] = CardList.Event7;
			s_arr[16] = CardList.Event3;
			s_arr[17] = CardList.Event3;
			s_arr[18] = CardList.Event2;
			s_arr[19] = CardList.Event2;
			s_arr[20] = CardList.Event4;
			s_arr[21] = CardList.Event5;
			s_arr[22] = CardList.Event6;
			// randomise then populate stack
			Collections.shuffle(Arrays.asList(s_arr));
			StoryCard[] s_arr2 = new StoryCard[5];
			if (deckId == 43) {
				s_arr2[4] = CardList.Tournament2; // should be 6
				s_arr2[3] = CardList.Event1;// should be event 1
				s_arr2[2] = CardList.Event8;// event 8
				s_arr2[1] = CardList.Quest6; // tournament 1
				s_arr2[0] = CardList.Tournament1; // quest7
			} else {
				s_arr2[4] = CardList.Quest6; // should be 6
				s_arr2[3] = CardList.Event1;// should be event 1
				s_arr2[2] = CardList.Event8;// event 8
				s_arr2[1] = CardList.Tournament1; // tournament 1
				s_arr2[0] = CardList.Quest7; // quest7
				;
			}
			this.fillDeck(s_arr);
			this.fillDeck(s_arr2);
		}

		return this;
	}

	boolean sendOnce = true;

	public void flipCard() {

		if (this.faceUp == null) {
			this.faceUp = this.cards.pop();
		} else {
			this.discardPile.add(this.faceUp);
			if (sendOnce) {
				SocketHandler.sendToAllSessions(SocketHandler.gameEngine, "setBack");
				sendOnce = false;
			}
			if (this.cards.isEmpty()) {
				this.fillEmptyDeck();
				SocketHandler.sendToAllSessions(SocketHandler.gameEngine, "clearBack");
				sendOnce = true;
			}
			this.faceUp = this.cards.pop();
		}
	}

}