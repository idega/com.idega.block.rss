package com.idega.block.rss.business;


public interface RSSFetcherHome extends com.idega.business.IBOHome
{
 public RSSFetcher create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}