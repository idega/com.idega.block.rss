/*
 * Created on 22.10.2003
 */
package com.idega.block.rss.business;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jdom.Element;
import com.idega.block.rss.data.RSSHeadline;
import com.idega.block.rss.data.RSSHeadlineHome;
import com.idega.block.rss.data.RSSHeadlineHomeImpl;
import com.idega.block.rss.data.RSSSource;
import com.idega.block.rss.data.RSSSourceHome;
import com.idega.block.rss.data.RSSSourceHomeImpl;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.util.timer.PastDateException;
import com.idega.util.timer.TimerEntry;
import com.idega.util.timer.TimerListener;
import com.idega.util.timer.TimerManager;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * This bundle starter goes through all defined RSSSources, and updates the
 * RSSHeadlines for them. It is of course started when the server starts and
 * runs at specific intervals.
 * 
 * @author <a href="mailto:jonas@idega.is>Jonas K. Blandon</a>
 */
public class RSSBusinessPoller implements IWBundleStartable {

	private static String IW_BUNDLE_IDENTIFIER = "com.idega.block.rss";

	/**
	 * Gets an instance of RSSBusinessPoller
	 * 
	 * @return An instance of RSSBusinessPoller
	 */
	public static RSSBusinessPoller getInstance(IWApplicationContext iwac) {
		if (_instance == null) {
			(new RSSBusinessPoller()).start(iwac.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER));
		}
		return _instance;
	}

	/**
	 * Goes through all defined RSSSources, and updates the RSSHeadlines for
	 * them. Removes RSSHeadlines that are no longer present in the RSSSource
	 * from the server. This means that the RSSHeadlines in the database are a
	 * time snapshot of the response from the RSSSource server.
	 * 
	 */
	private void updateAllRSSHeadlines() {
		try {
			RSSSourceHome sHome = new RSSSourceHomeImpl();
			Collection sources = sHome.findSources();
			Iterator sIter = sources.iterator();
			while (sIter.hasNext()) {
				RSSSource source = (RSSSource) sIter.next();
				System.out.println("Updating RSS Headlines for " + source);
				updateRSSHeadlinesForRSSSource(source);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Brings all RSSHeadlines for a given RSSSource up to date
	 * 
	 * @param rssSource
	 *            The RSSSource to bring headlines up to date for
	 * @return true if headlines are up to date, false otherwise (if, for
	 *         example, the no rss response was retrieved from the url)
	 */
	public boolean updateRSSHeadlinesForRSSSource(RSSSource rssSource) {
		// String sourceUrl = rssSource.getSourceURL();
		// String sourceId = rssSource.getSourceId();
		System.out.println("Saving snapshot of rss source " + rssSource);
		try {
			Collection newHeadlines = getLatestHeadlinesFromRSSByRSSSource(rssSource);
			if (newHeadlines.isEmpty()) {
				return false;
			}
			RSSBusiness business = getBusiness();
			Collection oldHeadlines = business.getHeadlinesByRSSSource(rssSource);
			Iterator newHeadlineIter = newHeadlines.iterator();
			if (newHeadlineIter.hasNext()) {
				while (newHeadlineIter.hasNext()) {
					try {
						RSSHeadline headline = (RSSHeadline) newHeadlineIter.next();
						if (oldHeadlines.contains(headline)) {
							// headline already exists, don't reinsert and don't
							// remove later
							System.out.println("Headline from rss already exists: " + headline);
							oldHeadlines.remove(headline);
						}
						else {
							System.out.println("Inserting new Headline from rss: " + headline);
							headline.store();
							rssSource.addHeadline(headline);
						}
					}
					catch (Exception e) {
						System.out.println("Exception inserting headline");
						e.printStackTrace();
					}
				}
			}
			// now go through the remaining headlines in existingHeadlines (they
			// are the
			// ones that no longer exists in the rss response) and remove them
			Iterator oldHeadlineIter = oldHeadlines.iterator();
			if (oldHeadlineIter.hasNext()) {
				while (oldHeadlineIter.hasNext()) {
					try {
						RSSHeadline headline = (RSSHeadline) oldHeadlineIter.next();
						if (!newHeadlines.contains(headline)) {
							System.out.println("Removing headline no longer in rss (" + headline + ")");
							headline.remove();
						}
					}
					catch (Exception e) {
						System.out.println("Exception removing headline");
						e.printStackTrace();
					}
				}
			}
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Retrieves the latest RSSHeadlines for a given RSSSource by making a
	 * request to the RSS server. Note: The headlines in the list have not been
	 * stored persistently.
	 * 
	 * @param rssSource
	 *            The RSSSource to retrieve latest RSSHeadline from
	 * @return A List of latest RSSHeadlines
	 */
	public List getLatestHeadlinesFromRSSByRSSSource(RSSSource rssSource) {
		List theReturn = new ArrayList();
		try {
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(new URL(rssSource.getSourceURL())));
			System.out.println(feed);
			List entries = feed.getEntries();
			
			if(entries!=null){
				for (Iterator iter = entries.iterator(); iter.hasNext();) {
					SyndEntry entry = (SyndEntry) iter.next();
					String link = entry.getLink();
					String title = entry.getTitle();
					
					RSSHeadlineHome hHome = new RSSHeadlineHomeImpl();
					RSSHeadline h = hHome.create();
					h.setHeadline(title);
					h.setLink(link);
					h.store();
					theReturn.add(h);
				}
				
			}
			
			
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return theReturn;
	}

	/**
	 * Collects all elements with name "item" below an element into a collection
	 * 
	 * @param element
	 *            search this element and its descendants
	 * @param col
	 *            The collection to add "item" elements into
	 */
	private void collectItems(Element element, List col) {
		if ("item".equals(element.getName())) {
			col.add(element);
		}
		java.util.List children = element.getChildren();
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			Element item = (Element) iter.next();
			collectItems(item, col);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.idegaweb.IWBundleStartable#start(com.idega.idegaweb.IWBundle)
	 */
	public void start(IWBundle starterBundle) {
		String strPollInterval = starterBundle.getProperty(BUNDLE_PROPERTY_NAME_POLL_INTERVAL);
		if (strPollInterval != null && strPollInterval.length() > 0) {
			try {
				pollInterval = Integer.parseInt(strPollInterval.trim());
				System.out.println("Poll interval set to " + pollInterval + " minutes");
			}
			catch (NumberFormatException e) {
				System.out.println("Could not set rss poll interval to " + strPollInterval);
				e.printStackTrace();
			}
		}
		_instance = this;
		bundle_ = starterBundle;
		if (tManager == null) {
			tManager = new TimerManager();
		}
		if (pollTimerEntry == null) {
			try {
				pollTimerEntry = tManager.addTimer(pollInterval, true, new TimerListener() {

					public void handleTimer(TimerEntry entry) {
						updateAllRSSHeadlines();
					}
				});
			}
			catch (PastDateException e) {
				pollTimerEntry = null;
				e.printStackTrace();
			}
		}
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
	 * Gets an instance of RSSBusiness
	 * 
	 * @return An instance of RSSBusiness
	 */
	private RSSBusiness getBusiness() {
		if (_business == null) {
			try {
				RSSBusinessHome businessHome = new RSSBusinessHomeImpl();
				_business = businessHome.create();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return _business;
	}

	private RSSBusiness _business = null;
	private static int pollInterval = 30; // polling interval in minutes
	private static TimerManager tManager = null;
	private static TimerEntry pollTimerEntry = null;
	private static RSSBusinessPoller _instance = null;
	private static final String BUNDLE_PROPERTY_NAME_POLL_INTERVAL = "iw_bundle_rss_poll_interval";
	private IWBundle bundle_;
}
