/*
 * Created on 2003-jun-03
 */
package com.idega.block.rss.presentation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import com.idega.block.rss.business.RSSBusiness;
import com.idega.block.rss.data.RSSHeadline;
import com.idega.block.rss.data.RSSSource;
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;

/**
 * Displays the current links from a selected RSS source.
 * 
 * @author <a href="mailto:jonas@idega.is>Jonas K. Blandon</a>
 */
public class RSSViewer extends Block {
	// Member variables   
	private static String IW_BUNDLE_IDENTIFIER = "com.idega.block.rss";
	private int sourceId = -1;
	private int maxLinks = 0;
	
	private String description = null;
	private String linkTargetType = Link.TARGET_NEW_WINDOW;
	private String layerID = "rssViewer";

	/**
	 * This is where everything happens.
	 */
	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = this.getResourceBundle(iwc);
		//if no selected rss source display an error message
		if(sourceId == -1) {
			Text msg = new Text(iwrb.getLocalizedString("no.rss.source.selected","No RSS source selected, please select one in the property window."));
			msg.setBold();
			add(msg);
			return;
		}
		else {
			try {
				//get the data with the business bean
				RSSBusiness business = getRSSBusiness(iwc);
				RSSSource rssSource = business.getRSSSourceBySourceId(sourceId);
				Collection headlines = business.getRSSHeadlinesByRSSSource(rssSource);
				
				Layer layer = new Layer();
				layer.setStyleClass("rss");
				layer.setID(layerID);
				add(layer);
				
				//add stuff to the block
				if (description!=null && description.length()>0) {
					Paragraph paragraph = new Paragraph();
					paragraph.setStyleClass("description");
					paragraph.add(new Text(description));
					add(paragraph);
				}
				
				int row = 1;
				int maxLinksTmp = maxLinks;
				if(maxLinksTmp<1)  {
					// if maxLinks is zero (or negative), no limit
					maxLinksTmp = 10000;
				}
				for (Iterator loop = headlines.iterator(); row<=maxLinksTmp && loop.hasNext();) {
					RSSHeadline rssHeadline = (RSSHeadline) loop.next();
					String headLine = rssHeadline.getHeadline();
					
					Layer item = new Layer();
					item.setStyleClass("rssItem");
					
					Link link = new Link(headLine, rssHeadline.getLink());
					link.setTarget(linkTargetType);
					item.add(link);
					
					layer.add(item);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * standard overriding to point to the correct bundle
	 */
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	/**
	 * @return
	 */
	public int getSourceId() {
		return sourceId;
	}

	/**
	 * @param string
	 */
	public void setSourceId(String id) {
		try {
			sourceId = Integer.parseInt(id);
		} catch(Exception e) {
			System.err.println("Couldn't save new source id value");
			e.printStackTrace();
		}
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
			System.err.println("Couldn't save new max link value");
			e.printStackTrace();
		}
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String str) {
		description = str;
	}
	
	public boolean getOpenInNewWindow() {
		return linkTargetType==Link.TARGET_NEW_WINDOW;
	}
	
	public void setOpenInNewWindow(boolean b) {
		if(b) {
			linkTargetType = Link.TARGET_NEW_WINDOW;
		} else {
			linkTargetType = Link.TARGET_SELF_WINDOW;
		}
	}
	
	/**
	 * Gets a RSSBusiness instance from a IWContext, used by the presentation classes
	 * @param iwc The IWContext
	 * @return A RSSBusiness instance
	 * @throws RemoteException
	 */
	public RSSBusiness getRSSBusiness(IWContext iwc) throws RemoteException{        
		return (RSSBusiness) IBOLookup.getServiceInstance(iwc, RSSBusiness.class);        
	}

	
	public void setLayerID(String layerID) {
		this.layerID = layerID;
	}

}
