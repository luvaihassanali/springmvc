package com.luvai.model.AI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.luvai.model.Player;
import com.luvai.model.AdventureCards.AdventureCard;

public abstract class AbstractAI {
	private static final Logger logger = LogManager.getLogger(AbstractAI.class);
	public String Strategy_Type;

	// constructs
	public AbstractAI() {
		logger.info("Initiating new AI player");

	}

	// non abstract methods

	// abstract methods
	abstract public void setStrategyType();

	abstract public AdventureCard[] getDiscardChoice(Player currentPlayer, int numDiscards);

	abstract public boolean doIParticipateQuest();

}
