/*
 * Created on 2003-jun-03
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.rss.presentation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import com.idega.block.rss.business.RSSBusiness;
import com.idega.block.rss.data.RSSHeadline;
import com.idega.business.IBOLookup;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;

/**
 * @author WMGOBOM
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RSSViewer extends Block {
	// Member variabels   
	private static String IW_BUNDLE_IDENTIFIER = "com.idega.block.rss";
	private String url = null;
	private int maxLinks = 0;
	private String description = null;

	// Methods
	protected RSSBusiness getRSSBusiness(IWContext iwc) throws RemoteException {
		return (RSSBusiness) IBOLookup.getServiceInstance(iwc, RSSBusiness.class);
	}

	public void main(IWContext iwc) throws Exception {
		try {
			if (description!=null && description.length()!=0) {
				Text text = new Text(description);
				add(text);
			}
			Table t = new Table();
			add(t);
			RSSBusiness business = getRSSBusiness(iwc);
			Collection headlines = business.getLinksAndHeadlines(url);
			int row = 1;
			int maxLinksTmp = maxLinks;
			if(maxLinksTmp<1)  {
				// if maxLinks is zero (or negative), no limit
				maxLinksTmp = 10000;
			}
			for (Iterator loop = headlines.iterator(); row<=maxLinksTmp && loop.hasNext();) {
				RSSHeadline element = (RSSHeadline) loop.next();
				String headLine = element.getHeadline();
				Link link = new Link(headLine, element.getLink());
				t.add(link, 1, row++);
			}
		} catch (RemoteException e) {
			add("Länkhämtningsfel");
		}
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	/**
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param string
	 */
	public void setUrl(String string) {
		url = string;
	}
	
	/**
	 * @return
	 */
	public String getMaxLinks() {
		return Integer.toString(maxLinks);
	}

	/**
	 * @param string
	 */
	public void setMaxLinks(String string) {
		try {
			maxLinks = Integer.parseInt(string);
		} catch(Exception e) {
			System.out.println("Couldn't save new max link value");
			e.printStackTrace();
		}
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String str) {
		description = str;
	}

}
