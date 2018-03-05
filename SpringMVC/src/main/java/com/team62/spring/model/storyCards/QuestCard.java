package com.team62.spring.model.storyCards;

import com.team62.spring.model.adventureCards.*;

public class QuestCard extends StoryCard{
	String name;
	FoeCard foe; 
	FoeCard foe2;
	int stages;

	public QuestCard() {	}
	
	public QuestCard(String name, String StringFile, FoeCard foe, int stages) {
		this.name = name;
		this.StringFile = StringFile;
		this.foe = foe;
		this.stages = stages;
		this.foe2 = null;
	}
	
	public QuestCard(String name, String StringFile, FoeCard foe, FoeCard foe2, int stages) {
		this.name = name;
		this.StringFile = StringFile;
		this.foe = foe;
		this.foe2 = foe2;
		this.stages = stages;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getStages(){
		return this.stages;
	}
	
	public FoeCard getFoe(){
		return this.foe;
	}
	
	public FoeCard getFoe2() {
		if(this.foe2 == null) {
			return null;
		} else { return this.foe2;
		}
	}
	public String toString() {
		return "QuestCard";
	}

}