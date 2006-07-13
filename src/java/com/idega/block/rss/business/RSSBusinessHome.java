package com.idega.block.rss.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface RSSBusinessHome extends IBOHome {
	public RSSBusiness create() throws CreateException, RemoteException;
}