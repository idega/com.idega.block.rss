package com.idega.block.rss.business;


public interface RSSBusiness extends com.idega.business.IBOService
{
 public boolean createNewRSSSource(java.lang.String p0,java.lang.String p1) throws java.rmi.RemoteException;
 public java.util.List getAllRSSSources() throws java.rmi.RemoteException;
 public java.util.Collection getHeadlinesByRSSSource(com.idega.block.rss.data.RSSSource p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection getRSSHeadlinesByRSSSource(com.idega.block.rss.data.RSSSource p0) throws java.rmi.RemoteException;
 public com.idega.block.rss.data.RSSSource getRSSSourceBySourceId(int p0) throws java.rmi.RemoteException;
 public boolean removeSourceById(int p0) throws java.rmi.RemoteException;
}
