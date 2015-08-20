/*
 * $Id: IWRSSProducerServlet.java,v 1.4 2007/04/26 18:59:10 justinas Exp $
 * Created on Sep 13, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.rss.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.block.rss.business.NoSuchRSSProducerException;
import com.idega.block.rss.business.RSSProducer;
import com.idega.block.rss.business.RSSProducerRegistry;
import com.idega.block.rss.data.RSSRequest;

/**
 * @see com.idega.block.rss.business.RSSProducer Finds the correct RSSProducer
 *      and delegates the task of getting an rss feed to it.
 *
 * Last modified: $Date: 2007/04/26 18:59:10 $ by $Author: justinas $
 *
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.4 $
 */
public class IWRSSProducerServlet extends HttpServlet {

	private static final long serialVersionUID = 8202220239727353313L;

	private static final Logger LOGGER = Logger.getLogger(IWRSSProducerServlet.class.getName());

	@Override
	public void doGet(HttpServletRequest _req, HttpServletResponse _res) throws IOException {
		doPost(_req, _res);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		RSSProducerRegistry factory = RSSProducerRegistry.getInstance();
		RSSRequest rssReq = new RSSRequest(request, response);

		try {
			RSSProducer producer = factory.getRSSProducerByIdentifier(rssReq.getIdentifier());
			producer.handleRSSRequest(rssReq);
		} catch (NoSuchRSSProducerException e) {
			LOGGER.warning("RSS producer '" + rssReq.getIdentifier() + "' does not exist!");
			response.getWriter().print(e.getMessage());
			response.getWriter().flush();
			response.getWriter().close();
		}

	}
}