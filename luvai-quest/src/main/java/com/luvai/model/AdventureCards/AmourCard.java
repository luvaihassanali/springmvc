package com.luvai.model.AdventureCards;

public class AmourCard extends AdventureCard {
	int battlePoints, bid;
	boolean equipped = false;
	
	public AmourCard(String name, String StringFile, int battlePoints, int bid){
		this.name = name;
		this.StringFile = StringFile;
		this.battlePoints = battlePoints;
		this.bid = bid;
	}
	
	public int getBattlePoints(){
		return battlePoints; 
		}
	
	int getBid(){
		return bid; 
		}

}