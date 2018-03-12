package com.luvai.model.Decks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.luvai.model.CardList;
import com.luvai.model.AdventureCards.AdventureCard;

public class AdventureDeck extends Decks {
	private static final Logger logger = LogManager.getLogger(AdventureDeck.class);
	public Stack<AdventureCard> cards = new Stack<AdventureCard>();
	public AdventureCard faceUp; 
	public ArrayList<AdventureCard> discardPile = new ArrayList<AdventureCard>(); 
	public  AdventureDeck() { 
		this.initAdventureDeck();
		logger.info("Shuffling adventure deck...");
	}
	
	public void fillDeck(AdventureCard[] a) {
		cardsLeft = 125;
		for(int i=0; i<a.length; i++) {
			cards.push(a[i]);
		}
	}
	
	public AdventureCard flipCard() {
		if(this.cards.isEmpty()) this.fillEmptyDeck();
		if(this.faceUp==null) {
		this.faceUp = this.cards.pop();
		} else { 
			discardPile.add(faceUp);
			faceUp = this.cards.pop();			
		}
		return this.faceUp;
	}
	
	public void fillEmptyDeck() {
		logger.info("Reshuffling story deck...");
		this.initAdventureDeck();
	}
	
	public AdventureDeck initAdventureDeck() {
		AdventureCard[] a_arr = new AdventureCard[126];
	    //create a mimic adventure deck using array to set card frequency and to randomize 
	    a_arr[0] = CardList.Excalibur; a_arr[1] = CardList.Excalibur; 
	    for(int i=2; i<8; i++) { a_arr[i] = CardList.Lance; };
	    for(int i=8; i<16; i++) { a_arr[i] = CardList.Battleax; };
	    for(int i=16; i<32; i++) { a_arr[i] = CardList.Sword; };
	    for(int i=32; i<43; i++) { a_arr[i] = CardList.Horse; };
	    for(int i=43; i<50; i++) { a_arr[i] = CardList.Dagger; };
	    a_arr[50] = CardList.Dragon; a_arr[51] = CardList.Giant; a_arr[52] = CardList.Giant;
	    for(int i=53; i<58; i++) { a_arr[i] = CardList.Mordred; };
	    a_arr[58] = CardList.GreenKnight; a_arr[59] = CardList.GreenKnight; 
	    for(int i=59; i<63; i++) { a_arr[i] = CardList.BlackKnight; };
	    for(int i=63; i<69; i++) { a_arr[i] = CardList.EvilKnight; };
	    for(int i=69; i<78; i++) { a_arr[i] = CardList.SaxonKnight; };
	    for(int i=78; i<86; i++) { a_arr[i] = CardList.RobberKnight; };
	    for(int i=86; i<92; i++) { a_arr[i] = CardList.Saxons; }; 
	    for(int i=95; i<92; i++) { a_arr[i] = CardList.Boar; };
	    for(int i=92; i<100; i++) { a_arr[i] = CardList.Thieves; };
	    a_arr[100] = CardList.ValorTest; a_arr[101] = CardList.ValorTest;
	    a_arr[102] = CardList.TemptationTest; a_arr[103] = CardList.TemptationTest;
	    a_arr[104] = CardList.MorganTest; a_arr[105] = CardList.MorganTest;
	    a_arr[106] = CardList.BeastTest; a_arr[107] = CardList.BeastTest;
	    a_arr[108] = CardList.SirGalahad; a_arr[109] = CardList.SirLancelot; a_arr[110] = CardList.KingArthur;
	    a_arr[111] = CardList.SirTristan; a_arr[112] = CardList.SirPellinore; a_arr[113] = CardList.SirGawain;
	    a_arr[114] = CardList.SirPercival; a_arr[115] = CardList.QueenGuinevere; a_arr[116] = CardList.QueenIseult;
	    a_arr[117] = CardList.Merlin;
	    for(int i=118; i<a_arr.length; i++) { a_arr[i] = CardList.Amour; };
	    //randomise - then populate stack 
	    Collections.shuffle(Arrays.asList(a_arr));
	    this.fillDeck(a_arr); //final adventure deck set here
		return this;		
	}
}