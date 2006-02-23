/*
 * $Id: RSSBusinessHome.java,v 1.3 2006/02/23 18:42:02 eiki Exp $
 * Created on Feb 22, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2006/02/23 18:42:02 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.3 $
 */
public interface RSSBusinessHome extends IBOHome {

	public RSSBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
