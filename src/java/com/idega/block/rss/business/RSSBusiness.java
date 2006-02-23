/*
 * $Id: RSSBusiness.java,v 1.6 2006/02/23 18:42:02 eiki Exp $
 * Created on Feb 22, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.business;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.FinderException;
import com.idega.block.rss.data.RSSSource;
import com.idega.block.rss.data.RSSSourceHome;
import com.idega.business.IBOService;
import com.idega.business.IBOServiceBean;
import com.idega.slide.business.IWSlideService;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherListener;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;


/**
 * 
 *  Last modified: $Date: 2006/02/23 18:42:02 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.6 $
 */
public interface RSSBusiness extends IBOService, FetcherListener {

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getRSSSourceBySourceId
	 */
	public RSSSource getRSSSourceBySourceId(int sourceId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getRSSLocalURIByRSSSourceId
	 */
	public String getRSSLocalURIByRSSSourceId(int sourceId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getRSSHeadlinesByRSSSource
	 */
	public Collection getRSSHeadlinesByRSSSource(RSSSource rssSource) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getAllRSSSources
	 */
	public List getAllRSSSources() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#createNewRSSSource
	 */
	public boolean createNewRSSSource(String name, String url) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getEntriesByRSSSource
	 */
	public Collection getEntriesByRSSSource(RSSSource rssSource) throws RemoteException, FinderException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#removeSourceById
	 */
	public boolean removeSourceById(int id) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#fetcherEvent
	 */
	public void fetcherEvent(FetcherEvent event);

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getSyndEntries
	 */
	public List getSyndEntries(SyndFeed feed) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#convertFeedToAtomXMLString
	 */
	public String convertFeedToAtomXMLString(SyndFeed feed) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.rss.business.RSSBusinessBean#getFeedFetcher
	 */
	public FeedFetcher getFeedFetcher() throws java.rmi.RemoteException;
}
