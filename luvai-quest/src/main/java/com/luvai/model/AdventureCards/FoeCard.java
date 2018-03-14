package com.luvai.model.AdventureCards;

import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.luvai.model.StoryCards.QuestCard;

public class FoeCard extends AdventureCard {
	int battlePoints, bonusBp;
	public ArrayList<WeaponCard> weapons;
	public QuestCard questCard;

	// Boar = new FoeCard("Boar", boarImage, 5, 15, CardList.Quest6);
	public FoeCard(String name, String StringFile, int battlePoints, int bonusBp, QuestCard q) {
		this.name = name;
		this.weapons = new ArrayList<WeaponCard>();
		this.StringFile = StringFile;
		this.battlePoints = battlePoints;
		this.bonusBp = bonusBp;
		questCard = q;
	}

	public FoeCard(String name, String StringFile, int battlePoints, QuestCard q) {
		this.name = name;
		this.weapons = new ArrayList<WeaponCard>();
		this.StringFile = StringFile;
		this.battlePoints = battlePoints;
		this.bonusBp = 0;
		questCard = q;
	}

	public void equipWeapons(JsonObject json) {

	}

	public int getBattlePoints() {
		return battlePoints;
	}

	public int getBonusBattlePoints() {
		return bonusBp;
	}

	public QuestCard getQuestCard() {
		return questCard;
	}

	public ArrayList<WeaponCard> getWeapons() {
		return weapons;
	}

}