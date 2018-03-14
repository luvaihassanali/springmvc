package com.luvai.controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.luvai.model.Card;
import com.luvai.model.CardList;
import com.luvai.model.Game;
import com.luvai.model.Player;
import com.luvai.model.PointArray;
import com.luvai.model.AdventureCards.AdventureCard;
import com.luvai.model.AdventureCards.AllyCard;
import com.luvai.model.AdventureCards.AmourCard;
import com.luvai.model.AdventureCards.FoeCard;
import com.luvai.model.AdventureCards.WeaponCard;
import com.luvai.model.StoryCards.QuestCard;

public class QuestController extends SocketHandler {
	private static final Logger logger = LogManager.getLogger(QuestController.class);
	Game gameEngine;
	Player sponsor;
	public ArrayList<Player> participants;
	QuestCard currentQuest;
	public ArrayList<FoeCard> QuestFoes;
	public FoeCard currentFoe;

	public QuestController(Game g, Player s, QuestCard q) throws IOException {
		logger.info("Initiating new quest {} sponsored by {}", q.getName(), s.getName());
		participants = new ArrayList<Player>();
		QuestFoes = new ArrayList<FoeCard>();
		gameEngine = g;
		sponsor = s;
		currentQuest = q;
		setupQuest();
	}

	// adjust player for points with chosen equipment
	public void equipPlayer(JsonObject json) {
		JsonElement player_equipment = json.get("equipment_info");
		Type listType = new TypeToken<List<String>>() {
		}.getType();
		List<String> equipmentList = new Gson().fromJson(player_equipment, listType);
		for (int i = 0; i < equipmentList.size(); i++) {
			AdventureCard tempCard = getCardFromName(equipmentList.get(i));
			if (tempCard instanceof AllyCard) {
				gameEngine.getActivePlayer().getAllies().add((AllyCard) tempCard);
			}
			if (tempCard instanceof WeaponCard) {
				gameEngine.getActivePlayer().getWeapons().add((WeaponCard) tempCard);
			}
			if (tempCard instanceof AmourCard) {
				gameEngine.getActivePlayer().setAmourCard(tempCard);
			}
		}
		calculatePlayerPoints();
	}

	// initiate foes for current quest
	public void initiateFoes(JsonObject json) {
		JsonElement quest_foes = json.get("foes");
		Type listType = new TypeToken<List<String>>() {
		}.getType();
		List<String> foeList = new Gson().fromJson(quest_foes, listType);
		// System.out.println(foeList);
		for (int k = 0; k < foeList.size(); k++) {
			AdventureCard tempCard = getCardFromName(foeList.get(k));
			QuestFoes.add((FoeCard) tempCard);
		}

		JsonArray quest_foe_weapons = json.getAsJsonArray("weapons");
		for (int i = 0; i < quest_foe_weapons.size(); i++) {
			JsonArray temp = (JsonArray) quest_foe_weapons.get(i);
			for (int j = 0; j < temp.size(); j++) {
				String x = temp.get(j).toString();
				x = x.replaceAll("\"", "");
				AdventureCard tempCard = getCardFromName(x);
				QuestFoes.get(i).weapons.add((WeaponCard) tempCard);
			}
		}
		/*		for (FoeCard f : QuestFoes) {
					System.out.println(f.getName());
					for (WeaponCard w : f.getWeapons()) {
						System.out.println(w.getName());
					}
				} */
		calculateFoeBattlePoints();
	}

	public void calculatePlayerPoints() {
		Player currentPlayer = gameEngine.getActivePlayer();
		int tempPts = currentPlayer.getBattlePoints();
		String cardNames = "";
		if (currentPlayer.getAmourCard() != null) {
			tempPts += 10;
			cardNames += currentPlayer.getAmourCard().getName() + "#";
		}
		for (WeaponCard w : currentPlayer.getWeapons()) {
			tempPts += w.getBattlePoints();
			cardNames += w.getName() + "#";
		}
		for (AllyCard a : currentPlayer.getAllies()) {
			tempPts += a.getBattlePoints();
			cardNames += a.getName() + "#";
			// ally bonus points
		}
		sendToAllSessions(gameEngine.players,
				"currentPlayerPoints" + tempPts + ";" + currentPlayer.getRank().getName() + ";" + cardNames);
	}

	public void calculateFoeBattlePoints() {
		int tempPts;
		PointArray foePoints = new PointArray(currentQuest.getStages() - 1);
		for (FoeCard f : QuestFoes) {
			tempPts = 0;
			if (currentQuest.getFoe().equals(f.getName()) || currentQuest.getFoe2().equals(f.getName())) {
				tempPts = f.getBonusBattlePoints();
			} else {
				tempPts = f.getBattlePoints();
			}
			for (WeaponCard w : f.getWeapons()) {
				tempPts += w.getBattlePoints();
			}
			foePoints.names.add(f.getName());
			foePoints.points.add(tempPts);
		}
		String temp = foePoints.toString();
		sendToAllSessions(gameEngine.players, "FoeInfo" + temp);
	}

	public AdventureCard getCardFromName(String name) {
		AdventureCard card = null;
		CardList cardList = new CardList();
		for (Card a : cardList.adventureTypes) {
			if (a.getName().equals(name)) {
				card = (AdventureCard) a;
				break;
			}
		}
		return card;

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
