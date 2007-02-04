/*
 * $Id: RSSProducerRegistry.java,v 1.2 2007/02/04 20:42:26 valdas Exp $
 * Created on Sep 13, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.business;

import java.util.HashMap;
import java.util.Map;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.Singleton;

/**
 * @see com.idega.block.rss.business.RSSProducer
 * 
 *  Last modified: $Date: 2007/02/04 20:42:26 $ by $Author: valdas $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public class RSSProducerRegistry implements Singleton {
	
	
	protected Map rssProducersMap;
	public static String RSS_PRODUCER_FACTORY_APPLICATION_STORAGE_PARAM = "RSSProducerRegistry";
	
	
	private RSSProducerRegistry(){}
	
	public static RSSProducerRegistry getInstance(){
		IWApplicationContext iwac = IWMainApplication.getDefaultIWApplicationContext();
		
		RSSProducerRegistry factory = (RSSProducerRegistry)iwac.getApplicationAttribute(RSS_PRODUCER_FACTORY_APPLICATION_STORAGE_PARAM);
		if(factory==null){
			factory =new RSSProducerRegistry();
			iwac.setApplicationAttribute(RSS_PRODUCER_FACTORY_APPLICATION_STORAGE_PARAM,factory);
		}
		return factory;
	} 
	
	
	/**
	 * 
	 * @return the map of RSSProducers keyed by their identifiers
	 */
	public Map getRSSProducers() {
		if(this.rssProducersMap==null){
			this.rssProducersMap = new HashMap();
		}
		return this.rssProducersMap;
	}
	
	/**
	 * Replaces the current RSSProducers Map
	 * @param rssProducersMap
	 */
	public synchronized void setRSSProducers(Map rssProducersMap) {
		this.rssProducersMap = rssProducersMap;
	}
	
	/**
	 * 
	 * @param identifier the identifier part of an RSSProducer URI e.g. "/rss/identifier/extrauri"
	 * @param rssProducer An instance of a RSSProducer
	 */
	public synchronized void addRSSProducer(String identifier, RSSProducer rssProducer){
		getRSSProducers().put(identifier,rssProducer);
	}
	
	/**
	 * 
	 * @param identifer
	 * @return Looks up and return the RSSProducer instance registered to the identifier
	 * @throws NoSuchRSSProducerException if the RSSProducer is not found for the identifier
	 */
	public RSSProducer getRSSProducerByIdentifier(String identifier) throws NoSuchRSSProducerException{
		RSSProducer producer = (RSSProducer) getRSSProducers().get(identifier);
		if(producer==null){
			throw new NoSuchRSSProducerException(identifier);
		}
		return producer;
	}

}

