package com.team62.spring.model.adventureCards;

public class TestCard extends AdventureCard {
	int minBid;
	
	public TestCard(String name, String StringFile, int minBid) {
		this.name = name;
		this.StringFile = StringFile;
		this.minBid = minBid;
	}
	
	public int getBattlePoints(){
		return -1;
	}
}
