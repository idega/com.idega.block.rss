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
import com.idega.business.IBOLookup;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.Window;

/**
 * @author jonas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RSSSourceDefWindow extends Window {
	public RSSSourceDefWindow() {
		super();
	}
	
	public void main(IWContext iwc) throws Exception {
		setTitle("Edit Source Definition");
		RSSBusiness business = RSSBusinessBean.getRSSBusiness(iwc);
		// handle add/delete
		Text actionMsg = null;
		if (iwc.isParameterSet(PARAM_SOURCE)) {
			String rssName = iwc.getParameter(PARAM_NAME);
			String rssSourceURL = iwc.getParameter(PARAM_SOURCE);
			boolean ok = false;
			try {
				ok = business.createNewRSSSource(rssName, rssSourceURL);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			if(!ok) {
				// url incorrect, not an RSS source or other internal error
				actionMsg = new Text("Error: RSS Source could not be added");
				actionMsg.setBold();
			} else {
				actionMsg = new Text("RSS Source \"" + rssName + "\" added");
			}
		 } else if (iwc.isParameterSet(PARAM_REMOVE)) {
			String rssSourceId = iwc.getParameter(PARAM_REMOVE);
			System.out.println("Deleting rss source: " + rssSourceId);
			boolean ok = false;
			try {
				ok = business.removeSourceById(rssSourceId);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			if(!ok) {
				// url incorrect, not an RSS source or other internal error
				actionMsg = new Text("Error: RSS Source could not be removed");
				actionMsg.setBold();
			} else {
				actionMsg = new Text("RSS Source has been removed");
			}
		}
		
		PresentationObject addForm = createAddForm();
		PresentationObject removeForm = createRemoveForm(iwc);
		add(addForm);
		add(removeForm);
	}
	
	private PresentationObject createRemoveForm(IWContext iwc) {
		PresentationObject sourceMenu = createSourceMenu(PARAM_REMOVE, iwc);
		Form removeForm = new Form();
		removeForm.add(sourceMenu);
		removeForm.add(new SubmitButton("Delete Selected RSS Source"));
		return removeForm;
	}


	private PresentationObject createAddForm() {
		Form addForm = new Form();
		Text nameText = new Text("Name for RSS Source");
		TextInput nameInput = new TextInput(PARAM_NAME);
		addForm.add(nameText);
		addForm.add(nameInput);

		Text sourceText = new Text("URL for RSS Source");
		TextInput sourceInput = new TextInput(PARAM_SOURCE);
		addForm.add(sourceText);
		addForm.add(sourceInput);
		addForm.add(new SubmitButton("Add"));
		return addForm;
	}
	
	private PresentationObject createSourceMenu(String name, IWContext iwc) {
		DropdownMenu menu = new DropdownMenu( name );
		menu.addMenuElement( "", "Select source to remove:" );
		try {
			RSSBusiness business = RSSBusinessBean.getRSSBusiness(iwc);
			List sources = business.getAllRSSSources();
			for (Iterator loop = sources.iterator(); loop.hasNext();) {
				RSSSource element = (RSSSource) loop.next();
				String sourceName = element.getName();
				String sourceUrl = element.getSourceURL();
				menu.addMenuElement( sourceUrl, sourceName );
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return menu;
	}

	private final static String PARAM_SOURCE = "rss_url";
	private final static String PARAM_NAME = "rss_name";
	private final static String PARAM_REMOVE = "rss_remove";
}
