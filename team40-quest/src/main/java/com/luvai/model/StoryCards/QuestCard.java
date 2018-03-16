package com.luvai.model.StoryCards;

import com.luvai.model.AdventureCards.FoeCard;

public class QuestCard extends StoryCard {
	FoeCard foe;
	FoeCard foe2;
	int stages;

	public QuestCard() {
	}

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

	public int getStages() {
		return this.stages;
	}

	public String getFoe() {
		if (this.foe == null) {
			return "";
		} else {
			return this.foe.getName();
		}
	}

	public String getFoe2() {
		if (this.foe2 == null) {
			return "\"\"";
		} else {
			return "\"" + this.foe2.getName() + "\"";
		}
	}

	public String toString() {
		String json = "{\"name\":" + "\"" + this.name + "\"" + ", \"stringFile\":" + "\"" + this.StringFile + "\""
				+ ", \"foe\":" + "\"" + this.getFoe() + "\"" + ", \"stages\":" + this.stages + ", \"foe2\":"
				+ this.getFoe2() + "}";

		return json;
	}

}