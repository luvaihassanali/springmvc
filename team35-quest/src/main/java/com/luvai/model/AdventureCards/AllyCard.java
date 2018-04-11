package com.luvai.model.AdventureCards;

public class AllyCard extends AdventureCard {
	int battlePoints, bonusBp, bid, bonusBid;
	WeaponCard[] weapons;
	String specialQuest;

	public AllyCard(String name, String StringFile, int battlePoints, int bonusBp, int bid, int bonusBid,
			String specialQuest) {
		this.name = name;
		this.StringFile = StringFile;
		this.battlePoints = battlePoints;
		this.bonusBp = bonusBp;
		this.bid = bid;
		this.bonusBid = bonusBid;
		this.specialQuest = specialQuest;
	}

	public String getSpecialQuest() {
		return specialQuest;
	}

	public int getBonusBattlePoints() {
		return bonusBp;
	}

	public int getBid() {
		return bid;
	}

	public int getBonusBid() {
		return bonusBid;
	}

}
