package com.luvai.model;

import java.util.ArrayList;

public class PointArray {
	public ArrayList<Integer> points;
	public ArrayList<String> names;

	public PointArray(int stages) {
		points = new ArrayList<Integer>();
		names = new ArrayList<String>();
	}

	public String toString() {
		String temp = "";
		for (int i = 0; i < points.size(); i++) {
			temp += names.get(i) + "#" + points.get(i) + ";";
		}
		return temp;
	}
}
