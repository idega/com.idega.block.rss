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
import com.idega.block.rss.data.RSSSource;
import com.idega.business.IBOLookup;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;


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
    protected RSSBusiness getRSSBusiness(IWContext iwc) throws RemoteException{        
        return (RSSBusiness) IBOLookup.getServiceInstance(iwc, RSSBusiness.class);        
    }
    
    public void main(IWContext iwc) throws Exception {
        Form form = new Form();
        add(form);
        TextInput sourceInput = new TextInput(PARAM_SOURCE);
        form.add(sourceInput);
        form.add(new SubmitButton("Fetch"));
        if (iwc.isParameterSet(PARAM_SOURCE)) {
            String sourceURL = iwc.getParameter(PARAM_SOURCE);
            getRSSBusiness(iwc).saveLinksAndHeadlines(sourceURL);
        }
        
        // Add a table with existing rss sources
		try {
			Table t = new Table();
			add(t);
			RSSBusiness business = getRSSBusiness(iwc);
			Collection sources = business.getSources();
			int row = 1;
			for (Iterator loop = sources.iterator(); loop.hasNext();) {
				RSSSource element = (RSSSource) loop.next();
				String name = element.getName();
				String url = element.getSourceURL();
				Link link = new Link(name, url);
				t.add(link, 1, row++);
			}
		} catch (RemoteException e) {
			add("Länkhämtningsfel");
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
