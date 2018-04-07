package com.luvai.model.AdventureCards;

public class AllyCard extends AdventureCard {
	int battlePoints, bonusBp, bid, bonusBid;
	WeaponCard[] weapons;

	public AllyCard(String name, String StringFile, int battlePoints, int bonusBp, int bid, int bonusBid) {
		this.name = name;
		this.StringFile = StringFile;
		this.battlePoints = battlePoints;
		this.bonusBp = bonusBp;
		this.bid = bid;
		this.bonusBid = bonusBid;
	}

	public int getBattlePoints() {
		return battlePoints;
	}

	public int getBonusBattlePoints() {
		return bonusBp;
	}

	public int getBid() {
		return bid;
	}

}
