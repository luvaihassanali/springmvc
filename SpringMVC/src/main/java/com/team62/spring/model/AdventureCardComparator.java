package com.team62.spring.model;

import com.team62.spring.model.adventureCards.*;

import java.util.Comparator;

public class AdventureCardComparator  implements Comparator<AdventureCard> {
    
    public int compare(AdventureCard c1, AdventureCard c2){
        int card1 = c1.getBattlePoints();
        int card2 = c2.getBattlePoints();
        
        return card1 < card2 ? -1
         : card1 > card2 ? 1
         : 0;
    }
}
