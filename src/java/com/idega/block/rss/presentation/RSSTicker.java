/*
 * $Id: RSSTicker.java,v 1.1 2006/02/23 18:42:02 eiki Exp $
 * Created on Feb 22, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.presentation;

import com.idega.block.rss.data.RSSSource;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
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
 *  Last modified: $Date: 2006/02/23 18:42:02 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public class RSSTicker extends RSSViewer {
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		IWBundle iwb = this.getBundle(iwc);
		
		if(getSourceId()>0){
			
			RSSSource rssSource = getRSSBusiness(iwc).getRSSSourceBySourceId(getSourceId());
			String rssSourceURL = IWMainApplication.getDefaultIWMainApplication().getTranslatedURIWithContext(rssSource.getLocalSourceURI());
						
			//rssticker_ajax(RSS_URL, cachetime, divId, divClass, delay, optionalswitch)
			String scriptString ="new rssticker_ajax('"+rssSourceURL+"',2, 'ddbox', 'bbcclass', 5000, 'date+description');";
			
			getParentPage().addScriptSource(iwb.getResourcesVirtualPath() +"/javascript/rssticker.js");
			
			Layer layer = new Layer();
			layer.setStyleClass("cnnclass");
			layer.setId("cnnbox");
			
			Script script = new Script();
			script.addFunction("", scriptString);
			
			add(layer);
			layer.add(script);
			
	
			
	//		document.write("CNN News: (Fade Effect enabled. Title+date shown)")
	//		document.write("<br />BBC News: (Title+date+description shown)")
	//		new rssticker_ajax("BBC", 1200, "ddbox", "bbcclass", 3500, "date+description")
	
			
		}
		
		
	}
		
}
