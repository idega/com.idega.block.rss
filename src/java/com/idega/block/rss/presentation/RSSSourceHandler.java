/*
 * Created on 21.10.2003
 */
package com.idega.block.rss.presentation;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import com.idega.block.rss.business.RSSBusiness;
import com.idega.block.rss.business.RSSBusinessBean;
import com.idega.block.rss.data.RSSSource;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.GenericButton;

/**
 * A property handler for adding rss sources
 * @author <a href="mailto:jonas@idega.is>Jonas K. Blandon</a>
 */
public class RSSSourceHandler implements ICPropertyHandler {
	private static String IW_BUNDLE_IDENTIFIER = "com.idega.block.rss";
	
	/* (non-Javadoc)
	 * @see com.idega.builder.handler.ICPropertyHandler#getDefaultHandlerTypes()
	 */
	public List getDefaultHandlerTypes() {
		return null;
	}
	
	private PresentationObject createSourceMenu(String name, String stringValue, IWContext iwc) {
		IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		
		DropdownMenu menu = new DropdownMenu( name );
		menu.addMenuElement( "-1", iwrb.getLocalizedString("select.rss.source","Select rss source to show:" ));
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
	 * @see com.idega.builder.handler.ICPropertyHandler#getHandlerObject(java.lang.String, java.lang.String, com.idega.presentation.IWContext)
	 */
	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc) {
		//System.out.println("Handling property rss source, name=[" + name + "], value=[" + stringValue + "]");
		IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		
		// create view
		Table result = new Table();
		int row = 1;
		PresentationObject menu = createSourceMenu(name, stringValue, iwc);
		result.add(menu, 1, row++);
		
		String textOnButton = iwrb.getLocalizedString("edit.rss.sources","Edit RSS Sources");
		GenericButton editButton = new GenericButton(textOnButton,textOnButton);
		editButton.setWindowToOpen(RSSSourceDefWindow.class);
		result.addBreak();
		result.add(editButton);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.idega.builder.handler.ICPropertyHandler#onUpdate(java.lang.String[], com.idega.presentation.IWContext)
	 */
	public void onUpdate(String[] values, IWContext iwc) {
		if(values!=null && values.length>0) {
			String rssSourceId = values[0];
			System.out.println("Selected rss source \"" + rssSourceId + "\"");
		}
	}
}
