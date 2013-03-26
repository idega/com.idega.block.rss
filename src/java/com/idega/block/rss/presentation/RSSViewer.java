/*
 * Created on 2003-jun-03
 */
package com.idega.block.rss.presentation;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.idega.block.rss.business.RSSBusiness;
import com.idega.block.rss.business.RSSSyndEntry;
import com.idega.block.rss.business.RSSSyndEntryComparator;
import com.idega.block.rss.data.RSSSource;
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.Page;
import com.idega.presentation.Script;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.util.IWTimestamp;
import com.idega.util.PresentationUtil;
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
	private Collection<Integer> sources = null;
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
	private int daysBack = -1;
	
	private String sourceURL;
	
	private static final String SOURCE_URL_PROPERTY = "sourceURL";
	private static final String DESCRIPTION_PROPERTY = "description";
	private static final String MAX_LINKS_PROPERTY = "maxLinks";
	private static final String SHOW_DATE_PROPERTY = "showDate";
	private static final String SHOW_DESCRIPTION_PROPERTY = "showDescription";
	private static final String NEW_WINDOW_PROPERTY = "newWindow";

	public RSSViewer() {
		super();
		setCacheable(getCacheKey(), (60 * 60 * 1000));
	}

	@Override
	public String getCacheKey() {
		return "rss_cache";
	}
	
	@Override
	protected String getCacheState(IWContext iwc, String cacheStatePrefix) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(cacheStatePrefix + (sourceURL != null ? "_" + sourceURL : ""));
		
		return buffer.toString();
	}

	@Override
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[7];
		values[0] = super.saveState(ctx);
		values[1] = this.sourceURL;
		values[2] = this.description;
		values[3] = new Integer(this.maxLinks);
		values[4] = new Boolean(this.showDate);
		values[5] = new Boolean(this.showDescription);
		values[6] = this.linkTargetType;
		return values;
	}

	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this.sourceURL = (String) values[1];
		this.description = (String) values[2];
		this.maxLinks = ((Integer) values[3]).intValue();
		this.showDate = ((Boolean) values[4]).booleanValue();
		this.showDescription = ((Boolean) values[5]).booleanValue();
		this.linkTargetType = (String) values[6];
	}
    
	@Override
	public void encodeBegin(FacesContext context) throws IOException { 
		ValueExpression ve = getValueExpression(SOURCE_URL_PROPERTY);
    	if (ve != null) {
	    	String url = (String) ve.getValue(context.getELContext());
	    	setSourceURL(url);
    	}    
    	
		ve = getValueExpression(DESCRIPTION_PROPERTY);
    	if (ve != null) {
	    	String description = (String) ve.getValue(context.getELContext());
    		setDescription(description);
    	}

		ve = getValueExpression(MAX_LINKS_PROPERTY);
    	if (ve != null) {
	    	int maxLinks = Integer.parseInt(ve.getValue(context.getELContext()).toString());
    		setMaxLinks(maxLinks);
    	}    
    	
		ve = getValueExpression(SHOW_DATE_PROPERTY);
    	if (ve != null) {
	    	boolean showDate = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
	    	setShowDate(showDate);
    	}

		ve = getValueExpression(SHOW_DESCRIPTION_PROPERTY);
    	if (ve != null) {
	    	boolean showDescription = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
	    	setShowDescription(showDescription);
    	}

		ve = getValueExpression(NEW_WINDOW_PROPERTY);
    	if (ve != null) {
	    	boolean newWindow = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
	    	setOpenInNewWindow(newWindow);
    	}
    	
    	super.encodeBegin(context);
	}
    
	/**
	 * This is where everything happens.
	 */
	@Override
	public void main(IWContext iwc) throws Exception {
		IWBundle iwb = getBundle(iwc);
		IWResourceBundle iwrb = this.getResourceBundle(iwc);

		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/rss.css"));

		// if no selected rss source display an error message
		if (this.sources == null && this.sourceURL == null) {
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
				List<RSSSyndEntry> allEntries = new ArrayList<RSSSyndEntry>();
				
				if (this.sources != null) {
					Iterator it = sources.iterator();
					while (it.hasNext()) {
						Integer sourceID = (Integer) it.next();
						RSSSource rssSource = business.getRSSSourceBySourceId(sourceID.intValue());
						Collection entries = business.getRSSHeadlinesByRSSSource(rssSource);
						allEntries.addAll(entries);
					}
				}
				else if (this.sourceURL != null) {
					allEntries.addAll(business.getRSSHeadlinesByURL(this.sourceURL, null));
				}
				Collections.sort(allEntries, new RSSSyndEntryComparator());
				
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
					layer.add(paragraph);
				}
				int row = 1;
				int maxLinksTmp = this.maxLinks;
				if (maxLinksTmp < 1) {
					// if maxLinks is zero (or negative), no limit
					maxLinksTmp = 10000;
				}
				
				IWTimestamp stamp = null;
				if (daysBack > 0) {
					stamp = new IWTimestamp();
					stamp.addDays(-daysBack);
				}
				
				for (Iterator loop = allEntries.iterator(); row <= maxLinksTmp && loop.hasNext();) {
					RSSSyndEntry rssEntry = (RSSSyndEntry) loop.next();
					SyndEntry entry = rssEntry.getEntry();
					RSSSource source = rssEntry.getSource();
			
					String entryLink = entry.getLink();
					String entryTitle = entry.getTitle();
					// TODO USE ALL SYNDFEED ITEMS
//					List entryAuthors = rssEntry.getAuthors();
//					List entryContributors = rssEntry.getContributors();
					List entryContents = entry.getContents();
					Date entryPublishedDate = entry.getPublishedDate();
					if(entryPublishedDate==null){
						entryPublishedDate = entry.getUpdatedDate();
					}
					
					if (stamp != null && stamp.isLaterThan(new IWTimestamp(entryPublishedDate))) {
						continue;
					}
					
//					Date entryUpdatedDate = rssEntry.getUpdatedDate();
					SyndContent entryDescription = entry.getDescription();
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
					
					if (source != null && source.getIconURI() != null) {
						Image image = new Image(source.getIconURI());
						image.setStyleClass("feedIcon");
						item.add(image);
					}
					
					String originalTitle = entryTitle;
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
						itemHeadline.add(originalTitle);
						
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
						itemDescription.add(description);
						item.add(itemDescription);	
					}
					
					layer.add(item);
					row++;
				}
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean getShowDescription() {
		return showDescription;
	}

	/**
	 * standard overriding to point to the correct bundle
	 */
	@Override
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
	
	public Collection<Integer> getSources() {
		return this.sources;
	}

	/**
	 * @param string
	 */
	public void setSourceId(String id) {
		try {
			int sourceID = Integer.parseInt(id);
			if (sources == null) {
				sources = new ArrayList<Integer>();
			}
			sources.add(new Integer(sourceID));
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
		return IBOLookup.getServiceInstance(iwc, RSSBusiness.class);
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

	public void setDaysBack(int days) {
		this.daysBack = days;
	}

	public String getSourceURL() {
		return sourceURL;
	}

	public void setSourceURL(String sourceURL) {
		this.sourceURL = sourceURL;
	}	
}