/*
 * Created on 2003-jun-05
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.rss.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOQuery;
import com.idega.data.IDORelationshipException;

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
	}
    
    public Collection ejbFindSources() throws FinderException {
        //IDOQuery query = idoQueryGetSelect();
        List result = new ArrayList(super.idoFindAllIDsOrderedBySQL("NAME"));
        return result;
    }
    
	public Collection ejbFindSourceByName(String name) throws FinderException {
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEqualsQuoted("name", name);
		return super.idoFindPKsByQuery(query);
	}
	
	public Collection ejbFindSourceById(int id) throws FinderException{
		/*IDOQuery query = idoQueryGetSelect();
		query.appendWhereEquals(getIDColumnName(), id);*/
		Collection result = new ArrayList(1);
		result.add(idoFindOnePKByColumnBySQL(getIDColumnName(), Integer.toString(id)));
		return result;
	}
    
    public Collection getHeadlines() {
    	System.out.println("Getting headlines using relationship");
    	try {
			return idoGetRelatedEntities(RSSHeadline.class);
    	} catch(IDORelationshipException e) {
    		System.out.println("Couldn't find headlines for source " + toString());
			e.printStackTrace();
			return Collections.EMPTY_LIST;
    	}
    }
    
	public void addHeadline(RSSHeadline headline) {
		try {
			idoAddTo(headline);
		} catch(IDOAddRelationshipException e) {
			System.out.println("Could not add headline to source");
			e.printStackTrace();
		} 
	}
    public void removeHeadlines() {
		try {
			Collection headlines = getHeadlines();
			Iterator hIter = headlines.iterator();
			while(hIter.hasNext()) {
				RSSHeadline headline = (RSSHeadline) hIter.next();
				headline.remove();
			}
			idoRemoveFrom(RSSHeadline.class);
		} catch(Exception e) {
			System.out.println("Error deleting RSS source");
		}
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
    	if(source!=null && source.trim().length()>0) {
			setColumn("SOURCE_URL", source);
    	}
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
	
	public String toString() {
		return "[" + getPrimaryKeyValue() + "]" + getName() + "@" + getSourceURL();
	}
}
