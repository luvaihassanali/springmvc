package com.luvai.model.AI;

//import com.luvai.model.Card;
import com.luvai.model.AdventureCards.*;
//import com.luvai.model.StoryCards.QuestCard;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
//import java.util.Collections;

//import static com.team62.spring.modelGame;

public class Strategy2 extends AI {
    // private static final Logger log = LogManager.getLogger(Strategy2.class);

    public Strategy2() {
        weaponsList = new ArrayList<WeaponCard>();
        alliesList = new ArrayList<AllyCard>();
        amourList = new ArrayList<AmourCard>();
        foeList = new ArrayList<FoeCard>();
        stageCards = new ArrayList<ArrayList<AdventureCard>>();
    }
/*
    //does not deal with name quests
     protected boolean setUpStages(int stages){
        //store the totals for each stage
        ArrayList<Integer> totals = new ArrayList<Integer>(stages);
        Collections.fill(totals, 0);
        int battlePoints = 0;
        int stageIndex = stages -1;

       for(int i = 0; i < stages; i++){
           ArrayList<AdventureCard> list = new ArrayList<AdventureCard>();
           stageCards.add(list);
       }

       //add the strongest foe to the last stage
         //add its battle points to the stage total
       battlePoints += foeList.get(foeList.size() -1).getBattlePoints();

       stageCards.get(stageIndex).add(foeList.remove(foeList.size() -1));


       // add weapons to reach 40 bp
         for(int i = weaponsList.size() - 1; i >= 0; i--){
            boolean duplicate = false;

            //can't add two of the same type
            for(AdventureCard c : stageCards.get(stageIndex)){
                if (c.getName().equals(weaponsList.get(i).getName())){duplicate = true;}
                }
            if(!duplicate) {
                battlePoints += weaponsList.get(i).getBattlePoints();
                stageCards.get(stageIndex).add(weaponsList.remove(i));
            }
            if(battlePoints >= 40) break;
       }

       //false if cards not good enough
       if(battlePoints < 40) {
             log.info("Cannot make a final stage with >= 40 battle points");
             return false;
       }

       //make early stages with 1 foe increasing in bp
         for(int i = 0; i < stages - 1; i++){
             stageCards.get(0).add(foeList.remove(i));
         }

       printStageCards(stages);
        return true;
     }

     public void printStageCards(int stages){
        for(int i = 0; i < stageCards.size(); i++) {
            log.info("Stage {} cards:", i);

            for (AdventureCard c : stageCards.get(i)) {
                log.info("{} with {} bp" , c.getName(), c.getBattlePoints());
            }
        }
     }

     //has two foes < 25
    //can increment by 10 each stage
     public boolean doIParticipateInQuest(){
        int stages = ((QuestCard)game.s_deck.faceUp).getStages();
        splitAndSort();

        for(int i = 0; i < stages; i++){
           ArrayList<AdventureCard> list = new ArrayList<AdventureCard>();
           stageCards.add(list);
       }

        //if the two smallest foes are not less than 25
        if(!(foeList.get(0).getBattlePoints() < 25 && foeList.get(1).getBattlePoints() < 25)) {
            log.info("Hand does not contain two foes less than 25");
            return false;
        }

        log.info("Hand contains two foes less than 25 for a test");

        if(!checkIncrements(stages)){
            log.info("Hand cannot increment by 10 for each stage");
            return false;
        }
        log.info("Hand can increment by 10 for each stage");
        return true;
     }

     //Check if its possible to increment hand strength by 10 each stage
    //TODO:****Need to account for allies****
    private boolean checkIncrements(int stages) {
         ArrayList<AdventureCard> cards = new ArrayList<AdventureCard>(stages);

        int stageIndex = stages -1;



        //make increments of > 10
        for(int i = 0; i < stages; i++){
            if(makeNormalStage(i).size() == 0) return false;
        }

        return true;
    }

    //>=10 points more than the last stage
    public ArrayList<AdventureCard> makeNormalStage(int stage){
        int battlePoints = 0;
        //add amour when implemented
        boolean amour = false;
        for (AdventureCard c : stageCards.get(stage)) {
            if (c.getName().equals("Amour")) amour = true;
        }

      if (!amour && amourList.size() > 0) {
          battlePoints += amourList.get(0).getBattlePoints();
          stageCards.get(stage).add(amourList.remove(0));
          return stageCards.get(stage);
      }

      //allies
        // ingores bonuses
        for (int i = 0; i < alliesList.size(); i++) {
            if (alliesList.get(i).getBattlePoints() > 0) {
                battlePoints += alliesList.get(i).getBattlePoints();
                stageCards.get(stage).add(alliesList.remove(0));
            }
            if(battlePoints >= 10) return stageCards.get(stage);
        }

        //weapons
        for (int i = weaponsList.size() - 1; i >= 0; i--) {
            boolean duplicate = false;

            //can't add two of the same type
            for (AdventureCard c : stageCards.get(stage)) {
                if (c.getName().equals(weaponsList.get(0).getName())) {
                duplicate = true;
                }
            }
            if (!duplicate) {
                battlePoints += weaponsList.get(0).getBattlePoints();
                stageCards.get(stage).add(weaponsList.remove(0));
            }
            if(battlePoints >= 10) return stageCards.get(stage);
        }

       return null;
    }

    // Makes strongest availible hand for the last stage of a quest.
    public int makeStrongestCombo(int stages){
        int battlePoints = 0;
        int stageLast = stages - 1;

        //weapons
         for(int i = weaponsList.size() - 1; i >= 0; i--){
            boolean duplicate = false;

            //can't add two of the same type
            for(AdventureCard c : stageCards.get(stageLast)){
                if (c.getName().equals(weaponsList.get(i).getName())){duplicate = true;}
                }
            if(!duplicate) {
                battlePoints += weaponsList.get(i).getBattlePoints();
                stageCards.get(stageLast).add(weaponsList.remove(i));
            }
       }
       //allies
        // ingores bonuses
        for(int i = 0; i < alliesList.size(); i++){
            if(alliesList.get(i).getBattlePoints() > 0){
                battlePoints += alliesList.get(i).getBattlePoints();
                stageCards.get(stageLast).add(alliesList.remove(i));
            }
        }

       return battlePoints;
    }

      public AdventureCard getDiscardChoice(){
        splitAndSort();
        //check foes < 20
        for(AdventureCard c: foeList){
            if (c.getBattlePoints() < 20){
                return c;
            }
        }
        //check for duplicates
          if(amourList.size() > 1){
              System.out.println("amourlist.size()" + amourList.size());
            return amourList.get(0);
          }
        System.out.println("ac amour");
          //weapon duplicates
          for(int i = 0; i < weaponsList.size(); i++){
            WeaponCard c1 = weaponsList.get(i);

            for(int j = i+1; j < weaponsList.size(); i++){
                if (c1.getName().equals(weaponsList.get(j).getName())){
                    return weaponsList.get(i);
                }
            }
       }
       //else card[0]
          return game.getActivePlayer().getHand().get(0);
    }*/
}
