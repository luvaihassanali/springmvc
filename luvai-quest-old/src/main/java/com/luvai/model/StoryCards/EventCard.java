package com.luvai.model.StoryCards;

public class EventCard extends StoryCard {
	String description;

	public EventCard(String name, String StringFile, String description) {
		this.name = name;
		this.StringFile = StringFile;
		this.description = description;
		this.StringFile = StringFile;
	}

	public String toString() {
		String json = "{\"name\":" + "\"" + this.name + "\"" + ", \"stringFile\":" + "\"" + this.StringFile + "\""
				+ ", \"description\":" + "\"" + this.description + "\"" + "}";

		return json;
	}

}
