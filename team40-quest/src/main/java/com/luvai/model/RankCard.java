package com.luvai.model;

import com.luvai.model.Card;

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