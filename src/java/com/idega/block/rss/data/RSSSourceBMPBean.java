/*
 * Created on 2003-jun-05
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.rss.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ejb.FinderException;
import com.idega.data.GenericEntity;

/**
 * 
 * 
 * Last modified: $Date: 2006/02/23 18:42:02 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.6 $
 */
public class RSSSourceBMPBean extends GenericEntity implements RSSSource {

	public static final String COLUMN_NAME = "NAME";
	public static final String COLUMN_LOCAL_SOURCE_URI = "LOCAL_SOURCE_URI";
	public static final String COLUMN_SOURCE_URL = "SOURCE_URL";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.IDOLegacyEntity#getEntityName()
	 */
	public String getEntityName() {
		return "RSS_SOURCE";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.IDOLegacyEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_NAME, "Name of RSS Source", String.class);
		addAttribute(COLUMN_SOURCE_URL, "RSS Source URL", String.class);
		addAttribute(COLUMN_LOCAL_SOURCE_URI, "RSS Local Source URI (no context) in Slide", String.class);
	}

	public Collection ejbFindSources() throws FinderException {
		// IDOQuery query = idoQueryGetSelect();
		List result = new ArrayList(super.idoFindAllIDsOrderedBySQL(COLUMN_NAME));
		return result;
	}

	public Integer ejbFindSourceByName(String name) throws FinderException {
		return (Integer) super.idoFindOnePKByColumnBySQL(COLUMN_NAME, name);
	}
	
	public Integer ejbFindSourceByURL(String url) throws FinderException {
		return (Integer) super.idoFindOnePKByColumnBySQL(COLUMN_SOURCE_URL, url);
	}
	
	public Integer ejbFindSourceByLocalSourceURI(String uri) throws FinderException {
		return (Integer) super.idoFindOnePKByColumnBySQL(COLUMN_LOCAL_SOURCE_URI, uri);
	}

	public Integer ejbFindSourceById(int id) throws FinderException {
		return (Integer) super.idoFindOnePKByColumnBySQL(getIDColumnName(),Integer.toString(id));
	}

	/**
	 * @return
	 */
	public String getName() {
		return getStringColumnValue(COLUMN_NAME);
	}



	/**
	 * @return
	 */
	public void setName(String name) {
		setColumn(COLUMN_NAME, name);
	}

	/**
	 * @param The real URL to the rss feed
	 */
	public void setSourceURL(String source) {
		setColumn(COLUMN_SOURCE_URL, source);
	}
	
	/**
	 * @return The real URL to the rss feed
	 */
	public String getSourceURL() {
		return getStringColumnValue(COLUMN_SOURCE_URL);
	}
	
	/**
	 * @param The URI (without context) to the local atom 1.0 xml file in slide
	 */
	public void setLocalSourceURI(String source) {
		setColumn(COLUMN_LOCAL_SOURCE_URI, source);
	}
	
	/**
	 * @return The URI (without context) to the local atom 1.0 xml file in slide
	 */
	public String getLocalSourceURI() {
		return getStringColumnValue(COLUMN_LOCAL_SOURCE_URI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.GenericEntity#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof RSSSource) {
			return getSourceURL().equals(((RSSSource) obj).getSourceURL());
		}
		else {
			return false;
		}
	}

	public String toString() {
		return "[" + getPrimaryKeyValue() + "]" + getName() + "@" + getSourceURL();
	}
}
