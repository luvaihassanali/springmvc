package com.luvai.model.StoryCards;

public class EventCard extends StoryCard {
	String description; 
	//void Event() {} ---leaving this until we decide how to handle events
	
	public EventCard(String name, String StringFile, String description) {
		this.name = name;
		this.StringFile = StringFile;
		this.description = description;
		this.StringFile = StringFile;
	}
	
	public String toString() {
		return "EventCard";
	}

}
