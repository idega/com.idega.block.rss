/*
 * $Id: RSSBusinessHome.java,v 1.2 2004/09/10 00:07:24 eiki Exp $
 * Created on Sep 9, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2004/09/10 00:07:24 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public interface RSSBusinessHome extends IBOHome {

	public RSSBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
