/*
 * $Id: RSSBusiness.java,v 1.16 2008/10/15 14:53:54 valdas Exp $
 * Created on Feb 22, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.business;


import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import javax.ejb.FinderException;

import org.jdom.Document;

import com.idega.block.rss.data.RSSSource;
import com.idega.block.rss.data.RSSSourceHome;
import com.idega.business.IBOService;
import com.idega.user.data.User;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherListener;


/**
 *
 *  Last modified: $Date: 2008/10/15 14:53:54 $ by $Author: valdas $
 *
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.16 $
 */
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
	public Collection<RSSSyndEntry> getRSSHeadlinesByRSSSource(RSSSource rssSource) throws RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getRSSHeadlinesByRSSSource
	 */
	public Collection<RSSSyndEntry> getRSSHeadlinesByURL(String url, RSSSource rssSource) throws RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getAllRSSSources
	 */
	public List getAllRSSSources() throws RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#createNewRSSSource
	 */
	public boolean createNewRSSSource(String name, String url, String iconURI) throws RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getEntriesByRSSSource
	 */
	public Collection getEntriesByRSSSource(String rssSourceURL) throws RemoteException, FinderException, RemoteException;

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
	@Override
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
	 * @see com.idega.block.rss.business.RSSBusinessBean#createFileInSlide
	 */
	public String createFileInSlide(String feedXML, String fileName) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getFeed
	 */
	public SyndFeed getFeed(String pathToFeed);

	public SyndFeed getFeedAuthenticatedByUser(String pathToFeed, User user);

	public SyndFeed getFeedAuthenticatedByAdmin(String pathToFeed);

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#createNewFeed
	 */
	public SyndFeed createNewFeed(String title, String serverName, String description, String type, String language, Timestamp date);

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#createNewEntry
	 */
	public SyndEntry createNewEntry(EntryData data);

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#convertFeedToRSS2XMLString
	 */
	public String convertFeedToRSS2XMLString(SyndFeed feed) throws RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#convertFeedToJDomDocument
	 */
	public Document convertFeedToJDomDocument(SyndFeed feed);

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getFeedFetcher
	 */
	public FeedFetcher getFeedFetcher() throws RemoteException;

	public String getLinkToFeedWithUUIDParameters(String link, User user);
}