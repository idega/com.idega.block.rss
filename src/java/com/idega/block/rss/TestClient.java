package com.idega.block.rss;

import java.net.URL;
import java.util.List;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

public class TestClient {

	public static void main(String[] arguments) {
		try {
			FeedFetcher fetcher = new HttpURLFeedFetcher(HashMapFeedInfoCache.getInstance());
			URL url = new URL("http://www.lhhestar.is/news/getRSS");
			SyndFeed feed = fetcher.retrieveFeed(url);
			System.out.println(feed.getTitle());
			
			List<SyndEntry> list = feed.getEntries();
			for (SyndEntry entry : list) {
				System.out.println(entry.getPublishedDate() + ": " + entry.getTitle());
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}