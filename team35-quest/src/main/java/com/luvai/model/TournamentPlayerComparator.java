package com.luvai.model;

import java.util.Comparator;

public class TournamentPlayerComparator implements Comparator<TournamentPlayer> {

	@Override
	public int compare(TournamentPlayer t1, TournamentPlayer t2) {
		int points1 = t1.points;
		int points2 = t2.points;
		return points1 > points2 ? -1 : points1 < points2 ? 1 : 0;
	}

}
