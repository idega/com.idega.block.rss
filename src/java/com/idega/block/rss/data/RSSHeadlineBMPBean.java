/*
 * Created on 2003-jun-05
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.rss.data;

import javax.ejb.RemoveException;

import com.idega.data.GenericEntity;
import com.idega.data.IDORelationshipException;

/**
 * @author WMGOBOM
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RSSHeadlineBMPBean extends GenericEntity implements RSSHeadline {

	/* (non-Javadoc)
	 * @see com.idega.data.IDOLegacyEntity#getEntityName()
	 */
	public String getEntityName() {
		return "RSS_HEADLINE";
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOLegacyEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
    // Skapa tabellkolumner i db för bönan
        //  Kolumnnamn för primärnyckel
        addAttribute(getIDColumnName());
        //      kolumnnamn, kolumnbesk, Kolumntyp(Varchar 255)
        addAttribute("LINK_URL", "Link url", String.class);
        //setUnique("LINK_URL", true); // got keysize to big for index using interbase
        addAttribute("HEADLINE", "Link text", String.class);
		addManyToManyRelationShip(RSSSource.class);
	}
    
    /**
     * @return
     */
    public String getHeadline() {
        return getStringColumnValue("HEADLINE");
    }

    /**
     * @return
     */
    public String getLink() {
        return getStringColumnValue("LINK_URL");
    }

    /**
     * @param string
     */
    public void setHeadline(String headline) {
        setColumn("HEADLINE", headline);
    }

    /**
     * @param string
     */
    public void setLink(String url) {
        setColumn("LINK_URL", url);
    }
    
    /**
     * Overloaded to remove relation with RSSSource
     */
    public void remove() throws RemoveException {
    	System.out.println("Removing headline: " + getHeadline());
    	try {
    		System.out.println("Removing RSSSource relation");
    		idoRemoveFrom(RSSSource.class);
    	} catch(IDORelationshipException e) {
    		e.printStackTrace();
    		throw new RemoveException("Could not remove relationship with RSSSource");
    	}
    	super.remove();
    }
    
    /* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj instanceof RSSHeadline) {
			return getLink().equals(((RSSHeadline)obj).getLink());
		} else {
			return false;
		}
	}
	
	
	
	public String toString() {
		return getHeadline();
	}
}
