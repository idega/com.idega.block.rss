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
        addAttribute("SOURCE_URL", "Source url", String.class);
	}
    
    public Collection ejbFindHeadlines(String sourceURL) throws FinderException{
        IDOQuery query = idoQueryGetSelect();
        query.appendWhereEqualsQuoted("SOURCE_URL", sourceURL);
        query.appendOrderByDescending(getIDColumnName());      
        return super.idoFindPKsByQuery(query);
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
     * @return
     */
    public String getSourceURL() {
        return getStringColumnValue("SOURCE_URL");
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
     * @param string
     */
    public void setSourceURL(String source) {
    	System.out.println("setSourceUrl called with " + source);
        setColumn("SOURCE_URL", source);
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

}
