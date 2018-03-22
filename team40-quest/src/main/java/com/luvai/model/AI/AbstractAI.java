package com.luvai.model.AI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	abstract public void doIParticipate();

}
