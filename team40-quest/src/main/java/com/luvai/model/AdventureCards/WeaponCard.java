package com.luvai.model.AdventureCards;

public class WeaponCard extends AdventureCard {

	int battlePoints;

	public WeaponCard(String name, String StringFile, int battlePoints) {
		this.name = name;
		this.StringFile = StringFile;
		this.battlePoints = battlePoints;
	}

	public int getBattlePoints() {
		return battlePoints;
	}

	public String toString() {
		return this.name;
	}

}
