package com.team62.spring.model.AI;

import com.team62.spring.model.AdventureCardComparator;
import com.team62.spring.model.Card;
import com.team62.spring.model.Player;
import com.team62.spring.model.adventureCards.*;
import com.team62.spring.model.Game;
import com.team62.spring.model.storyCards.QuestCard;
import com.team62.spring.websocket.SocketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import static java.util.Collections.sort;

public abstract class AI {
    private static final Logger log = LogManager.getLogger(AI.class);
    ArrayList<WeaponCard> weaponsList;
    ArrayList<AllyCard> alliesList;
    ArrayList<AmourCard> amourList;
	ArrayList<FoeCard> foeList;
	ArrayList<ArrayList<AdventureCard>> stageCards;
	
	/*Game game = SocketHandler.gameEgnine;
	
    //TODO: update when tests become a thing
    //checks for potential ranking up of other players and if the AI has the right number/strength
    //of cards to set up a quest
    
    public boolean DoISponsorAQuest(){
        int stages = ((QuestCard)Game.s_deck.faceUp).getStages();
        splitAndSort();
        
        if(canAnyrank(stages)){
            return false;
        }
        else if(!checkFoes(stages)) {
            log.info("Insufficient foe cards");
            return false;
        }else
            return true;
    }
    
    //checks if gaining a number of shields would cause a player to rank up
    protected boolean canRankUp(Player player, int reward){
        int shieldTot = player.getShields();
        
        for(int i = 0; i < reward; i++){
            shieldTot++;
            if(shieldTot == 5 || shieldTot == 12 || shieldTot == game.getWinCondition()){
                return true;
            }
        }
        return false;
    }
    
    //loops through the player list and checks for canRankUp. Bugged if two players have the same name
    protected boolean canAnyrank(int reward) {
        for (int i = 0; i < game.playerList.size(); i++) {
            if (canRankUp(game.playerList.get(i), reward) && (game.playerList.get(i).getName() != game.getActivePlayer().getName())) {
                log.info("Player rank up is possible");
                return true;
            }
        }
        log.info("No player can rank up");
        return false;
    }
    
    protected boolean setUpStages(int stages){
        return true;
    }
    
    //checks for the correct amount of foes
    //check that foes can increase in strength each round
    //checking for proper weapon strength is polymorphic and will be implemented by each AI
    protected boolean checkFoes(int stages){
        int strengths = 0;

        if (foeList.size() < stages) return false;
        
        for(int i = 1; i < foeList.size(); i++){
            if(foeList.get(i).getBattlePoints() > foeList.get(i - 1).getBattlePoints()) strengths++;
        }
        
        add comment markers - check if we can apply a stronger foe at each stage. Not fucking dealing
          with weapons in this pr.
          
        if(strengths < stages) return false;
        
        setUpStages(stages);
        return true;
    }
    
    protected void printLists(){
        for(int i = 0; i< foeList.size(); i++){
            System.out.println(foeList.get(i).getName());
        }
         for(int i = 0; i< weaponsList.size(); i++){
            System.out.println(weaponsList.get(i).getName());
        }
         for(int i = 0; i< alliesList.size(); i++){
            System.out.println(alliesList.get(i).getName());
        }
    }
    
    //creates lists for each adventure Card type
    //sort each list by increasing battlePoints
    public void splitAndSort() {
        Player p = game.getActivePlayer();
        AdventureCard card;
        clearLists();
        for (int i = 0; i < p.getHand().size(); i++){
            card = p.getHand().get(i);
            
           if (card instanceof AllyCard) {
               alliesList.add((AllyCard) card);
            }
    
            else if (card instanceof WeaponCard) {
                weaponsList.add((WeaponCard) card);
            }
           
            else if (card instanceof FoeCard) {
                foeList.add((FoeCard) card);
            }
        }
        Collections.sort(amourList, new AdventureCardComparator());
        Collections.sort(alliesList, new AdventureCardComparator());
        Collections.sort(weaponsList, new AdventureCardComparator());
        Collections.sort(foeList, new AdventureCardComparator());
    }
    
     public boolean doIParticipateInQuest(){
        return false;
    }
    
    //to be overriden
    protected ArrayList<AdventureCard> makeNormalStage(int stage){
        return null;
    }
       public int makeStrongestCombo(int stages){
        return 1;
       }
    
     //clear all lists
    public void clearLists(){
        foeList.clear();
        alliesList.clear();
        amourList.clear();
        weaponsList.clear();
        stageCards.clear();
    }
    
    //return an array of cards for a quest stage. Right now is used for sponsor setup
    public ArrayList<AdventureCard> getStage(int i){
        return this.stageCards.get(i);
    }
    
    public AdventureCard getDiscardChoice(){
        return null;
    } */
} 
