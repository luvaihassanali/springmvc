package com.luvai.model.AI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.luvai.model.Player;
import com.luvai.model.AdventureCards.AdventureCard;

public class Strategy2 extends AbstractAI {
	private static final Logger logger = LogManager.getLogger(Strategy2.class);

	public Strategy2() {

		setStrategyType();
		logger.info("Assigning new AI Player strategy {}", this.Strategy_Type);
	}

	@Override
	public boolean doIParticipateQuest() {
		// to:do once implemented test
		return false;

	}

	@Override
	public void setStrategyType() {
		this.Strategy_Type = "Strategy2";

	}

	// to-do IMPLEMENT THIS SHIT
	@Override
	public AdventureCard[] getDiscardChoice(Player currentPlayer, int numDiscards) {
		AdventureCard[] discards = new AdventureCard[numDiscards];
		for (int i = 0; i < discards.length; i++) {
			discards[i] = currentPlayer.getHand().get(i);
		}
		return discards;
	}
}
