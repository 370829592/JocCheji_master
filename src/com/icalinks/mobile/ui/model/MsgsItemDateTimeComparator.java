package com.icalinks.mobile.ui.model;

import java.util.Comparator;

public class MsgsItemDateTimeComparator implements Comparator<MsgsItem>{

	@Override
	public int compare(MsgsItem lhs, MsgsItem rhs) {
		return lhs.getDatetime().compareTo(rhs.getDatetime());
	}
}