package com.idega.block.rss.business;

import java.util.Comparator;
import java.util.Date;

public class RSSSyndEntryComparator implements Comparator<RSSSyndEntry> {

	public int compare(RSSSyndEntry o1, RSSSyndEntry o2) {
		Date date1 = o1.getEntry().getPublishedDate();
		if (date1 == null) {
			date1 = o1.getEntry().getUpdatedDate();
		}
		Date date2 = o2.getEntry().getPublishedDate();
		if (date2 == null) {
			date2 = o2.getEntry().getUpdatedDate();
		}
		
		if (date1 == null && date2 != null) {
			return -1;
		}
		else if (date1 != null && date2 == null) {
			return 1;
		}
		else if (date1 == null && date1 == null) {
			return 0;
		}
		
		return date1.getTime() - date2.getTime() > 0 ? -1 : 1;
	}
}