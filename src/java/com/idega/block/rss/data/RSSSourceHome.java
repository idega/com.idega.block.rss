/*
 * $Id: RSSSourceHome.java,v 1.5 2006/02/23 18:42:02 eiki Exp $
 * Created on Feb 23, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOHome;


/**
 * 
 *  Last modified: $Date: 2006/02/23 18:42:02 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.5 $
 */
public interface RSSSourceHome extends IDOHome {

	public RSSSource create() throws javax.ejb.CreateException;

	public RSSSource findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#ejbFindSources
	 */
	public Collection findSources() throws FinderException;

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#ejbFindSourceByName
	 */
	public RSSSource findSourceByName(String name) throws FinderException;

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#ejbFindSourceByURL
	 */
	public RSSSource findSourceByURL(String url) throws FinderException;

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#ejbFindSourceByLocalSourceURI
	 */
	public RSSSource findSourceByLocalSourceURI(String uri) throws FinderException;

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#ejbFindSourceById
	 */
	public RSSSource findSourceById(int id) throws FinderException;
}
