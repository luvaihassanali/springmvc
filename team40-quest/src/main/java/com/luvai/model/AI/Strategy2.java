package com.luvai.model.AI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Strategy2 extends AbstractAI {
	private static final Logger logger = LogManager.getLogger(Strategy2.class);

	public Strategy2() {
		setStrategyType();
		logger.info("Assigning new AI Player strategy {}", this.Strategy_Type);
	}

	@Override
	public void doIParticipate() {
		// TODO Auto-generated method st

	}

	@Override
	public void setStrategyType() {
		this.Strategy_Type = "Strategy2";

	}

}
