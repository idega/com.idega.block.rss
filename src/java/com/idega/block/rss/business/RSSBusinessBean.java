/*
 * Created on 2003-jun-04
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.rss.business;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.block.rss.data.RSSSource;
import com.idega.block.rss.data.RSSSourceHome;
import com.idega.business.IBOLookup;
import com.idega.business.IBOServiceBean;
import com.idega.presentation.IWContext;
import com.idega.util.ListUtil;

/**
 * @author wmgobom
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RSSBusinessBean extends IBOServiceBean implements RSSBusiness {
	
	/**
	 * Gets a RSSBusiness instance from a IWContext, used by the presentation classes
	 * @param iwc The IWContext
	 * @return A RSSBusiness instance
	 * @throws RemoteException
	 */
	public static RSSBusiness getRSSBusiness(IWContext iwc) throws RemoteException{        
		return (RSSBusiness) IBOLookup.getServiceInstance(iwc, RSSBusiness.class);        
	}
	
	/**
	 * Gets an RSSSource by its Id
	 * @param sourceId The Id of the RSSSource
	 * @return The RSSSource
	 */
	public RSSSource getRSSSourceBySourceId(String sourceId) {
		RSSSource source = null;
		try {
			RSSSourceHome sHome = (RSSSourceHome) getIDOHome(RSSSource.class);
			Collection sources = sHome.findSourceById(sourceId);
			if(sources.size()>0) {
				source = (RSSSource) sources.iterator().next();
			}
		} catch (Exception e) {
			//String msg = e.getMessage();
			e.printStackTrace();
		}
		return source;
	}
	
	/**
	 * Gets all RSSHeadlines for a given RSSSource
	 * @param rssSource The RSSSource
	 * @return A Collection of RSSHeadlines for the given RSSSource
	 */
	public Collection getRSSHeadlinesByRSSSource(RSSSource rssSource) {
		Collection headlines = Collections.EMPTY_LIST;
		if(rssSource!=null) {
			try {
				headlines = getHeadlinesByRSSSource(rssSource);                    
			} catch (RemoteException re) {
				re.printStackTrace();
				headlines = ListUtil.getEmptyList();
			} catch (FinderException fe) {
				fe.printStackTrace();
				headlines = ListUtil.getEmptyList();            
			}
		}
		return headlines;
	}
	
	/**
	 * Gets all defined RSSSources
	 * @return A List of all defined RSSSources  
	 */
	public List getAllRSSSources() {
		List sources = null;
		try {
			RSSSourceHome sHome = (RSSSourceHome) getIDOHome(RSSSource.class);
			sources = new ArrayList(sHome.findSources());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sources;
	}

	/**
	 * Creates a new RSSSource
	 * @param name The name of the new source (same as url if null or empty)
	 * @param url The URL for the RSS source
	 * @return true if a new source was created, otherwise false (for example, if
	 *         an equivalent source already existed)
	 */
	public boolean createNewRSSSource(String name, String url) {
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
		Iterator sourcesIter = getAllRSSSources().iterator();
		while(sourcesIter.hasNext()) {
			RSSSource source = (RSSSource) sourcesIter.next();
			if(source.getSourceURL().equals(url.trim())) {
				// not added because it already exists
				return false;
			}
		}
		try {
			RSSSourceHome sHome = (RSSSourceHome) getIDOHome(RSSSource.class);
			RSSSource source = sHome.create();
			source.setName(name);
			source.setSourceURL(url);
			source.store();
			boolean ok = RSSBusinessPoller.getInstance().updateRSSHeadlinesForRSSSource(source);
			if(!ok) {
				System.out.println("Coulnd't fetch and save source, url may be wrong");
				source.remove();
				return false;
			}
		} catch(Exception e){
			System.out.println("Couldn't add RSS source: " + name);
			e.printStackTrace();
			return false;
		}
	
		return true;
	}
	
	/**
	 * Gets All RSSHeadlines for a given RSSSource
	 * @param rssSource The RSSSource
	 * @return A Collection of all RSSHeadlins for the given RSSSource
	 * @throws RemoteException
	 * @throws FinderException
	 */
	public Collection getHeadlinesByRSSSource(RSSSource rssSource) throws RemoteException, FinderException {
		return rssSource.getHeadlines();
	}
	
	/**
	 * Removes the source definition and all headlines for a RSSSource
	 * @param id The id of the RSSSource
	 * @return true if a source definition was successfully removed, 
	 *         false otherwise
	 */
	public boolean removeSourceById(String id) {
		if(id==null) {
			return false;
		} else {
			try {
				RSSSource source = getRSSSourceBySourceId(id);
				if(source!=null) {
					source.removeHeadlines();
					// remove source definition
					source.remove();
					System.out.println("Removed RSS source: " + source);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
