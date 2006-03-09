/*
 * $Id: RSSSource.java,v 1.5 2006/03/09 12:59:57 eiki Exp $
 * Created on Feb 27, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.data;

import com.idega.data.IDOEntity;


/**
 * 
 *  Last modified: $Date: 2006/03/09 12:59:57 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.5 $
 */
public interface RSSSource extends IDOEntity {

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#getName
	 */
	public String getName();

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#getTitle
	 */
	public String getTitle();

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#setTitle
	 */
	public void setTitle(String title);

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#setSourceURL
	 */
	public void setSourceURL(String source);

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#getSourceURL
	 */
	public String getSourceURL();

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#setLocalSourceURI
	 */
	public void setLocalSourceURI(String source);

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#getLocalSourceURI
	 */
	public String getLocalSourceURI();

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#equals
	 */
	public boolean equals(Object obj);

	/**
	 * @see com.idega.block.rss.data.RSSSourceBMPBean#toString
	 */
	public String toString();
}
