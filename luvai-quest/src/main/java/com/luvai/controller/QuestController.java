package com.luvai.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.luvai.model.Game;
import com.luvai.model.Player;
import com.luvai.model.StoryCards.QuestCard;

public class QuestController extends SocketHandler {
	private static final Logger logger = LogManager.getLogger(QuestController.class);
	Game gameEngine;
	Player sponsor;
	public ArrayList<Player> participants;
	QuestCard currentQuest;

	public QuestController(Game g, Player s, QuestCard q) throws IOException {
		logger.info("Initiating new quest {} sponsored by {}", q.getName(), s.getName());
		participants = new ArrayList<Player>();
		gameEngine = g;
		sponsor = s;
		currentQuest = q;
		setupQuest();
	}

	public void setupQuest() throws IOException {
		logger.info("{} is setting up stages for {} quest", this.sponsor.getName(), this.currentQuest.getName());
		sendToAllSessionsExceptCurrent(gameEngine, sponsor.session, "QuestBeingSetup");
	}

	public ArrayList<Player> getParticipants() {
		return this.participants;
	}

	public void addParticipant(Player player) {
		this.participants.add(player);
	}

}
