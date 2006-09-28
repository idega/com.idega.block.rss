/*
 * $Id: NoSuchRSSProducerException.java,v 1.1.2.1 2006/09/28 18:20:08 eiki Exp $
 * Created on Sep 13, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.business;

/**
 * An exception thrown when a RSSProducer lookup fails.
 * 
 *  Last modified: $Date: 2006/09/28 18:20:08 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1.2.1 $
 */
public class NoSuchRSSProducerException extends Exception {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchRSSProducerException(String identifier) {
		super("No RSSProducer found for the identifier: "+identifier);
	}

}
