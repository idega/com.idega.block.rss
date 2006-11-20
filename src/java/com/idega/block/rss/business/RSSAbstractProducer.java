/*
 * $Id: RSSAbstractProducer.java,v 1.1.2.2 2006/11/20 14:28:56 eiki Exp $
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.httpclient.HttpException;

import com.idega.block.rss.data.RSSRequest;
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.slide.business.IWSlideService;
import com.idega.slide.business.IWSlideSession;

/**
 * @see com.idega.block.rss.business.RSSProducer
 * 
 *  Last modified: $Date: 2006/11/20 14:28:56 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1.2.2 $
 */
public abstract class RSSAbstractProducer implements RSSProducer {

	public static final String RSS_CONTENT_TYPE = "application/rss+xml";
	protected IWSlideService service;
	protected IWSlideSession session;

	public RSSAbstractProducer() {
	}

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
	 * Checks if the file or folder in slide the uri points to exists or not
	 * @param URI
	 * @return true if the file exists, checks as root
	 */
	public boolean existsInSlide(String URI,RSSRequest rssRequest){
		
		try {
			return getIWSlideService(rssRequest).getExistence(URI);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	/**
	 * 
	 * @param URI
	 * @return true if the uri points to a folder in slide or false if it does not or the user has no access priviledges to it
	 */
	public boolean isAFolderInSlide(String URI,RSSRequest rssRequest){
	
		try {
			return getIWSlideSession(rssRequest).isFolder(URI);
		} catch (RemoteException e) {
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
	public String getServerURLWithURI(String URI, RSSRequest rssRequest){
		HttpServletRequestWrapper wrapped = rssRequest.getRequestWrapped();
		
		return "http://"+wrapped.getServerName()+":"+wrapped.getServerPort()+URI;
	}
	
	
	/**
	 * Gets a RSSBusiness instance
	 * 
	 * @return A RSSBusiness instance
	 * @throws RemoteException
	 */
	public RSSBusiness getRSSBusiness() throws RemoteException {
		return (RSSBusiness) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), RSSBusiness.class);
	}
	
	
	/**
	 * Gets a IWSlideService instance
	 * 
	 * @return A IWSlideService instance
	 * @throws RemoteException
	 */
	public IWSlideService getIWSlideService(RSSRequest rssRequest) throws RemoteException {
		service = (IWSlideService) IBOLookup.getServiceInstance(IWMainApplication.getIWMainApplication(rssRequest.getRequest().getSession().getServletContext()).getIWApplicationContext(), IWSlideService.class);
		return service;
	}
	
	/**
	 * Gets a IWSlideSession instance
	 * 
	 * @return A IWSlideSession instance
	 * @throws RemoteException
	 */
	public IWSlideSession getIWSlideSession(RSSRequest rssRequest) throws RemoteException {
		session = (IWSlideSession) IBOLookup.getSessionInstance(rssRequest.getRequest().getSession(), IWSlideSession.class);
		return session;
	}
	
	/**
	 * Fetches the IWApplicationContext using the RSSRequest and IWMainApplication
	 * @param rssRequest
	 * @return IWApplicationContext
	 */
	public IWApplicationContext getIWApplicationContext(RSSRequest rssRequest){
		return IWMainApplication.getIWMainApplication(rssRequest.getRequest().getSession().getServletContext()).getIWApplicationContext();
	}
	
}
