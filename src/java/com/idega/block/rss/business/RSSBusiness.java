package com.idega.block.rss.business;


import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.block.rss.data.RSSSource;
import com.idega.block.rss.data.RSSSourceHome;
import com.idega.business.IBOService;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherListener;

public interface RSSBusiness extends IBOService, FetcherListener {
	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getRSSSourceBySourceId
	 */
	public RSSSource getRSSSourceBySourceId(int sourceId) throws RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getRSSSourceHome
	 */
	public RSSSourceHome getRSSSourceHome() throws RemoteException, RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getRSSLocalURIByRSSSourceId
	 */
	public String getRSSLocalURIByRSSSourceId(int sourceId) throws RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getRSSHeadlinesByRSSSource
	 */
	public Collection getRSSHeadlinesByRSSSource(RSSSource rssSource) throws RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getAllRSSSources
	 */
	public List getAllRSSSources() throws RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#createNewRSSSource
	 */
	public boolean createNewRSSSource(String name, String url) throws RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getEntriesByRSSSource
	 */
	public Collection getEntriesByRSSSource(RSSSource rssSource) throws RemoteException, FinderException, RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getRSSLocalURIWithContextAndSlideServlet
	 */
	public String getRSSLocalURIWithContextAndSlideServlet(RSSSource rssSource) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getRSSLocalURIWithContextAndSlideServletNoServerURL
	 */
	public String getRSSLocalURIWithContextAndSlideServletNoServerURL(RSSSource rssSource) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#removeSourceById
	 */
	public boolean removeSourceById(int id) throws RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#fetcherEvent
	 */
	public void fetcherEvent(FetcherEvent event);

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getSyndEntries
	 */
	public List getSyndEntries(SyndFeed feed) throws RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#convertFeedToAtomXMLString
	 */
	public String convertFeedToAtomXMLString(SyndFeed feed) throws RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#convertFeedToRSS2XMLString
	 */
	public String convertFeedToRSS2XMLString(SyndFeed feed) throws RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getFeedFetcher
	 */
	public FeedFetcher getFeedFetcher() throws RemoteException;
}