/*
 * Created on 21.10.2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.idega.block.rss.presentation;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import com.idega.block.rss.business.RSSBusiness;
import com.idega.block.rss.data.RSSSource;
import com.idega.builder.handler.PropertyHandler;
import com.idega.business.IBOLookup;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

/**
 * @author jonas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RSSSourceHandler implements PropertyHandler {

	/* (non-Javadoc)
	 * @see com.idega.builder.handler.PropertyHandler#getDefaultHandlerTypes()
	 */
	public List getDefaultHandlerTypes() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.builder.handler.PropertyHandler#getHandlerObject(java.lang.String, java.lang.String, com.idega.presentation.IWContext)
	 */
	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc) {
		System.out.println("Handling property rss source, name=[" + name + "], value=[" + stringValue + "]");
		Table result = new Table();
		DropdownMenu menu = new DropdownMenu( name );
		menu.addMenuElement( "", "Select :" );
		result.add(menu, 1, 1);
		Form form = new Form();
		result.add(form, 1, 2);
		
		// show inputfields for adding a source
		Text nameText = new Text("Name for RSS Source");
		TextInput nameInput = new TextInput(PARAM_NAME);
		form.add(nameText);
		form.add(nameInput);

		Text sourceText = new Text("URL for RSS Source");
		TextInput sourceInput = new TextInput(PARAM_SOURCE);
		form.add(sourceText);
		form.add(sourceInput);
		form.add(new SubmitButton("Add"));
		if (iwc.isParameterSet(PARAM_SOURCE)) {
			String rssName = iwc.getParameter(PARAM_NAME);
			String rssSourceURL = iwc.getParameter(PARAM_SOURCE);
			boolean ok = false;
			try {
				ok = getRSSBusiness(iwc).addSource(rssName, rssSourceURL);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			Text msg;
			if(!ok) {
				// url incorrect, not an RSS source or other internal error
				msg = new Text("Error: RSS Source could not be added");
				msg.setBold();
			} else {
				msg = new Text("RSS Source \"" + rssName + "\" added");
			}
			result.add(msg, 1, 3);
		}
		
		//	show list of existing sources
		try {
			RSSBusiness business = getRSSBusiness(iwc);
			List sources = business.getSources();
			//int row = 1;
			for (Iterator loop = sources.iterator(); loop.hasNext();) {
				RSSSource element = (RSSSource) loop.next();
				String sourceName = element.getName();
				String sourceUrl = element.getSourceURL();
				menu.addMenuElement( sourceUrl, sourceName );
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		menu.setSelectedElement( stringValue );
		 
		return result;
	}
	
	private RSSBusiness getRSSBusiness(IWContext iwc) throws RemoteException {        
		return (RSSBusiness) IBOLookup.getServiceInstance(iwc, RSSBusiness.class);        
	}

	/* (non-Javadoc)
	 * @see com.idega.builder.handler.PropertyHandler#onUpdate(java.lang.String[], com.idega.presentation.IWContext)
	 */
	public void onUpdate(String[] values, IWContext iwc) {
		if(values!=null && values.length>0) {
			String rssName = values[0];
			System.out.println("Selected rss source \"" + rssName + "\"");
		}
	}
	
	private static String PARAM_SOURCE = "rss_url";
	private static String PARAM_NAME = "rss_name";

}
