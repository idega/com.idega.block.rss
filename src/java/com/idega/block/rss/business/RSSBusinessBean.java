/*
 * Created on 2003-jun-04
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.rss.business;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.idega.block.rss.data.RSSHeadline;
import com.idega.block.rss.data.RSSHeadlineBMPBean;
import com.idega.block.rss.data.RSSHeadlineHome;
import com.idega.block.rss.data.RSSSource;
import com.idega.block.rss.data.RSSSourceHome;
import com.idega.business.IBOServiceBean;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.util.ListUtil;
import com.idega.util.timer.PastDateException;
import com.idega.util.timer.TimerEntry;
import com.idega.util.timer.TimerListener;
import com.idega.util.timer.TimerManager;

/**
 * @author wmgobom
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RSSBusinessBean extends IBOServiceBean implements RSSBusiness, IWBundleStartable {

	public List getHeadlinesFromRSS(String urlStr) {
		SAXBuilder builder = new SAXBuilder();
		ArrayList theReturn = new ArrayList();
		try {
			URL url = new URL(urlStr);
			Document doc = builder.build(url);
			ArrayList items = new ArrayList(8);
			collectItems(doc.getRootElement(), items);
			int numItems = items.size();
			for(int i=0; i<numItems; i++) {
				Element item = (Element) items.get(i);
				Element eTitle = (Element) item.getChild("title", item.getNamespace());
				Element eLink = (Element) item.getChild("link", item.getNamespace());
				if(eTitle==null || eLink==null) {
					continue;
				}
				String sTitle = eTitle.getText();
				String sLink = eLink.getText();
				if(sTitle==null || sLink==null) {
					continue;
				}
				RSSHeadline h = new RSSHeadlineBMPBean();
				h.setHeadline(sTitle);
				h.setLink(sLink);
				theReturn.add(h);
			}
		} catch (MalformedURLException mue) {
			//String msg = mue.getMessage();
			mue.printStackTrace();
		} catch (Exception e) {
			//String msg = e.getMessage();
			e.printStackTrace();
		}
		return theReturn;
	}
	
	// Collects all elements with name "item" into a collection
	private void collectItems(Element element, List col) {
		if("item".equals(element.getName())) {
			col.add(element);
		}
		java.util.List children = element.getChildren();
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			Element item = (Element) iter.next();
			collectItems(item, col);
		}
	}

	public boolean saveLinksAndHeadlines(String sourceURL) {
		try {
			RSSHeadlineHome hHome = (RSSHeadlineHome) getIDOHome(RSSHeadline.class);
			/*HashSet newHeadlines = new HashSet(getHeadlinesFromRSS(sourceURL));
			HashSet existingHeadlines = new HashSet(getHeadlinesFromDB(sourceURL));*/
			List newHeadlines = getHeadlinesFromRSS(sourceURL);
			if(newHeadlines.isEmpty()) {
				return false;
			}
			Collection oldHeadlines = getHeadlinesFromDB(sourceURL);
			Iterator newHeadlineIter = newHeadlines.iterator();
			if(newHeadlineIter.hasNext()) {
				while (newHeadlineIter.hasNext()) {
		            try{
						RSSHeadlineBMPBean elem = (RSSHeadlineBMPBean) newHeadlineIter.next();
						if(oldHeadlines.contains(elem)) {
							// headline already exists, don't reinsert and don't remove later
							System.out.println("Headline from rss already exists (" + elem.getHeadline() +")");
						} else {
							System.out.println("Inserting new Headline from rss (" + elem.getHeadline() +")");
							RSSHeadline bmp = hHome.create();
							bmp.setHeadline(elem.getHeadline());
							bmp.setLink(elem.getLink());
		                	bmp.setSourceURL(sourceURL);
							bmp.store();
						}
		            } catch(Exception e){
						System.out.println("Exception inserting headline");
		                e.printStackTrace();
		            }
				}
			}
			// now go through the remaining headlines in existingHeadlines and remove them
			Iterator oldHeadlineIter = oldHeadlines.iterator();
			if(oldHeadlineIter.hasNext()) {
				while (oldHeadlineIter.hasNext()) {
					try {
						RSSHeadlineBMPBean elem = (RSSHeadlineBMPBean) oldHeadlineIter.next();
						if(!newHeadlines.contains(elem)) {
							System.out.println("Removing headline no longer in rss (" + elem.getHeadline() +")");
							elem.remove();
						}
					} catch(Exception e){
						System.out.println("Exception removing headline");
						e.printStackTrace();
					}
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
        }
	}
    
	public Collection getLinksAndHeadlines(String sourceURL) {
		Collection headlines = null;
		try {
			headlines = getHeadlinesFromDB(sourceURL);                    
		} catch (RemoteException re) {
			re.printStackTrace();
			headlines = ListUtil.getEmptyList();
		} catch (FinderException fe) {
			fe.printStackTrace();
			headlines = ListUtil.getEmptyList();            
		}
		return headlines;
	}
    
    public Collection getHeadlinesFromDB(String sourceURL) throws RemoteException, FinderException {
        RSSHeadlineHome hHome = (RSSHeadlineHome) getIDOHome(RSSHeadline.class);
        return hHome.findHeadlines(sourceURL);
    }
    
	private void updateRSSHeadlines() {
		try {
			RSSSourceHome sHome = (RSSSourceHome) getIDOHome(RSSSource.class);
			Collection sources = sHome.findSources();
			Iterator sIter = sources.iterator();
			while(sIter.hasNext()) {
				RSSSource source = (RSSSource)sIter.next();
				String url = source.getSourceURL();
				System.out.println("Updating RSS Headlines for " + source.getName());
				saveLinksAndHeadlines(url);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
    
	public List getSources() {
		List sources = null;
		try {
			RSSSourceHome sHome = (RSSSourceHome) getIDOHome(RSSSource.class);
			sources = new ArrayList(sHome.findSources());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sources;
	}
	
	public boolean addSource(String name, String url) {
		if(url!=null && url.trim().length()!=0) {
			url = url.trim();
			if(name==null || name.trim().length()==0) {
				name = url;
			}
			name = name.trim();
		} else {
			// not added because url was empty
			return false;
		}
		Iterator sourcesIter = getSources().iterator();
		while(sourcesIter.hasNext()) {
			RSSSource source = (RSSSource) sourcesIter.next();
			if(source.getSourceURL().equals(url.trim())) {
				// not added because it already exists
				return false;
			}
		}
		try {
			boolean ok = saveLinksAndHeadlines(url);
			if(!ok) {
				System.out.println("Coulnd't fetch and save source, url may be wrong");
				return false;
			}
			RSSSourceHome sHome = (RSSSourceHome) getIDOHome(RSSSource.class);
			RSSSource source = sHome.create();
			source.setName(name);
			source.setSourceURL(url);
			source.store();
		} catch(Exception e){
			System.out.println("Couldn't add RSS source: " + name);
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#start(com.idega.idegaweb.IWBundle)
	 */
	public void start(IWBundle starterBundle) {
		/*try {
			// Tempcode - to make sure some sources are available
			RSSSourceHome sHome = (RSSSourceHome) getIDOHome(RSSSource.class);
			if(sHome.findSources().isEmpty()) {
				RSSSource source = sHome.create();
				source.setName("Yahoo Tech");
				source.setSourceURL("http://rss.news.yahoo.com/rss/tech");
				System.out.println("storing yahoo tech rss");
				source.store();
				RSSSource source2 = sHome.create();
				source2.setName("Yahoo topstories");
				source2.setSourceURL("http://rss.news.yahoo.com/rss/topstories");
				System.out.println("storing yahoo topstories rss");
				source2.store();
			}
		} catch(Exception e){
			System.out.println("Couldn't initialize an RSS source");
			e.printStackTrace();
		}*/
		bundle_ = starterBundle;
		if (tManager==null) {
			tManager = new TimerManager();
		}
		if(pollTimerEntry==null) {
			try {
				pollTimerEntry = tManager.addTimer(RSS_POLL_INTERVAL, true, new TimerListener() {
					public void handleTimer(TimerEntry entry) {
						updateRSSHeadlines();
					}
				});
			} catch(PastDateException e) {
				pollTimerEntry = null;
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#stop(com.idega.idegaweb.IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
		if(tManager!=null) {
			if (pollTimerEntry != null) {
				tManager.removeTimer(pollTimerEntry);
				pollTimerEntry = null;
			}
		}
	}
	
	private static final int RSS_POLL_INTERVAL = 20; // polling interval in minutes
	private static TimerManager tManager = null;
	private static TimerEntry pollTimerEntry = null;
		
	
	private IWBundle bundle_;
}
