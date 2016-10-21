/*
 * $Id: RSSAbstractProducer.java,v 1.6 2009/05/15 07:23:44 valdas Exp $
 * Created on Sep 13, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.business;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequestWrapper;

import com.idega.block.rss.data.RSSRequest;
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.repository.RepositoryService;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.expression.ELUtil;

/**
 * @see com.idega.block.rss.business.RSSProducer
 *
 *  Last modified: $Date: 2009/05/15 07:23:44 $ by $Author: valdas $
 *
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.6 $
 */
public abstract class RSSAbstractProducer implements RSSProducer {

	public static final String RSS_CONTENT_TYPE = "application/rss+xml";

	public RSSAbstractProducer() {
	}

	@Override
	public abstract void handleRSSRequest(RSSRequest rssRequest) throws IOException;

	/**
	 * Sends the request to a new URI
	 * @param URI
	 * @throws IOException
	 * @throws ServletException
	 */
	public void dispatch(String URI, RSSRequest rssRequest) throws IOException, ServletException{
		rssRequest.getRequestWrapped().getRequestDispatcher(URI).forward(rssRequest.getRequest(), rssRequest.getResponse());
	}

	/**
	 * Sets the requests contenttype to "application/rss+xml". DO NOT USE if you then call dispatch.
	 * @param rssRequest
	 */
	public void setAsRSSContentType(RSSRequest rssRequest){
		rssRequest.getResponse().setContentType(RSS_CONTENT_TYPE);
	}

	public String getRSSContentType(){
		return RSS_CONTENT_TYPE;
	}

	/**
	 * Sets the requests contenttype to "text/xml". DO NOT USE if you then call dispatch.
	 * @param rssRequest
	 */
	public void setAsXMLContentType(RSSRequest rssRequest){
		rssRequest.getResponse().setContentType("text/xml");
	}

	/**
	 * Checks if the file or folder in repository the uri points to exists or not
	 * @param URI
	 * @return true if the file exists, checks as root
	 */
	public boolean existsInRepository(String URI,RSSRequest rssRequest){
		try {
			return getRepository().getExistence(URI);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 *
	 * @param URI
	 * @return true if the uri points to a folder in repository or false if it does not or the user has no access priviledges to it
	 */
	public boolean isAFolderInRepository(String URI,RSSRequest rssRequest){
		try {
			return getRepository().isFolder(URI);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 *
	 * @param URI
	 * @param rssRequest
	 * @return the full URL with the http protocol, servername and port with the URI suffixed
	 */
	public String getServerURLWithURI(String uri, RSSRequest rssRequest){
		HttpServletRequestWrapper wrapped = rssRequest.getRequestWrapped();

		IWContext iwc = CoreUtil.getIWContext();
		String serverURL = iwc == null ? null : CoreUtil.getServerURL(iwc.getRequest());
		serverURL = serverURL == null ? "http://"+wrapped.getServerName() + CoreConstants.COLON + wrapped.getServerPort() + CoreConstants.SLASH : serverURL;

		if (uri.startsWith(CoreConstants.SLASH)) {
			uri = uri.substring(1);
		}
		return serverURL + uri;
	}


	/**
	 * Gets a RSSBusiness instance
	 *
	 * @return A RSSBusiness instance
	 * @throws RemoteException
	 */
	public RSSBusiness getRSSBusiness() throws RemoteException {
		return IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), RSSBusiness.class);
	}

	/**
	 * Fetches the IWApplicationContext using the RSSRequest and IWMainApplication
	 * @param rssRequest
	 * @return IWApplicationContext
	 */
	public IWApplicationContext getIWApplicationContext(RSSRequest rssRequest){
		return IWMainApplication.getIWMainApplication(rssRequest.getRequest().getSession().getServletContext()).getIWApplicationContext();
	}

	public IWContext getIWContext(RSSRequest rss){
		IWContext iwc = null;
		try {
			iwc = IWContext.getInstance();
		} catch (Exception e) {
//		e.printStackTrace();
		}

		if(iwc==null){
			iwc = new IWContext(rss.getRequest(), rss.getResponse(), rss.getRequest().getSession().getServletContext());
		}
		return iwc;
	}

	protected RepositoryService getRepository() {
		return ELUtil.getInstance().getBean(RepositoryService.class);
	}
}