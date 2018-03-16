package com.luvai.model.StoryCards;

public class TournamentCard extends StoryCard {
	int bonus;
	// void LaunchTournament() {}

	public TournamentCard(String n, String StringFile, int bonus) {
		this.name = n;
		this.StringFile = StringFile;
		this.bonus = bonus;
	}

	public String toString() {
		String json = "{\"name\":" + "\"" + this.name + "\"" + ", \"stringFile\":" + "\"" + this.StringFile + "\""
				+ ", \"bonus\":" + this.bonus + "}";

		return json;
	}
}