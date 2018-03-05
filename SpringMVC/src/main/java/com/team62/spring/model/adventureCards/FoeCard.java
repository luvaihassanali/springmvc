package com.team62.spring.model.adventureCards;

import java.util.ArrayList;

import com.team62.spring.model.storyCards.QuestCard;


public class FoeCard extends AdventureCard { 
	int battlePoints, bonusBp;
	public ArrayList<WeaponCard> weapons;
	public QuestCard questCard;
	
	public FoeCard() {
    }
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
	
	public QuestCard getQuest() {
		return this.questCard;
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