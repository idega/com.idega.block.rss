/*
 * Created on 2003-jun-03
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.rss.presentation;

import java.rmi.RemoteException;

import com.idega.business.IBOLookup;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.block.rss.business.RSSFetcher;


/**
 * @author WMGOBOM
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RSSAdmin extends Block {
    // Member variabels   
    private static String IW_BUNDLE_IDENTIFIER = 
                                            "com.idega.block.rss";
    private String url = null;
    private static String PARAM_SOURCE = "rss_src";

    // Methods
    protected RSSFetcher getRSSFetcher(IWContext iwc) throws RemoteException{        
        return (RSSFetcher) IBOLookup.getServiceInstance(iwc, RSSFetcher.class);        
    }
    
    public void main(IWContext iwc) throws Exception {
        Form form = new Form();
        add(form);
        TextInput sourceInput = new TextInput(PARAM_SOURCE);
        form.add(sourceInput);
        form.add(new SubmitButton("Fetch"));
        if (iwc.isParameterSet(PARAM_SOURCE)) {
            String sourceURL = iwc.getParameter(PARAM_SOURCE);
            getRSSFetcher(iwc).saveLinksAndHeadlines(sourceURL);
        }
    }
	
    public String getBundleIdentifier(){
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

}
