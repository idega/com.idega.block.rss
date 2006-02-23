/*
 * $Id: RSSSource.java,v 1.4 2006/02/23 18:42:02 eiki Exp $
 * Created on Feb 23, 2006
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
 *  Last modified: $Date: 2006/02/23 18:42:02 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.4 $
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
