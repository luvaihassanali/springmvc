package com.luvai.controller;

import com.luvai.model.AdventureCards.FoeCard;
import com.luvai.model.AdventureCards.TestCard;
import com.luvai.model.StoryCards.QuestCard;

public class StageController {
	int stage_num;
	TestCard test;
	QuestCard q;
	FoeCard foe;

	public StageController() {
		stage_num = 0;
		test = null;
		q = null;
		foe = null;
	}

	public void setFoe(FoeCard f) {
		this.foe = f;
	}

	public FoeCard getFoe() {
		return foe;

	}
}