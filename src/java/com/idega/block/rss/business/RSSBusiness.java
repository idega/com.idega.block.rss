/*
 * $Id: RSSBusiness.java,v 1.5 2004/09/10 00:07:24 eiki Exp $
 * Created on Sep 9, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import javax.ejb.FinderException;
import com.idega.block.rss.data.RSSSource;
import com.idega.business.IBOService;

/**
 * 
 *  Last modified: $Date: 2004/09/10 00:07:24 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.5 $
 */
public interface RSSBusiness extends IBOService{
/**
 * @see com.idega.block.rss.business.RSSBusinessBean#getRSSSourceBySourceId
 */
public RSSSource getRSSSourceBySourceId(int sourceId) throws java.rmi.RemoteException;
/**
 * @see com.idega.block.rss.business.RSSBusinessBean#getRSSHeadlinesByRSSSource
 */
public Collection getRSSHeadlinesByRSSSource(RSSSource rssSource) throws java.rmi.RemoteException;
/**
 * @see com.idega.block.rss.business.RSSBusinessBean#getAllRSSSources
 */
public List getAllRSSSources() throws java.rmi.RemoteException;
/**
 * @see com.idega.block.rss.business.RSSBusinessBean#createNewRSSSource
 */
public boolean createNewRSSSource(String name, String url) throws java.rmi.RemoteException;
/**
 * @see com.idega.block.rss.business.RSSBusinessBean#getHeadlinesByRSSSource
 */
public Collection getHeadlinesByRSSSource(RSSSource rssSource) throws RemoteException,FinderException,java.rmi.RemoteException;
/**
 * @see com.idega.block.rss.business.RSSBusinessBean#removeSourceById
 */
public boolean removeSourceById(int id) throws java.rmi.RemoteException;

}
