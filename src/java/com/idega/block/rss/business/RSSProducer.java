/*
 * $Id: RSSProducer.java,v 1.4 2007/04/27 12:52:13 justinas Exp $
 * Created on Sep 13, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.business;

import java.io.IOException;

import com.idega.block.rss.data.RSSRequest;

/**
 * The IdegaWeb ePlatform supports generating RSS from any type of source from an URI of the format "/rss/identifier/extrauri", e.g. "/rss/folder/files/public" would return an rss listing the files from the folder /files/public". Another example would be "/rss/articles/frontpage" that would return all articles in the category "frontpage" as an rss feed.
<br>
The most important part of the URI is the "identifier" (articles,folder...) because that controls which class generates the resulting rss.<br><br>

To get create an rss from any type of source in you need to do two things:<br>
1. Implement RssProducer<br>
2. Register the producer to RssProducerRegistry.<br>
	Use addProducer(identifier,RSSProducer) (usually done in your IWBundleStarter)<br>
	You could register a RSSProducer by many identifiers if it can handle different types of sources.<br>
<br>
RssProducer interface contains only one method<br>
	-handleRssRequest(RSSRequest rssReq)<br>
(Your RSSProducer should be programmed in a thread safe manner because only one instance of it should exist!)<br>
<br>
RSSRequest <br>
Is a wrapper object for the HttpRequest and HttpResponse objects along with the URL from the request, the identifier and the extrauri.<br>

A typical RssProducer would first check the extrauri to see what is supposed to be returned. Then it would check if the result already exists e.g. in Slide as an xml file. If so it would dispatch the request to the existing file. If not it would create the file first and then dispatch the request to it.
<br>
To avoid writing the same code over and over again for dispatching and setting the correct content type etc. there is a default abstract RSSProducer implementation available to subclass called RSSAbstractProducer.
<br>
RSSAbstractProducer<br>
This class has methods to; dispatch, check existance of a file in slide or the file system, set the standard xml + rss content type to the response and a method to write a string to the response, save a ROME rss object to a file in Slide.
<br>

Example RSS feeds produced could be (not necessarily implemented at this time)<br><br>
/rss/articles/mycategory<br>
Articles rss feed for a certain category (or just /articles/ for all articles)<br>

/rss/category/mycategory<br>
All files in a category in Slide<br>

/rss/folder/files/public<br>
All files from the folder /files/public<br>

/rss/meta/tagname/tagvalue<br>
All files with the tagname and/or tagvalue as metadata from Slide<br>

/rss/calendar/categoryname or id<br>
Rss view of the calendar block<br>

/rss/entity/entityname or entitypath (com.idega.block.entity)<br>
RSS view of a datatable<br>

/rss/podcast/files/public/podcasts/<br>
Rss podcast from that folder in Slide<br>

etc...<br>
 * 
 *  Last modified: $Date: 2007/04/27 12:52:13 $ by $Author: justinas $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.4 $
 */
public interface RSSProducer {
	
	public void handleRSSRequest(RSSRequest rssRequest) throws IOException;

}
