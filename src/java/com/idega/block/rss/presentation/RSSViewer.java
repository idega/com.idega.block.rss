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
import com.idega.block.rss.business.RSSBusinessBean;
import com.idega.block.rss.data.RSSHeadline;
import com.idega.block.rss.data.RSSSource;
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
	private String sourceId = null;
	private int maxLinks = 0;
	private String description = null;

	public void main(IWContext iwc) throws Exception {
		if(sourceId == null) {
			Text msg = new Text("No RSS source defined!");
			msg.setBold();
			add(msg);
			return;
		}
		try {
			if (description!=null && description.length()!=0) {
				Text text = new Text(description);
				add(text);
			}
			Table t = new Table();
			add(t);
			RSSBusiness business = RSSBusinessBean.getRSSBusiness(iwc);
			RSSSource rssSource = business.getRSSSourceBySourceId(sourceId);
			Collection headlines = business.getRSSHeadlinesByRSSSource(rssSource);
			int row = 1;
			int maxLinksTmp = maxLinks;
			if(maxLinksTmp<1)  {
				// if maxLinks is zero (or negative), no limit
				maxLinksTmp = 10000;
			}
			for (Iterator loop = headlines.iterator(); row<=maxLinksTmp && loop.hasNext();) {
				RSSHeadline rssHeadline = (RSSHeadline) loop.next();
				String headLine = rssHeadline.getHeadline();
				Link link = new Link(headLine, rssHeadline.getLink());
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
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * @param string
	 */
	public void setSourceId(String id) {
		System.out.println("setting rss source id to " + id);
		sourceId = id;
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
