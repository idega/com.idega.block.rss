/*
 * Created on 2003-jun-04
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.rss.business;

import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;

import org.jdom.Document;

import com.idega.block.rss.data.RSSSource;
import com.idega.block.rss.data.RSSSourceHome;
import com.idega.business.IBOServiceBean;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.idgenerator.business.UUIDBusiness;
import com.idega.slide.business.IWSlideService;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.URIUtil;
import com.sun.syndication.feed.module.DCModule;
import com.sun.syndication.feed.module.DCModuleImpl;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.wfw.CommentAPIModule;
import com.sun.syndication.feed.module.wfw.CommentAPIModuleImpl;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.feed.synd.SyndPersonImpl;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.FetcherListener;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * This service bean does all the real rss handling work
 * 
 * Last modified: $Date: 2009/06/15 15:05:17 $ by $Author: valdas $
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.37 $
 */
public class RSSBusinessBean extends IBOServiceBean implements RSSBusiness, FetcherListener {

	private static final long serialVersionUID = -4108662712781008003L;

	private static final Logger LOGGER = Logger.getLogger(RSSBusinessBean.class.getName());
	
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
	public Collection<RSSSyndEntry> getRSSHeadlinesByRSSSource(RSSSource rssSource) {
		Collection<RSSSyndEntry> list = new ArrayList<RSSSyndEntry>();
		
		Collection<RSSSyndEntry> headlines = Collections.EMPTY_LIST;
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
		
		Iterator it = headlines.iterator();
		while (it.hasNext()) {
			SyndEntry entry = (SyndEntry) it.next();
			list.add(new RSSSyndEntry(rssSource, entry));
		}
		
		return list;
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
	public boolean createNewRSSSource(String name, String url, String iconURI) {
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
			source.setIconURI(iconURI);
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
	public Collection<RSSSyndEntry> getEntriesByRSSSource(RSSSource rssSource) throws RemoteException, FinderException {
		try {
			
			/*String localRSSFileURL = getRSSLocalURIWithContextAndSlideServlet(rssSource);
			
			URL theURL = new URL(localRSSFileURL);*/
			URL theURL = new URL(rssSource.getSourceURL());
			//LOGGER.info("Getting feed from local URL :" + theURL.toExternalForm());
			return getSyndEntries(getFeedFetcher().retrieveFeed(theURL));
		}
		catch (Exception e) {
			//Something failed, don't really care...
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
		
		if(localRSSFileURL.startsWith(CoreConstants.WEBDAV_SERVLET_URI)){
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
				this.slideService = getServiceInstance(IWSlideService.class);
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
		feedURL = feedURL.substring(0, Math.max(feedURL.length(), feedURL.lastIndexOf("?") + 1));
		if (feedURL.endsWith(".xml")) {
			fileName = feedURL.substring(feedURL.lastIndexOf("/") + 1);
			if (source != null) {
				fileName = StringHandler.stripNonRomanCharacters(source.getName()) + "_" + fileName;
			}
		}
		else if (feedURL.endsWith(".atom")) {
			fileName = feedURL.substring(feedURL.lastIndexOf("/") + 1);
			fileName = fileName.replaceAll(".atom", ".xml");
			if (source != null) {
				fileName = StringHandler.stripNonRomanCharacters(source.getName()) + "_" + fileName;
			}
		}
		else if (feedURL.endsWith(".rss")) {
			fileName = feedURL.substring(feedURL.lastIndexOf("/") + 1);
			fileName = fileName.replaceAll(".rss", ".xml");
			if (source != null) {
				fileName = StringHandler.stripNonRomanCharacters(source.getName()) + "_" + fileName;
			}
		}
		else if (source != null) {
			String name = source.getName();
			fileName = name + ".xml";
		}
		else {
			String title = feed.getTitle();
			fileName = title + ".xml";
		}
		return createFileInSlide(xml, fileName);
	}
	
	/**
	 * @param feedXML
	 * @param fileName
	 * @return path to uploaded feed
	 * @throws RemoteException
	 */
	public String createFileInSlide(String feedXML, String fileName) throws RemoteException {
		if (feedXML == null || fileName == null) {
			return null;
		}
		
		// just to be safe
		fileName = fileName.replaceAll(" ", "");
		char[] except = { '.' };
		fileName = StringHandler.stripNonRomanCharacters(fileName, except);
		// "true" : delete the previous version of the file  (do not create millions of versions)
		IWSlideService ss = getIWSlideService();
		ss.uploadXMLFileAndCreateFoldersFromStringAsRoot(RSS_FOLDER_URI, fileName, feedXML, true);
		return RSS_FOLDER_URI + fileName;
	}

	/**
	 * Returns the list of SyndEntry's or an empty list
	 */
	public List<RSSSyndEntry> getSyndEntries(SyndFeed feed) {
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
		feed.setFeedType("atom_1.0");
		SyndFeedOutput output = new SyndFeedOutput();
		String xmlFeed = null;
		try {
			xmlFeed = output.outputString(feed);
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
		feed.setFeedType("rss_2.0");
		SyndFeedOutput output = new SyndFeedOutput();
		
		String xmlFeed = null;
		try {
			xmlFeed = output.outputString(feed);
		}
		catch (FeedException e) {
			e.printStackTrace();
		}
		return xmlFeed;
	}
	
	/**
	 * Takes a SyndFeed of any type and returns it as an JDOM document
	 * 
	 * @param feed
	 * @return
	 */
	public Document convertFeedToJDomDocument(SyndFeed feed) {
		if (feed == null) {
			return null;
		}
		SyndFeedOutput output = new SyndFeedOutput();
		try {
			return output.outputJDom(feed);
		} catch (FeedException e) {
			e.printStackTrace();
		}
		return null;
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
	
	public String getLinkToFeedWithUUIDParameters(String link, User user) {
		if (StringUtil.isEmpty(link) || user == null) {
			return link;
		}
		
		URIUtil uri = new URIUtil(link);
		uri.setParameter(LoginBusinessBean.PARAM_LOGIN_BY_UNIQUE_ID, user.getUniqueId());
		uri.setParameter(LoginBusinessBean.LoginStateParameter, LoginBusinessBean.LOGIN_EVENT_LOGIN);
		
		return uri.getUri();
	}
	
	/**
	 * @param pathToFeed
	 * @return returns instance of SyndFeed
	 */
	public SyndFeed getFeed(String pathToFeed) {
		return getFeedAuthenticatedByUser(pathToFeed, null);
	}
	
	public SyndFeed getFeedAuthenticatedByUser(String pathToFeed, User user) {
		if (StringUtil.isEmpty(pathToFeed)) {
			return null;
		}
		
		SyndFeed feed = null;
		try {
			if (user != null) {
				if (user.getUniqueId() == null) {
					UUIDBusiness uuidBusiness = getServiceInstance(UUIDBusiness.class);
					uuidBusiness.addUniqueKeyIfNeeded(user, null);
				}
				
				pathToFeed = getLinkToFeedWithUUIDParameters(pathToFeed, user);
			}
			
			URL url = new URL(pathToFeed);
			feed = getFeedFetcher().retrieveFeed(url);
		} catch (FetcherException fe) {
			LOGGER.warning("Error getting Feed from: " + pathToFeed + ", by user: " + user + ". Error: " + fe.getMessage());
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting Feed from: " + pathToFeed + ", by user: " + user + ": Error: ", e.getMessage());
		}
		
		return feed;
	}
	
	public SyndFeed getFeedAuthenticatedByAdmin(String pathToFeed) {
		try {
			return getFeedAuthenticatedByUser(pathToFeed, getIWMainApplication().getAccessController().getAdministratorUser());
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * @param type: for example "atom_1.0"
	 * @param title
	 * @param serverName
	 * @param feedName
	 * @param description
	 * @param type
	 * @param language
	 * @param date
	 * @return creates new instance of SyndFeed
	 */
	public SyndFeed createNewFeed(String title, String serverName, String description, String type, String language, Timestamp date) {
		SyndFeed feed = new SyndFeedImpl();
		feed.setPublishedDate(date);
		feed.setLanguage(language);
		feed.setTitle(title);
		feed.setLink(serverName);
		feed.setDescription(description);
		feed.setFeedType(type);
		return feed;
	}
	
	/**
	 * @param title
	 * @param link
	 * @param published
	 * @param descriptionType: for example "text/plain"
	 * @param description
	 * @param descriptionType
	 * @param body
	 * @param author
	 * @param language
	 * @param categories
	 * @param bodyType
	 * @param updated
	 * @param source
	 * @param comment
	 * @param linkToComments
	 * @return creates new instance of SyndEntry
	 */
	public SyndEntry createNewEntry(String title, String link, Timestamp updated, Timestamp published, String descriptionType,
			String description, String bodyType, String body, String author, String language, List<String> categories, String source,
			String comment, String linkToComments, String creator) {
		SyndEntry entry = null;
		SyndContent descr = null;
		SyndContent content = null;

		entry = new SyndEntryImpl();
		entry.setTitle(title);
		entry.setLink(link);
		entry.setUri(link);
		entry.setPublishedDate(published);
		entry.setUpdatedDate(updated);
		
		if (categories != null) {
			List<SyndCategory> categoriesList = new ArrayList<SyndCategory>();
			SyndCategory category = null;
			for (int i = 0; i < categories.size(); i++) {
				category = new SyndCategoryImpl();
				category.setName(categories.get(i));
				categoriesList.add(category);
			}
			entry.setCategories(categoriesList);
		}
		
		descr = new SyndContentImpl();
		descr.setType(descriptionType);
		descr.setValue(description);
		entry.setDescription(descr);
		
		content = new SyndContentImpl();
		content.setType(bodyType);
		content.setValue(body);
		List<SyndContent> contents = new ArrayList<SyndContent>();
		contents.add(content);
		entry.setContents(contents);
		
		List<Module> modules = new ArrayList<Module>();
		DCModule dcModule = new DCModuleImpl();
		dcModule.setSource(source);
		dcModule.setCreator(creator);
		dcModule.setDate(published);
		modules.add(dcModule);
		
		if (comment != null || linkToComments != null) {
			CommentAPIModule commentModule = new CommentAPIModuleImpl();
			commentModule.setComment(comment);
			commentModule.setCommentRss(linkToComments);
			modules.add(commentModule);
		}
		entry.setModules(modules);

		List<SyndPerson> authors = new ArrayList<SyndPerson>();
		SyndPerson authorPerson = new SyndPersonImpl();
		authorPerson.setName(author);
		authors.add(authorPerson);
		entry.setAuthors(authors);
		
		return entry;
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
