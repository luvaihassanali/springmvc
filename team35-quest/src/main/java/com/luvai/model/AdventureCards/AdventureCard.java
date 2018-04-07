package com.luvai.model.AdventureCards;

import com.luvai.model.Card;


public abstract class AdventureCard extends Card{
    int battlePoints;
    
    public int getBattlePoints(){
        return this.battlePoints;
    }
}
