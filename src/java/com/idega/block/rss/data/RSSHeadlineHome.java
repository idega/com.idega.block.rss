package com.idega.block.rss.data;


public interface RSSHeadlineHome extends com.idega.data.IDOHome
{
 public RSSHeadline create() throws javax.ejb.CreateException;
 public RSSHeadline findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findHeadlines(java.lang.String p0)throws javax.ejb.FinderException;

}