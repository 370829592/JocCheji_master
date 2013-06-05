package com.icalinks.mobile.ui.model;

import java.util.Comparator;

public class NearItemDistanceComparator implements Comparator<NearItem> {

	@Override
	public int compare(NearItem lhs, NearItem rhs) {

		return lhs.getDistance() - rhs.getDistance();
	}
}