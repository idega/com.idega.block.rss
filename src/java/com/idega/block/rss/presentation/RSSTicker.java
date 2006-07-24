/*
 * $Id: RSSTicker.java,v 1.4.2.3 2006/07/24 10:54:30 laddi Exp $
 * Created on Feb 22, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.presentation;

import com.idega.block.rss.business.RSSBusiness;
import com.idega.block.rss.data.RSSSource;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Page;
import com.idega.presentation.Script;

/**
 * Displays one headline at a time with or without extra information<br>
 * in a "ticker" sort of way ;) Neat'O!<br>
 * The javascript it uses is in the file rssticker.js and the method you call is<br>
 * rssticker_ajax(RSS_URL, cachetime, divId, divClass, delay, optionalswitch)<br>
 * 1) RSS_URL: "URL to an RSS source (atom 1.0 or RSS 2.0)"<br>
 * 2) cachetime: Time to cache the feed in minutes (0 for no cache)<br>
 * 3) divId: "ID of DIV to display ticker in. DIV dynamically created"<br>
 * 4) divClass: "Class name of this ticker, for styling purposes"<br>
 * 5) delay: delay between message change, in milliseconds<br>
 * 6) optionalswitch: "optional arbitrary" string to create additional logic in call back function<br>
 * e.g. "date" will show title and date, "date+description" will also show the description with the date and title.
 * 
 *  Last modified: $Date: 2006/07/24 10:54:30 $ by $Author: laddi $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.4.2.3 $
 */
public class RSSTicker extends RSSViewer {
	
	private long tickerIntervalInMS = 5000;
	private int tickerPollingIntervalInMinutes = 5;
//	private boolean showTitle = true;
//	private boolean showDate = true;
//	private boolean showDescription = true;
	private String tickerStyleClass = "rssTicker";
	private String tickerId = null;
	private String dateFormatPattern = "dd.MM.yyyy hh:mm:ss";
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		IWBundle iwb = this.getBundle(iwc);
		
		if(getSourceId()>-1){
			RSSBusiness business = getRSSBusiness(iwc);
			
			RSSSource rssSource = business.getRSSSourceBySourceId(getSourceId());
			//ADD /content
			String rssSourceURL = business.getRSSLocalURIWithContextAndSlideServlet(rssSource);
			String options = "title";
			if(showDate()){
				options+="+date";
			}
			if(showDescription()){
				options+="+description";
			}
			
			
			Layer layer = new Layer();
			layer.setStyleClass("rssTicker");
			if(getTickerId() == null){
				this.tickerId = layer.getID();		
			}
			
			//TODO use cachtime in javascript!!
			//rssticker_ajax(RSS_URL, cachetime, divId, divClass, delay, optionalswitch)
			String javascriptObjectName = "rssTicker"+this.tickerId;
			String scriptString ="var "+javascriptObjectName+" = new rssticker_ajax('"+rssSourceURL+"',"+getTickerPollingIntervalInMinutes()+", '"+this.tickerId+"', '"+getTickerStyleClass()+"', "+getTickerIntervalInMS()+", '"+options+"','"+getDateFormatPattern()+"');";
			String scriptSource = iwb.getResourcesVirtualPath() +"/javascript/rssticker.js";
			
			add(layer);
			
			Page parentPage = getParentPage();
			if(parentPage!=null && !iwc.isSafari()){
				parentPage.addScriptSource(scriptSource);	
			}
			else{
				//in the builder for example
				Script scriptSourceScript = new Script();
				scriptSourceScript.setScriptSource(scriptSource);
				layer.add(scriptSourceScript);
			}
			
			Script script = new Script();
			script.addFunction("", scriptString);
			
			layer.add(script);
	
			
	//		document.write("CNN News: (Fade Effect enabled. Title+date shown)")
	//		document.write("<br />BBC News: (Title+date+description shown)")
	//		new rssticker_ajax("BBC", 1200, "ddbox", "bbcclass", 3500, "date+description")
	
			
		}
		
		
	}
	
	/**
	 * @return Returns the tickerId.
	 */
	public String getTickerId() {
		return this.tickerId;
	}

	
	/**
	 * @param tickerId The tickerId to set.
	 */
	public void setTickerId(String tickerId) {
		this.tickerId = tickerId;
	}

	
	/**
	 * @return Returns the tickerIntervalInMS.
	 */
	public long getTickerIntervalInMS() {
		return this.tickerIntervalInMS;
	}

	
	/**
	 * @param tickerIntervalInMS The tickerIntervalInMS to set.
	 */
	public void setTickerIntervalInMS(long tickerIntervalInMS) {
		this.tickerIntervalInMS = tickerIntervalInMS;
	}

	
	/**
	 * @return Returns the tickerStyleClass.
	 */
	public String getTickerStyleClass() {
		return this.tickerStyleClass;
	}

	
	/**
	 * @param tickerStyleClass The tickerStyleClass to set.
	 */
	public void setTickerStyleClass(String tickerStyleClass) {
		this.tickerStyleClass = tickerStyleClass;
	}


	
	/**
	 * @return Returns the tickerPollingIntervalInMinutes.
	 */
	public int getTickerPollingIntervalInMinutes() {
		return this.tickerPollingIntervalInMinutes;
	}


	
	/**
	 * @param tickerPollingIntervalInMinutes The tickerPollingIntervalInMinutes to set.
	 */
	public void setTickerPollingIntervalInMinutes(int tickerPollingIntervalInMinutes) {
		this.tickerPollingIntervalInMinutes = tickerPollingIntervalInMinutes;
	}

	/**
	 * @return Returns the dateFormatPattern.
	 */
	public String getDateFormatPattern() {
		return this.dateFormatPattern;
	}

	/**
	 * Use this method to change the way an rss items date is displayed
	 * @param dateFormatPattern The default dateFormatPattern is dd.MM.yyyy hh:mm:ss
	 */
	public void setDateFormatPattern(String dateFormatPattern) {
		this.dateFormatPattern = dateFormatPattern;
	}
		
}
