package com.idega.block.rss.business;


public interface RSSFetcher extends com.idega.business.IBOService
{
 public java.util.Collection getLinksAndHeadlines(java.lang.String p0) throws java.rmi.RemoteException;
 public void saveLinksAndHeadlines(String sourceURL) throws java.rmi.RemoteException;
}
