package com.luvai.model;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.luvai.model.Player;
import com.luvai.model.AdventureCards.AdventureCard;
import com.luvai.model.Decks.AdventureDeck;
import com.luvai.model.Decks.StoryDeck;

public class Game {
	private static final Logger logger = LogManager.getLogger(Game.class);
	public ArrayList<Player> players;
	public StoryDeck storyDeck;
    public AdventureDeck adventureDeck;
	public ArrayList<Player> participants;
	public Player sponsor;
	private int winCondition;
	
	public Game() {
		logger.info("Initialising new game");
		players = new ArrayList<Player>();
		
		adventureDeck = new AdventureDeck();
		storyDeck = new StoryDeck();
	}
	
	@SuppressWarnings("serial")
	public ArrayList<AdventureCard> mockHand1 = new ArrayList<AdventureCard>() {
		{
			add(CardList.Boar);
			add(CardList.BlackKnight);
			add(CardList.BeastTest);
			add(CardList.Dagger);
			add(CardList.Amour);
			add(CardList.Horse);
			add(CardList.Saxons);
			add(CardList.Merlin);
			add(CardList.Sword);
			add(CardList.TemptationTest);
			add(CardList.Sword);
			add(CardList.SirGawain);
		}
	};
	@SuppressWarnings("serial")
	public ArrayList<AdventureCard> mockHand2 = new ArrayList<AdventureCard>() {
		{
			add(CardList.SirTristan);
			add(CardList.MorganTest);
			add(CardList.Amour);
			add(CardList.RobberKnight);
			add(CardList.Merlin);
			add(CardList.Horse);
			add(CardList.Sword);
			add(CardList.Horse);
			add(CardList.Battleax);
			add(CardList.GreenKnight);
			add(CardList.Lance);
			add(CardList.SirLancelot);
		}
	};
	@SuppressWarnings("serial")
	public ArrayList<AdventureCard> mockHand3 = new ArrayList<AdventureCard>() {
		{
			add(CardList.BeastTest);
			add(CardList.Excalibur);
			add(CardList.Giant);
			add(CardList.EvilKnight);
			add(CardList.KingArthur);
			add(CardList.Excalibur);
			add(CardList.Horse);
			add(CardList.Horse);
			add(CardList.Dragon);
			add(CardList.BlackKnight);
			add(CardList.QueenGuinevere);
			add(CardList.SirGalahad);
		}
	};
	@SuppressWarnings("serial")
	public ArrayList<AdventureCard> mockHand4 = new ArrayList<AdventureCard>() {
		{
			add(CardList.KingArthur);
			add(CardList.GreenKnight);
			add(CardList.Saxons);
			add(CardList.SaxonKnight);
			add(CardList.Lance);
			add(CardList.QueenIseult);
			add(CardList.MorganTest);
			add(CardList.ValorTest);
			add(CardList.Excalibur);
			add(CardList.SirPellinore);
			add(CardList.Battleax);
			add(CardList.SirLancelot);
		}
	};
	
	public ArrayList<Player> getParticipants() {
		return participants;
	}
	
	public void addParticipant(Player player){
		this.participants.add(player);
	}

	public void setName(Player p, String s) {
		p.setName(s);
	}

	public void getActivePlayer() {
		//return players.get(SocketHandler.numTurns % players.size());
	}

	public void getPrevPlayer() {
		//return players.get((SocketHandler.numTurns % players.size())-1);
	}
	
    //convenience method because I think this will get called a lot
    public void isActiveAI(){
	//	return (this.getActivePlayer().isAI());
	}
	
	public int getWinCondition() {
		return winCondition;
	}
	
	public void setWinCondition(int winCondition) {
		this.winCondition = winCondition;
	}
	
	public void checkForTie() {
		//loop through player list checking for tieCheck = 1 (player var set after champ knight achieved)
       //keep track of # of tieCheck and if 1 ---> end of game screen sole winner
	 //if +1 then ---> end of game screen tie?
	}
	

}

