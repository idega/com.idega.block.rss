package com.idega.block.rss.business;


public interface RSSBusiness extends com.idega.business.IBOService,com.idega.idegaweb.IWBundleStartable
{
 public boolean addSource(java.lang.String p0,java.lang.String p1) throws java.rmi.RemoteException;
 public java.util.Collection getHeadlinesFromDB(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.List getHeadlinesFromRSS(java.lang.String p0) throws java.rmi.RemoteException;
 public java.util.Collection getLinksAndHeadlines(java.lang.String p0) throws java.rmi.RemoteException;
 public java.util.List getSources() throws java.rmi.RemoteException;
 public boolean saveLinksAndHeadlines(java.lang.String p0) throws java.rmi.RemoteException;
}
