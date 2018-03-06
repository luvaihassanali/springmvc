package com.team62.spring.model.decks;

import com.team62.spring.model.CardList;
import com.team62.spring.model.RankCard;

public class RankDeck extends Decks {
	
	public RankCard[] rankArray;
	public RankCard[] discardPile; 
	public  RankDeck() { 
		
	}
	
	public void fillDeck(RankCard[] r) {
		cardsLeft = 8;
		for(int i=0; i<r.length; i++) {
			r[i] = rankArray[i]; 
		}
	}
	
	public RankDeck initRankDeck(RankDeck r) {
		RankCard[] r_arr = new RankCard[12];
	    //using array instead of stack because we need to pick and choose rank cards	    
	    r_arr[0] = CardList.Squire; r_arr[1] = CardList.Squire; r_arr[2] = CardList.Squire; r_arr[3] = CardList.Squire;
	    r_arr[4] = CardList.Knight; r_arr[5] = CardList.Knight; r_arr[6] = CardList.Knight; r_arr[7] = CardList.Knight;
	    r_arr[8] = CardList.ChampionKnight; r_arr[9] = CardList.ChampionKnight; r_arr[10] = CardList.ChampionKnight; r_arr[11] = CardList.ChampionKnight;
	    r.rankArray= r_arr; //rank deck final set here
	    return r;
	}

	
}