/*
 * $Id$
 * Created on Feb 13, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import com.idega.block.rss.business.RSSBusiness;
import com.idega.block.rss.data.RSSSource;
import com.idega.block.rss.data.RSSSourceHome;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.include.GlobalIncludeManager;
import com.idega.util.timer.PastDateException;
import com.idega.util.timer.TimerEntry;
import com.idega.util.timer.TimerListener;
import com.idega.util.timer.TimerManager;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;

/**
 * This bundle starter goes through all defined RSSSources, and updates the
 * rss feed files for them that are store in the webdav repository under /files/cms/rss/.
 * It is of course started when the server starts and
 * runs at specific intervals.
 * 
 *  Last modified: $Date$ by $Author$
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision$
 */
public class IWBundleStarter implements IWBundleStartable {

//	private static String IW_BUNDLE_IDENTIFIER = "com.idega.block.rss";
//	private boolean started = false;	
	private RSSBusiness _business = null;
	private static int pollInterval = 20; // polling interval in minutes
	private static TimerManager tManager = null;
	private static TimerEntry pollTimerEntry = null;
	private static final String BUNDLE_PROPERTY_NAME_POLL_INTERVAL = "iw_bundle_rss_poll_interval";
	
	public void start(IWBundle starterBundle) {
		GlobalIncludeManager includeManager = GlobalIncludeManager.getInstance();
		includeManager.addBundleStyleSheet("com.idega.block.rss", "/style/rss.css");
		//START THE RSS FEED POLLING/AGGREGATING
		//startPoller(starterBundle);
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.idegaweb.IWBundleStartable#stop(com.idega.idegaweb.IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
		if (tManager != null) {
			if (pollTimerEntry != null) {
				tManager.removeTimer(pollTimerEntry);
				pollTimerEntry = null;
			}
		}
	}


	/**
	 * Goes through all defined RSSSources and updates the stored (in slide)
	 * feeds on this server if needed
	 */
	protected void pollAllRSSFeeds() {
		try {
			RSSSourceHome sHome = (RSSSourceHome) IDOLookup.getHome(RSSSource.class);
			Collection sources = sHome.findSources();
			Iterator sIter = sources.iterator();
			while (sIter.hasNext()) {
				RSSSource source = (RSSSource) sIter.next();
				URL feedUrl;
				try {
					feedUrl = new URL(source.getSourceURL());
				
				// Feed Poll and then Retrieved event will happen (assuming the
				// feed is valid)
				// we do nothing here, all is handled in the event handling in
				// RSSBusiness
				SyndFeed feed = getRSSBusiness().getFeedFetcher().retrieveFeed(feedUrl);
				System.out.println(feedUrl + " has a title: " + feed.getTitle() + " and contains "
						+ feed.getEntries().size() + " entries.");
				}
				catch (MalformedURLException e) {
					//todo remove
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startPoller(IWBundle starterBundle) {
			String strPollInterval = starterBundle.getProperty(BUNDLE_PROPERTY_NAME_POLL_INTERVAL);
			if (strPollInterval != null && strPollInterval.length() > 0) {
				try {
					pollInterval = Integer.parseInt(strPollInterval.trim());
					System.out.println("RSS feed polling interval set to " + pollInterval + " minutes");
				}
				catch (NumberFormatException e) {
					System.out.println("Could not set rss poll interval to " + strPollInterval);
					e.printStackTrace();
				}
			}
			if (tManager == null) {
				tManager = new TimerManager();
				FeedFetcher fetcher;
				try {
					fetcher = getRSSBusiness().getFeedFetcher();
					fetcher.addFetcherEventListener(getRSSBusiness());
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			if (pollTimerEntry == null) {
				try {
					pollTimerEntry = tManager.addTimer(pollInterval, true, new TimerListener() {

						public void handleTimer(TimerEntry entry) {
							pollAllRSSFeeds();
						}
					});
				}
				catch (PastDateException e) {
					pollTimerEntry = null;
					e.printStackTrace();
				}
			}
	}


	/**
	 * Gets an instance of RSSBusiness
	 * 
	 * @return An instance of RSSBusiness
	 * @throws IBOLookupException
	 */
	protected RSSBusiness getRSSBusiness() throws IBOLookupException {
		if (this._business == null) {
			this._business = (RSSBusiness) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(),
					RSSBusiness.class);
		}
		return this._business;
	}

}
