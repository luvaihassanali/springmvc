package com.luvai.model;

public class TournamentPlayer {
	public String name;
	public int points;
	public String rank;

	public TournamentPlayer(String[] s) {
		this.name = s[0];
		this.points = Integer.parseInt(s[1]);
		this.rank = s[2];

	}

	public String toString() {
		return this.name + "#" + this.points + "#" + this.rank;

	}

}
