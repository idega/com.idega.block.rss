package com.idega.block.rss.data;


public interface RSSSourceHome extends com.idega.data.IDOHome
{
 public RSSSource create() throws javax.ejb.CreateException;
 public RSSSource findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findSources()throws javax.ejb.FinderException;

}