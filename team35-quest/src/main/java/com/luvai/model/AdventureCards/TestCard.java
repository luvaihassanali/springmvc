package com.luvai.model.AdventureCards;

public class TestCard extends AdventureCard {
	int minBid;

	public TestCard(String name, String StringFile, int minBid) {
		this.name = name;
		this.StringFile = StringFile;
		this.minBid = minBid;
	}

	public int getBattlePoints() {
		return -1;
	}

	public int getMinBid() {
		return this.minBid;
	}
}
