/*
 * Created on 2003-jun-03
 */
package com.idega.block.rss.presentation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.idega.block.rss.business.RSSBusiness;
import com.idega.block.rss.data.RSSSource;
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.util.IWTimestamp;
import com.sun.syndication.feed.synd.SyndEntry;

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
	private int maximumNumberOfLettersInHeadline = 0;

	// TODO implement caching and displaying of multiple sources
	public RSSViewer() {
		super();
		// setCacheable...
	}

	/**
	 * This is where everything happens.
	 */
	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = this.getResourceBundle(iwc);
		// if no selected rss source display an error message
		if (sourceId == -1) {
			Text msg = new Text(iwrb.getLocalizedString("no.rss.source.selected",
					"No RSS source selected, please select one in the property window."));
			msg.setBold();
			add(msg);
			return;
		}
		else {
			try {
				// get the data with the business bean
				RSSBusiness business = getRSSBusiness(iwc);
				RSSSource rssSource = business.getRSSSourceBySourceId(sourceId);
				Collection entries = business.getRSSHeadlinesByRSSSource(rssSource);
				Layer layer = new Layer();
				layer.setStyleClass("rss");
				layer.setID(layerID);
				add(layer);
				// add stuff to the block
				if (description != null && description.length() > 0) {
					Paragraph paragraph = new Paragraph();
					paragraph.setStyleClass("description");
					paragraph.add(new Text(description));
					add(paragraph);
				}
				int row = 1;
				int maxLinksTmp = maxLinks;
				if (maxLinksTmp < 1) {
					// if maxLinks is zero (or negative), no limit
					maxLinksTmp = 10000;
				}
				for (Iterator loop = entries.iterator(); row <= maxLinksTmp && loop.hasNext();) {
					SyndEntry rssEntry = (SyndEntry) loop.next();
			
					String entryLink = rssEntry.getLink();
					String entryTitle = rssEntry.getTitle();
					// probably a list of SyndContent items
					//List entryAuthors = rssEntry.getAuthors();
					//List entryContributors = rssEntry.getContributors();
					//List entryContents = rssEntry.getContents();
					Date entryPublishedDate = rssEntry.getPublishedDate();
					//Date entryUpdatedDate = rssEntry.getUpdatedDate();
					//SyndContent entryDescription = rssEntry.getDescription();
					//if (entryDescription != null) {
						//String descriptionType = entryDescription.getType();
					//}
					Layer item = new Layer();
					item.setStyleClass("rssItem");
					if (entryTitle.length() > getMaximumNumberOfLettersInHeadline()
							&& getMaximumNumberOfLettersInHeadline() != 0) {
						entryTitle = entryTitle.substring(0, getMaximumNumberOfLettersInHeadline() - 1) + "...";
					}
					Link link = new Link(entryTitle, entryLink);
					link.setTarget(linkTargetType);
					item.add(link);
					Layer itemPublished = new Layer();
					itemPublished.setStyleClass("rssItemPublishedDate");
					item.add(itemPublished);
					if (entryPublishedDate != null) {
						itemPublished.add(new IWTimestamp(entryPublishedDate).getLocaleDateAndTime(iwc.getCurrentLocale(), IWTimestamp.SHORT, IWTimestamp.SHORT));
					}
					else {
						itemPublished.add(new Text(new IWTimestamp().getLocaleDateAndTime(iwc.getCurrentLocale(), IWTimestamp.SHORT, IWTimestamp.SHORT)));
					}
					layer.add(item);
				}
			}
			catch (RemoteException e) {
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
		}
		catch (Exception e) {
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
		}
		catch (Exception e) {
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
		return linkTargetType == Link.TARGET_NEW_WINDOW;
	}

	public void setOpenInNewWindow(boolean b) {
		if (b) {
			linkTargetType = Link.TARGET_NEW_WINDOW;
		}
		else {
			linkTargetType = Link.TARGET_SELF_WINDOW;
		}
	}

	/**
	 * Gets a RSSBusiness instance from a IWContext, used by the presentation
	 * classes
	 * 
	 * @param iwc
	 *            The IWContext
	 * @return A RSSBusiness instance
	 * @throws RemoteException
	 */
	public RSSBusiness getRSSBusiness(IWContext iwc) throws RemoteException {
		return (RSSBusiness) IBOLookup.getServiceInstance(iwc, RSSBusiness.class);
	}

	public void setLayerID(String layerID) {
		this.layerID = layerID;
	}

	/**
	 * @return Returns the maximumNumberOfLettersInHeadline.
	 */
	public int getMaximumNumberOfLettersInHeadline() {
		return maximumNumberOfLettersInHeadline;
	}

	/**
	 * @param maximumNumberOfLettersInHeadline
	 *            The maximumNumberOfLettersInHeadline to set.
	 */
	public void setMaximumNumberOfLettersInHeadline(int maximumNumberOfLettersInHeadline) {
		this.maximumNumberOfLettersInHeadline = maximumNumberOfLettersInHeadline;
	}
}
