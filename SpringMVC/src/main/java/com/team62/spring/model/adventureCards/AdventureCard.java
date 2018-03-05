package com.team62.spring.model.adventureCards;

import com.team62.spring.model.Card;


public abstract class AdventureCard extends Card{
    int battlePoints;
    
    public int getBattlePoints(){
        return this.battlePoints;
    }
}
