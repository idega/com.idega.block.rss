/*
 * $Id$
 * Created on Feb 13, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.include.GlobalIncludeManager;


public class IWBundleStarter implements IWBundleStartable {

	public void start(IWBundle starterBundle) {
    GlobalIncludeManager includeManager = GlobalIncludeManager.getInstance();
    includeManager.addBundleStyleSheet("com.idega.block.rss", "/style/rss.css");
	}

	public void stop(IWBundle starterBundle) {
	}
}
