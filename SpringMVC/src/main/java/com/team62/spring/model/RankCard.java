package com.team62.spring.model;

public class RankCard extends Card{
	int battlePoints;
   
	public RankCard(String name, String StringFile, int battlePoints) {
		this.name = name;
		this.StringFile = StringFile;
		this.battlePoints = battlePoints;
	}
	public int getBattlePoints(){
		return battlePoints;
	}
}