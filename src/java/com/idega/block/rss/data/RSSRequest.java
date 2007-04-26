/*
 * $Id: RSSRequest.java,v 1.5 2007/04/26 18:58:49 justinas Exp $
 * Created on Sep 13, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.data;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * @see com.idega.block.rss.business.RSSProducer
 * 
 *  Last modified: $Date: 2007/04/26 18:58:49 $ by $Author: justinas $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.5 $
 */
public class RSSRequest {
	
	String URI = null;
	String identifier = null;
	String extraUri = null;
	HttpServletRequestWrapper requestWrapped = null;
	HttpServletRequest request = null;
	HttpServletResponse response = null;
//	ServletContext servletContext = null; 
	
	public RSSRequest(HttpServletRequest request, HttpServletResponse response) {
		super();
		this.request = request;
		this.response = response;
		
		HttpServletRequestWrapper requestWrapped = new HttpServletRequestWrapper(request);
		setRequestWrapped(requestWrapped);
		
		setURI(requestWrapped.getRequestURI());
		
		if(getURI()!=null){
			
			try {
				int rssPartIndex = getURI().indexOf("/rss/");
				String identifierPartAndRest = getURI().substring(rssPartIndex+5);
				int firstSlash = identifierPartAndRest.indexOf("/");
				
				if(firstSlash>0){
					setIdentifier(identifierPartAndRest.substring(0,firstSlash));
					setExtraUri(identifierPartAndRest.substring(getIdentifier().length()+1));
				}
				else{
					setIdentifier(identifierPartAndRest);
				}
			} catch (IndexOutOfBoundsException e) {
				//no sweat...
			}
			
		}
		
	}
	
	/**
	 * @return Returns the extraUri. /rss/theidentifier/"extrauri"
	 */
	public String getExtraUri() {
		return extraUri;
	}
	/**
	 * @param extraUri The extraUri to set.
	 */
	public void setExtraUri(String extraUri) {
		this.extraUri = extraUri;
	}
	/**
	 * @return Returns the identifier. /rss/"theidentifier"/...
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * @param identifier The identifier to set.
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	/**
	 * @return Returns the request.
	 */
	public HttpServletRequest getRequest() {
		return request;
	}
	/**
	 * @param request The request to set.
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	/**
	 * @return Returns the response.
	 */
	public HttpServletResponse getResponse() {
		return response;
	}
	/**
	 * @param response The response to set.
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	/**
	 * @return Returns the uRI.
	 */
	public String getURI() {
		return URI;
	}
	/**
	 * @param uri The uRI to set.
	 */
	public void setURI(String uri) {
		URI = uri;
	}

	/**
	 * @return Returns the requestWrapped. A handy methods wrapper for a request object
	 */
	public HttpServletRequestWrapper getRequestWrapped() {
		return requestWrapped;
	}

	/**
	 * @param requestWrapped The requestWrapped to set.
	 */
	public void setRequestWrapped(HttpServletRequestWrapper requestWrapped) {
		this.requestWrapped = requestWrapped;
	}

//	public ServletContext getServletContext() {
//		return servletContext;
//	}
//
//	public void setServletContext(ServletContext servletContext) {
//		this.servletContext = servletContext;
//	}

	

}
