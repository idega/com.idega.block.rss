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
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

/**
 * An editor for available rss feeds
 * @author <a href="mailto:jonas@idega.is>Jonas K. Blandon</a>
 */
public class RSSSourceDefWindow extends IWAdminWindow {
	
	private final static String PARAM_SOURCE = "rss_url";
	private final static String PARAM_NAME = "rss_name";
	private final static String PARAM_REMOVE = "rss_remove";
	
	//TODO localize all strings
	public RSSSourceDefWindow() {
		super();
	}
	
	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = this.getResourceBundle(iwc);
		setTitle(iwrb.getLocalizedString("edit.rss.sources.title","Edit Source Definition"));
		RSSBusiness business = getRSSBusiness(iwc);
		// handle add/delete
		String actionMsg = null;
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
				actionMsg = iwrb.getLocalizedString("error.could.not.be.added","Error: RSS Source could not be added");
			} else {
				actionMsg =  iwrb.getLocalizedString("success.added","RSS Source \"" + rssName + "\" added");
			}
		 } else if (iwc.isParameterSet(PARAM_REMOVE)) {
			String rssSourceIdStr = iwc.getParameter(PARAM_REMOVE);
			int rssSourceId = -1;
			try {
				rssSourceId = Integer.parseInt(rssSourceIdStr);
			} catch(NumberFormatException e) {
				System.err.println("Could not get id of source from parameter");
				e.printStackTrace();
			}
			boolean ok = false;
			if(rssSourceId!=-1) {
				//System.out.println("Deleting rss source: " + rssSourceId);
				try {
					//String name = business.getRSSSourceBySourceId(rssSourceId).getName();
					ok = business.removeSourceById(rssSourceId);
					actionMsg =  iwrb.getLocalizedString("success.deleted","RSS Source deleted");
				} catch (RemoteException e) {
					e.printStackTrace();
					actionMsg =  iwrb.getLocalizedString("error.could.not.delete","Deletion error, RSS Source not deleted");
				}
			}
			if(!ok) {
				// url incorrect, not an RSS source or other internal error
				actionMsg = iwrb.getLocalizedString("error.could.not.remove","Error: RSS Source could not be removed");
			} else {
				actionMsg = iwrb.getLocalizedString("success.removed","RSS Source has been removed");
			}
		}
		
		PresentationObject addForm = createAddForm();
		PresentationObject removeForm = createRemoveForm(iwc);
		add(addForm);
		addBreak();
		add(removeForm);
		addBreak();
		add(getSyndic8Link());
		if(actionMsg!=null) {
			addBreak();
			add(iwrb.getLocalizedString("result.from.action","Result from last action: "));
			Text text = new Text(actionMsg);
			text.setBold();
			add(text);
		}
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
		nameInput.setSize(25);
		addForm.add(nameText);
		addForm.addBreak();
		addForm.add(nameInput);
		addForm.addBreak();

		Text sourceText = new Text("URL for RSS Source");
		TextInput sourceInput = new TextInput(PARAM_SOURCE);
		sourceInput.setSize(50);
		addForm.add(sourceText);
		addForm.addBreak();
		addForm.add(sourceInput);
		addForm.addBreak();
		addForm.add(new SubmitButton("Add"));
		return addForm;
	}
	
	private PresentationObject createSourceMenu(String name, IWContext iwc) {
		DropdownMenu menu = new DropdownMenu( name );
		menu.addMenuElement( "", "Select source to remove:" );
		try {
			RSSBusiness business = getRSSBusiness(iwc);
			List sources = business.getAllRSSSources();
			for (Iterator loop = sources.iterator(); loop.hasNext();) {
				RSSSource element = (RSSSource) loop.next();
				String sourceName = element.getName();
				Object sourceId = element.getPrimaryKey();
				menu.addMenuElement( sourceId.toString(), sourceName );
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return menu;
	}
	
	private PresentationObject getSyndic8Link() {
		Link link = new Link(new Text("www.Syndic8.com"), "http://www.syndic8.com/");
		link.setTarget(Link.TARGET_NEW_WINDOW);
		Text text1 = new Text("To search for RSS sources, go to ");
		Text text2 = new Text(" and copy a source URL from there.");
		PresentationObjectContainer container = new PresentationObjectContainer();
		container.add(text1);
		container.add(link);
		container.add(text2);
		return container;
	}
	
	/**
	 * Gets a RSSBusiness instance from a IWContext, used by the presentation classes
	 * @param iwc The IWContext
	 * @return A RSSBusiness instance
	 * @throws RemoteException
	 */
	public RSSBusiness getRSSBusiness(IWContext iwc) throws RemoteException{        
		return (RSSBusiness) IBOLookup.getServiceInstance(iwc, RSSBusiness.class);        
	}
}
