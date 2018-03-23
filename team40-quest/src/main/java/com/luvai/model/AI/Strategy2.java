package com.luvai.model.AI;

import java.util.ArrayList;

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
		ArrayList<AdventureCard> toDiscard = new ArrayList<AdventureCard>();

		for (AdventureCard a : currentPlayer.getHand()) {
			if (toDiscard.size() == 2)
				break;
			if (a.getBattlePoints() == 0)
				if (toDiscard.contains(a)) {
				} else {
					toDiscard.add(a);
				}
			if (a.getBattlePoints() == -1)
				if (toDiscard.contains(a)) {
				} else {
					toDiscard.add(a);
				}
			if (a.getBattlePoints() < 6)
				if (toDiscard.contains(a)) {
				} else {
					toDiscard.add(a);
				}
		}

		AdventureCard[] test = new AdventureCard[toDiscard.size()];
		toDiscard.toArray(test);

		for (int i = 0; i < numDiscards; i++) {
			discards[i] = test[i];
		}

		return discards;
	}
}
