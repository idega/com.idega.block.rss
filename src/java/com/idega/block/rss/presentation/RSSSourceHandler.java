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
import com.idega.block.rss.business.RSSBusinessBean;
import com.idega.block.rss.data.RSSSource;
import com.idega.builder.handler.PropertyHandler;
import com.idega.business.IBOLookup;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.GenericButton;

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
	
	private PresentationObject createSourceMenu(String name, String stringValue, IWContext iwc) {
		DropdownMenu menu = new DropdownMenu( name );
		menu.addMenuElement( "", "Select :" );
		try {
			RSSBusiness business = RSSBusinessBean.getRSSBusiness(iwc);
			List sources = business.getAllRSSSources();
			for (Iterator loop = sources.iterator(); loop.hasNext();) {
				RSSSource rssSource = (RSSSource) loop.next();
				String sourceName = rssSource.getName();
				String sourceId = rssSource.getPrimaryKey().toString();
				menu.addMenuElement( sourceId, sourceName );
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		menu.setSelectedElement( stringValue );
		return menu;
	}

	/* (non-Javadoc)
	 * @see com.idega.builder.handler.PropertyHandler#getHandlerObject(java.lang.String, java.lang.String, com.idega.presentation.IWContext)
	 */
	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc) {
		System.out.println("Handling property rss source, name=[" + name + "], value=[" + stringValue + "]");
		
		// create view
		Table result = new Table();
		int row = 1;
		PresentationObject menu = createSourceMenu(name, stringValue, iwc);
		result.add(menu, 1, row++);
		
		GenericButton editButton = new GenericButton("Edit RSS Sources", "Edit RSS Sources");
		editButton.setWindowToOpen(RSSSourceDefWindow.class);
		result.addBreak();
		result.add(editButton);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.idega.builder.handler.PropertyHandler#onUpdate(java.lang.String[], com.idega.presentation.IWContext)
	 */
	public void onUpdate(String[] values, IWContext iwc) {
		if(values!=null && values.length>0) {
			String rssSourceId = values[0];
			System.out.println("Selected rss source \"" + rssSourceId + "\"");
		}
	}
}
