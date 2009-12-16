package com.idega.block.rss.business;

import java.util.Comparator;

public class RSSSyndEntryComparator implements Comparator<RSSSyndEntry> {

	@Override
	public int compare(RSSSyndEntry o1, RSSSyndEntry o2) {
		if (o1.getEntry().getPublishedDate() == null && o2.getEntry().getPublishedDate() != null) {
			return 1;
		}
		else if (o1.getEntry().getPublishedDate() != null && o2.getEntry().getPublishedDate() == null) {
			return -1;
		}
		else if (o1.getEntry().getPublishedDate() == null && o2.getEntry().getPublishedDate() == null) {
			return 0;
		}
		return (int) (o1.getEntry().getPublishedDate().getTime() - o2.getEntry().getPublishedDate().getTime());
	}
}