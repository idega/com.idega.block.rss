/*
 * Created on 2003-jun-03
 */
package com.idega.block.rss.presentation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import com.idega.block.rss.business.RSSBusiness;
import com.idega.block.rss.data.RSSSource;
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Page;
import com.idega.presentation.Script;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.util.IWTimestamp;
import com.idega.util.StringHandler;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;

/**
 * Displays the current links from a selected RSS source.
 * 
 * @author <a href="mailto:eiki@idega.is>Eirikur S. Hrafnsson</a>
 */
public class RSSViewer extends Block {

	// Member variables
	private static String IW_BUNDLE_IDENTIFIER = "com.idega.block.rss";
	private int sourceId = -1;
	private int maxLinks = 0;
	private String description = null;
	private String linkTargetType = Link.TARGET_NEW_WINDOW;
	private String layerID = null;
	private int maximumNumberOfLettersInHeadline = 0;
	private boolean useHiddenLayer = false;
	private boolean showTitle = true;
	private boolean showDate = true;
	private boolean showDescription = true;
	private boolean stripHTMLFromContent = false;
	private boolean stripHTMLFromDescription = false;
	
	

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
		if (this.sourceId == -1) {
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
				RSSSource rssSource = business.getRSSSourceBySourceId(this.sourceId);
				Collection entries = business.getRSSHeadlinesByRSSSource(rssSource);
				Layer layer = new Layer();
				layer.setStyleClass("rss");
				if(this.layerID!=null){
					layer.setID(this.layerID);
				}
				add(layer);
				
				// add stuff to the block
				if (this.description != null && this.description.length() > 0) {
					Paragraph paragraph = new Paragraph();
					paragraph.setStyleClass("description");
					paragraph.add(new Text(this.description));
					add(paragraph);
				}
				int row = 1;
				int maxLinksTmp = this.maxLinks;
				if (maxLinksTmp < 1) {
					// if maxLinks is zero (or negative), no limit
					maxLinksTmp = 10000;
				}
				for (Iterator loop = entries.iterator(); row <= maxLinksTmp && loop.hasNext();) {
					SyndEntry rssEntry = (SyndEntry) loop.next();
					row++;
			
					String entryLink = rssEntry.getLink();
					String entryTitle = rssEntry.getTitle();
					// TODO USE ALL SYNDFEED ITEMS
//					List entryAuthors = rssEntry.getAuthors();
//					List entryContributors = rssEntry.getContributors();
					List entryContents = rssEntry.getContents();
					Date entryPublishedDate = rssEntry.getPublishedDate();
//					Date entryUpdatedDate = rssEntry.getUpdatedDate();
					SyndContent entryDescription = rssEntry.getDescription();
					String description = "";
					if (entryDescription != null) {
						//String descriptionType = entryDescription.getType();
						//todo force pure text if type xhtml?
						description = entryDescription.getValue();
					}
					
					if(entryTitle==null){
						//rss 0.92 does not require the title
						entryTitle = description;
					}
					
					String content = "";
					if( entryContents!=null && !entryContents.isEmpty()){
						for (Iterator iter = entryContents.iterator(); iter.hasNext();) {
							SyndContent entryContent = (SyndContent) iter.next();
							String value = entryContent.getValue();
							if(value!=null && !"null".equals(value) && !value.equals(description)){
								content+=value;
							}
						}
					}
					
					Layer item = new Layer();
					item.setStyleClass("rssItem");
					if (entryTitle.length() > getMaximumNumberOfLettersInHeadline()
							&& getMaximumNumberOfLettersInHeadline() != 0) {
						entryTitle = entryTitle.substring(0, getMaximumNumberOfLettersInHeadline() - 1) + "...";
					}
					
					Link link = new Link(entryTitle, entryLink);
					
					if(isSetToStripHTMLFromDescription()){
						description = StringHandler.removeHtmlTagsFromString(description);	
					}
					
					if(isSetToStripHTMLFromContent()){
						content = StringHandler.removeHtmlTagsFromString(content);
					}
					
					if(this.useHiddenLayer){
						
						String scriptSource = getBundle(iwc).getResourcesVirtualPath() +"/javascript/rss.js";
						if("".equals(content) && description!=null){
							content = description;
						}
						Layer itemContent = new Layer();
						itemContent.setStyleClass("rssItemContent");
						
						Layer itemHeadline = new Layer();
						itemHeadline.setStyleClass("rssItemHeadline");
						itemHeadline.add(entryTitle);
						
						itemContent.add(itemHeadline);
						
						if(content!=null && !"".equals(content)){
							itemContent.add(content);
						}
						
						Page parentPage = getParentPage();
						if(parentPage!=null){
							parentPage.add(itemContent);
						}
						else{
							this.add(itemContent);
						}
						
						Script script = null;
						String onClickString = null;	
			
						if(parentPage!=null && !iwc.isSafari()){
							parentPage.addScriptSource(scriptSource);
							onClickString = "showRSSContentLayer('"+itemContent.getID()+"');";
						}
						else{
							//in the builder for example
							Script scriptSourceScript = new Script();
							scriptSourceScript.setScriptSource(scriptSource);
							layer.add(scriptSourceScript);
							
							script = new Script();
							layer.add(script);
							String layerId = itemContent.getID();
							String scriptString = "function showRSSContentLayer"+layerId+"(contentLayerID) {  \n\tsetRSSContentLayerId(contentLayerID);\n\tvar content = findObj(contentLayerID);\n\tcontent.innerHTML = content.innerHTML +'<'+'a href=# style=visibility:hidden;  id=focusLink name=focusLink ><'+'/a>';\n\tcontent.style.visibility='visible';\n\tfindObj('focusLink').focus();\n}";
							script.addFunction("showRSSContentLayer"+layerId+"(contentLayerID)", scriptString);		
							onClickString = "showRSSContentLayer"+layerId+"('"+layerId+"');";
						}
						
						//so we have some connection to the hidden layer for javascript to work with
						item.setName(itemContent.getID());
						
						link = new Link(entryTitle, "javascript:"+onClickString);
						
						link.setOnClick(onClickString);
					}
					else{
						link = new Link(entryTitle, entryLink);
						link.setTarget(this.linkTargetType);
					}
					item.add(link);
					
					Layer itemPublished = new Layer();
					itemPublished.setStyleClass("rssItemPublishedDate");
					item.add(itemPublished);
					if (entryPublishedDate != null) {
						itemPublished.add(new Text(new IWTimestamp(entryPublishedDate).getLocaleDate(iwc.getCurrentLocale(), IWTimestamp.SHORT)));
					}
					else {
						//JUST ADD THE CURRENT TIME
						itemPublished.add(new Text(new IWTimestamp().getLocaleDate(iwc.getCurrentLocale(), IWTimestamp.SHORT)));
					}
					
					if(!"".equals(description) && getShowDescription()){
						Layer itemDescription = new Layer();
						itemDescription.setStyleClass("rssItemDescription");
						item.add(itemDescription);	
					}
					
					layer.add(item);
				}
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean getShowDescription() {
		// TODO Auto-generated method stub
		return false;
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
		return this.sourceId;
	}

	/**
	 * @param string
	 */
	public void setSourceId(String id) {
		try {
			this.sourceId = Integer.parseInt(id);
		}
		catch (Exception e) {
			System.err.println("Couldn't save new source id value");
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	public int getMaxLinks() {
		return this.maxLinks;
	}

	/**
	 * @param string
	 */
	public void setMaxLinks(String string) {
		try {
			this.maxLinks = Integer.parseInt(string);
		}
		catch (Exception e) {
			System.err.println("Couldn't save new max link value");
			e.printStackTrace();
		}
	}
	
	public void setMaxLinks(int maximumNumberOfLinksToDisplay) {
		this.maxLinks = maximumNumberOfLinksToDisplay;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String str) {
		this.description = str;
	}

	public boolean getOpenInNewWindow() {
		return this.linkTargetType == Link.TARGET_NEW_WINDOW;
	}

	public void setOpenInNewWindow(boolean b) {
		if (b) {
			this.linkTargetType = Link.TARGET_NEW_WINDOW;
		}
		else {
			this.linkTargetType = Link.TARGET_SELF_WINDOW;
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
		return this.maximumNumberOfLettersInHeadline;
	}

	/**
	 * @param maximumNumberOfLettersInHeadline
	 *            The maximumNumberOfLettersInHeadline to set.
	 */
	public void setMaximumNumberOfLettersInHeadline(int maximumNumberOfLettersInHeadline) {
		this.maximumNumberOfLettersInHeadline = maximumNumberOfLettersInHeadline;
	}
	
	public void setUseOnClickAndHiddenLayerForContent(boolean useHiddenLayer){
		this.useHiddenLayer  = useHiddenLayer;
	}
	
	/**
	 * @return Returns the showDescription.
	 */
	public boolean showDescription() {
		return this.showDescription;
	}

	
	/**
	 * @param showDescription The showDescription to set.
	 */
	public void setShowDescription(boolean showDescription) {
		this.showDescription = showDescription;
	}
	
	
	/**
	 * @return Returns the showDate.
	 */
	public boolean showDate() {
		return this.showDate;
	}

	
	/**
	 * @param showDate The showDate to set.
	 */
	public void setShowDate(boolean showDate) {
		this.showDate = showDate;
	}
	
	/**
	 * @return Returns the showTitle.
	 */
	public boolean showTitle() {
		return this.showTitle;
	}

	
	/**
	 * @param showTitle The showTitle to set.
	 */
	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}

	
	/**
	 * @return Returns the isSetToStripHTMLFromContent.
	 */
	public boolean isSetToStripHTMLFromContent() {
		return this.stripHTMLFromContent;
	}

	
	/**
	 * @param stripHTMLFromContent The stripHTMLFromContent to set.
	 */
	public void setStripHTMLFromContent(boolean stripHTMLFromContent) {
		this.stripHTMLFromContent = stripHTMLFromContent;
	}

	
	/**
	 * @return Returns the isSetToStripHTMLFromDescription.
	 */
	public boolean isSetToStripHTMLFromDescription() {
		return this.stripHTMLFromDescription;
	}

	
	/**
	 * @param stripHTMLFromDescription The stripHTMLFromDescription to set.
	 */
	public void setStripHTMLFromDescription(boolean stripHTMLFromDescription) {
		this.stripHTMLFromDescription = stripHTMLFromDescription;
	}

}
