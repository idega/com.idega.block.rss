/*
 * Created on 2003-jun-05
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.rss.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOQuery;

/**
 * @author WMGOBOM
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RSSSourceBMPBean extends GenericEntity implements RSSSource {

	/* (non-Javadoc)
	 * @see com.idega.data.IDOLegacyEntity#getEntityName()
	 */
	public String getEntityName() {
		return "RSS_SOURCE";
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOLegacyEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
        addAttribute(getIDColumnName());
		addAttribute("NAME", "Name of RSS Source", String.class);
        addAttribute("SOURCE_URL", "RSS Source URL", String.class);
		//setUnique("SOURCE_URL", true);
	}
    
    public Collection ejbFindSources() throws FinderException {
        IDOQuery query = idoQueryGetSelect();
        return super.idoFindAllIDsOrderedBySQL("NAME");
    }
    
	public Collection ejbFindSourceByName(String name) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEqualsQuoted("name", name);      
		return super.idoFindPKsByQuery(query);
	}
    
    /**
     * @return
     */
    public String getName() {
        return getStringColumnValue("NAME");
    }

    /**
     * @return
     */
    public String getSourceURL() {
        return getStringColumnValue("SOURCE_URL");
    }
    
	/**
	 * @return
	 */
	public void setName(String name) {
		setColumn("NAME", name);
	}
    
    /**
     * @param string
     */
    public void setSourceURL(String source) {
        setColumn("SOURCE_URL", source);
    }
    
	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj instanceof RSSSource) {
			return getSourceURL().equals(((RSSSource)obj).getSourceURL());
		} else {
			return false;
		}
	}
}
