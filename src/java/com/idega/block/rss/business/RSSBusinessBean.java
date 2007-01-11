/*
 * Created on 2003-jun-04
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.rss.business;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.block.rss.data.RSSSource;
import com.idega.block.rss.data.RSSSourceHome;
import com.idega.business.IBOServiceBean;
import com.idega.slide.business.IWSlideService;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherListener;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * This service bean does all the real rss handling work
 * 
 * Last modified: $Date: 2007/01/11 17:33:23 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.24.2.3 $
 */
public class RSSBusinessBean extends IBOServiceBean implements RSSBusiness, FetcherListener {

	public static final String RSS_FOLDER_URI = "/files/cms/rss/";
	private IWSlideService slideService;
	private FeedFetcherCache feedInfoCache;
	private FeedFetcher fetcher;

	/**
	 * Gets an RSSSource by its Id
	 * 
	 * @param sourceId
	 *            The Id of the RSSSource
	 * @return The RSSSource
	 */
	public RSSSource getRSSSourceBySourceId(int sourceId) {
		RSSSource source = null;
		try {
			RSSSourceHome sHome = getRSSSourceHome();
			source = sHome.findSourceById(sourceId);
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		return source;
	}

	/**
	 * @return
	 * @throws RemoteException
	 */
	public RSSSourceHome getRSSSourceHome() throws RemoteException {
		RSSSourceHome sHome = (RSSSourceHome) getIDOHome(RSSSource.class);
		return sHome;
	}

	public String getRSSLocalURIByRSSSourceId(int sourceId) {
		RSSSource source = getRSSSourceBySourceId(sourceId);
		return source.getLocalSourceURI();
	}

	/**
	 * Gets all RSSHeadlines for a given RSSSource
	 * 
	 * @param rssSource
	 *            The RSSSource
	 * @return A Collection of RSSHeadlines for the given RSSSource
	 */
	public Collection getRSSHeadlinesByRSSSource(RSSSource rssSource) {
		Collection headlines = Collections.EMPTY_LIST;
		if (rssSource != null) {
			try {
				headlines = getEntriesByRSSSource(rssSource);
			}
			catch (RemoteException re) {
				re.printStackTrace();
				headlines = ListUtil.getEmptyList();
			}
			catch (FinderException fe) {
				fe.printStackTrace();
				headlines = ListUtil.getEmptyList();
			}
		}
		return headlines;
	}

	/**
	 * Gets all defined RSSSources
	 * 
	 * @return A List of all defined RSSSources
	 */
	public List getAllRSSSources() {
		List sources = null;
		try {
			RSSSourceHome sHome = getRSSSourceHome();
			sources = new ArrayList(sHome.findSources());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return sources;
	}

	/**
	 * Creates a new RSSSource
	 * 
	 * @param name
	 *            The name of the new source (same as url if null or empty)
	 * @param url
	 *            The URL for the RSS source
	 * @return true if a new source was created, otherwise false (for example,
	 *         if an equivalent source already existed)
	 */
	public boolean createNewRSSSource(String name, String url) {
		if (url != null && url.trim().length() != 0) {
			url = url.trim();
			if (name == null || name.trim().length() == 0) {
				name = url;
			}
			name = name.trim();
		}
		else {
			// not added because url was empty
			return false;
		}
		
		try {
			RSSSource source = null;
			try {
				source = getRSSSourceHome().findSourceByURL(url);
			}
			catch (FinderException exp) {
				RSSSourceHome sHome = getRSSSourceHome();
				source = sHome.create();
			}
			source.setName(name);
			source.setSourceURL(url);
			source.store();
			processFeed(getFeedFetcher().retrieveFeed(new URL(url)), url);
		}
		catch (Exception e) {
			System.out.println("Couldn't add RSS source: " + name);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Gets All SyndEntry for a given RSSSource
	 * 
	 * @param rssSource
	 *            The RSSSource
	 * @return A Collection of all SyndEntry for the given RSSSource
	 * @throws RemoteException
	 */
	public Collection getEntriesByRSSSource(RSSSource rssSource) throws RemoteException, FinderException {
		try {
			
			String localRSSFileURL = getRSSLocalURIWithContextAndSlideServlet(rssSource);
			
			URL theURL = new URL(localRSSFileURL);
			log("Getting feed from local URL :" + theURL.toExternalForm());
			return getSyndEntries(getFeedFetcher().retrieveFeed(theURL));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return ListUtil.getEmptyList();
	}

	/**
	 * @param rssSource
	 * @return
	 * @throws RemoteException
	 */
	public String getRSSLocalURIWithContextAndSlideServlet(RSSSource rssSource) throws RemoteException {
		String localRSSFileURL = rssSource.getLocalSourceURI();
		
		String serverURLWithContent = getIWSlideService().getWebdavServerURL().toString();
		if(!serverURLWithContent.endsWith("/")){
			serverURLWithContent+="/";
		}
		
		if (localRSSFileURL.endsWith("/")) {
			localRSSFileURL = localRSSFileURL.substring(0, localRSSFileURL.length() - 1);
		}
		
		if(localRSSFileURL.startsWith("/content")){
			localRSSFileURL = localRSSFileURL.substring(9);
		}
		else if(localRSSFileURL.startsWith("/")){
			localRSSFileURL = localRSSFileURL.substring(1);
		}
		
		localRSSFileURL = serverURLWithContent+localRSSFileURL;
		return localRSSFileURL;
	}
	
	public String getRSSLocalURIWithContextAndSlideServletNoServerURL(RSSSource rssSource) throws RemoteException {
		String localRSSFileURL = rssSource.getLocalSourceURI();
		String serverURLWithContent = getIWSlideService().getURI(localRSSFileURL);
		return serverURLWithContent;
	}
	
	/**
	 * Removes the source definition and all headlines for a RSSSource
	 * 
	 * @param id
	 *            The id of the RSSSource
	 * @return true if a source definition was successfully removed, false
	 *         otherwise
	 */
	public boolean removeSourceById(int id) {
		try {
			RSSSource source = getRSSSourceBySourceId(id);
			if (source != null) {
				// remove source definition
				source.remove();
				System.out.println("Removed RSS source: ");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Gets an instance of IWSlideService
	 * 
	 * @return An instance of IWSlideService
	 */
	protected IWSlideService getIWSlideService() {
		if (this.slideService == null) {
			try {
				this.slideService = (IWSlideService) getServiceInstance(IWSlideService.class);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.slideService;
	}

	/**
	 * @see com.sun.syndication.fetcher.FetcherListener#fetcherEvent(com.sun.syndication.fetcher.FetcherEvent)
	 */
	public void fetcherEvent(FetcherEvent event) {
		String eventType = event.getEventType();
		if (FetcherEvent.EVENT_TYPE_FEED_POLLED.equals(eventType)) {
			logDebug("RSS feed Polled. URL = " + event.getUrlString());
		}
		else if (FetcherEvent.EVENT_TYPE_FEED_RETRIEVED.equals(eventType)) {
			logDebug("RSS feed Retrieved. URL = " + event.getUrlString());
			SyndFeed feed = event.getFeed();
			// Only when we are not fetching it locally
			processFeed(feed, event.getUrlString());
			// try {
			// SyndFeedInfo feedInfo = getFeedFetcherCache().getFeedInfo(new
			// URL(event.getUrlString()));
			// if(feedInfo!=null){
			// log(feedInfo.toString());
			// }
			// }
			// catch (MalformedURLException e) {
			// e.printStackTrace();
			// }
		}
		else if (FetcherEvent.EVENT_TYPE_FEED_UNCHANGED.equals(eventType)) {
			logDebug("Rss feed Unchanged. URL = " + event.getUrlString());
		}
	}

	/**
	 * If needed this method creates an Atom 1.0 formatted xml feed from the rss
	 * response and stores in Slide and then update RSSSource
	 * 
	 * @param feed
	 * @param feedURL
	 */
	protected void processFeed(SyndFeed feed, String feedURL) {
		try {
			// upload the xml as a file
			RSSSource source = getRSSSourceHome().findSourceByURL(feedURL);
			String localSourceURI = createFileInSlide(feed, feedURL, source);
			updateRSSSource(source, feed, feedURL, localSourceURI);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (FinderException ex) {
			// it is most likely a local fetch! no matter just skip the storing
			// in slide part and the updating
		}
	}

	/**
	 * @param feed
	 * @param feedURL
	 * @param localSourceURI
	 * @throws FinderException
	 * @throws RemoteException
	 */
	protected void updateRSSSource(RSSSource source, SyndFeed feed, String feedURL, String localSourceURI)
			throws FinderException, RemoteException {
		// TODO add extra column? localizable?
		// todo update RSSSource with all data
		// String subscriptionURL = feed.getLink();
		String title = feed.getTitle();
		// String author = feed.getAuthor();
		// String description = feed.getDescription();
		// String copyright = feed.getCopyright();
		// String feedType = feed.getFeedType();
		// String language = feed.getLanguage();
		// String encoding = feed.getEncoding();
		// Date publishedDate = feed.getPublishedDate();
		// SyndImage image = feed.getImage();
		// if (image != null) {
		// String feedImageURL = image.getUrl();
		// }
		source.setTitle(title);
		source.setLocalSourceURI(localSourceURI);
		source.setSourceURL(feedURL);
		source.store();
	}

	/**
	 * @param feed
	 * @param atomXML
	 * @throws RemoteException
	 */
	protected String createFileInSlide(SyndFeed feed, String feedURL, RSSSource source) throws RemoteException {
		//String atomXML = convertFeedToAtomXMLString(feed);
		String xml = null;
		try{
			xml = convertFeedToRSS2XMLString(feed);
			
			if(xml==null){
				//rss2 failed try atom 1.0
				xml = convertFeedToAtomXMLString(feed);
			}
		}
		catch(NullPointerException ex){
			ex.printStackTrace();
			//because of bug in ROME, try with mbl.is rss files
			xml = convertFeedToAtomXMLString(feed);
		}
		
		
		
		String fileName = null;
		IWSlideService ss = getIWSlideService();
		feedURL = feedURL.substring(0, Math.max(feedURL.length(), feedURL.lastIndexOf("?") + 1));
		if (feedURL.endsWith(".xml")) {
			fileName = feedURL.substring(feedURL.lastIndexOf("/") + 1);
		}
		else if (feedURL.endsWith(".atom")) {
			fileName = feedURL.substring(feedURL.lastIndexOf("/") + 1);
			fileName = fileName.replaceAll(".atom", ".xml");
		}
		else if (feedURL.endsWith(".rss")) {
			fileName = feedURL.substring(feedURL.lastIndexOf("/") + 1);
			fileName = fileName.replaceAll(".rss", ".xml");
		}
		else if (source != null) {
			String name = source.getName();
			fileName = name + ".xml";
		}
		else {
			String title = feed.getTitle();
			fileName = title + ".xml";
		}
		// just to be safe
		fileName = fileName.replaceAll(" ", "");
		char[] except = { '.' };
		fileName = StringHandler.stripNonRomanCharacters(fileName, except);
		// "true" : delete the previous version of the file  (do not create millions of versions)
		ss.uploadXMLFileAndCreateFoldersFromStringAsRoot(RSS_FOLDER_URI, fileName, xml, true);
		return RSS_FOLDER_URI + fileName;
	}

	/**
	 * Returns the list of SyndEntry's or an empty list
	 */
	public List getSyndEntries(SyndFeed feed) {
		List entries = feed.getEntries();
		if (entries == null) {
			entries = ListUtil.getEmptyList();
		}
		return entries;
	}

	/**
	 * Takes a SyndFeed of any type and returns it as an Atom 1.0 xml string
	 * 
	 * @param feed
	 * @return
	 */
	public String convertFeedToAtomXMLString(SyndFeed feed) {
		// SyndFeedInput input = new SyndFeedInput();
		feed.setFeedType("atom_1.0");
		SyndFeedOutput output = new SyndFeedOutput();
		String xmlFeed = null;
		try {
			xmlFeed = output.outputString(feed);
			// System.out.println(xmlFeed);
			// output.output(feed,new PrintWriter(System.out));
		}
		catch (FeedException e) {
			e.printStackTrace();
		}
		return xmlFeed;
	}

	/**
	 * Takes a SyndFeed of any type and returns it as an RSS 2.0 xml string
	 * 
	 * @param feed
	 * @return
	 */
	public String convertFeedToRSS2XMLString(SyndFeed feed) {
		// SyndFeedInput input = new SyndFeedInput();
	//	System.out.println("FEED TYPE: "+feed.getFeedType());
		feed.setFeedType("rss_2.0");
		SyndFeedOutput output = new SyndFeedOutput();
		
		String xmlFeed = null;
		try {
			xmlFeed = output.outputString(feed);
			// System.out.println(xmlFeed);
			// output.output(feed,new PrintWriter(System.out));
		}
		catch (FeedException e) {
			e.printStackTrace();
		}
		return xmlFeed;
	}

	/**
	 * @return a feedfetcher with caching
	 */
	public FeedFetcher getFeedFetcher() {
		if (this.fetcher == null) {
			this.fetcher = new HttpURLFeedFetcher(getFeedFetcherCache());
		}
		return this.fetcher;
	}

	/**
	 * 
	 * @return the instance of FeedFetcherCache so we can know if a feed needs
	 *         to be updated or not
	 */
	protected FeedFetcherCache getFeedFetcherCache() {
		if (this.feedInfoCache == null) {
			this.feedInfoCache = HashMapFeedInfoCache.getInstance();
		}
		return this.feedInfoCache;
	}
	/*
	 * Aggreate many into one! public static void main(String[] args) { boolean
	 * ok = false; if (args.length>=2) { try { String outputType = args[0];
	 * 
	 * SyndFeed feed = new SyndFeedImpl(); feed.setFeedType(outputType);
	 * 
	 * feed.setTitle("Aggregated Feed"); feed.setDescription("Anonymous
	 * Aggregated Feed"); feed.setAuthor("anonymous");
	 * feed.setLink("http://www.anonymous.com");
	 * 
	 * List entries = new ArrayList(); feed.setEntries(entries);
	 * 
	 * for (int i=1;i<args.length;i++) { URL inputUrl = new URL(args[i]);
	 * 
	 * SyndFeedInput input = new SyndFeedInput(); SyndFeed inFeed =
	 * input.build(new XmlReader(inputUrl));
	 * 
	 * entries.addAll(inFeed.getEntries()); }
	 * 
	 * SyndFeedOutput output = new SyndFeedOutput(); output.output(feed,new
	 * PrintWriter(System.out));
	 * 
	 * ok = true; } catch (Exception ex) { System.out.println("ERROR:
	 * "+ex.getMessage()); } }
	 * 
	 * if (!ok) { System.out.println(); System.out.println("FeedAggregator
	 * aggregates different feeds into a single one."); System.out.println("The
	 * first parameter must be the feed type for the aggregated feed.");
	 * System.out.println(" [valid values are: rss_0.9, rss_0.91U, rss_0.91N,
	 * rss_0.92, rss_0.93, ]"); System.out.println(" [ rss_0.94, rss_1.0,
	 * rss_2.0 & atom_0.3 ]"); System.out.println("The second to last parameters
	 * are the URLs of feeds to aggregate."); System.out.println(); } } }
	 * 
	 */
}
