package com.idega.block.rss.business;

import com.idega.block.rss.data.RSSSource;
import com.sun.syndication.feed.synd.SyndEntry;

public class RSSSyndEntry {

	private SyndEntry entry;
	private RSSSource source;
	
	public RSSSyndEntry() {
	}
	
	public RSSSyndEntry(RSSSource source, SyndEntry entry) {
		this.source = source;
		this.entry = entry;
	}
	
	public SyndEntry getEntry() {
		return entry;
	}

	public void setEntry(SyndEntry entry) {
		this.entry = entry;
	}
	
	public RSSSource getSource() {
		return source;
	}
	
	public void setSource(RSSSource source) {
		this.source = source;
	}
}