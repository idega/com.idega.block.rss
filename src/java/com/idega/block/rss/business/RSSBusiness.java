/*
 * $Id: RSSBusiness.java,v 1.11 2007/02/06 01:33:00 valdas Exp $
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
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherListener;


/**
 * 
 *  Last modified: $Date: 2007/02/06 01:33:00 $ by $Author: valdas $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.11 $
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
	 * @see com.idega.block.rss.business.RSSBusinessBean#createFileInSlide
	 */
	public String createFileInSlide(String feedXML, String fileName) throws java.rmi.RemoteException;
	
	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getFeed
	 */
	public SyndFeed getFeed(String pathToFeed);
	
	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#createNewFeed
	 */
	public SyndFeed createNewFeed(String title, String serverName, String description, String type, String language, Timestamp date);
	
	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#createNewEntry
	 */
	public SyndEntry createNewEntry(String title, String link, Timestamp updated, Timestamp published, String descriptionType,
			String description, String bodyType, String body, String author, String language, List<String> categories, String source,
			String comment);
	
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
}