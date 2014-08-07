package com.idega.block.rss.fetcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.file.util.MimeTypeUtil;
import com.idega.repository.RepositoryService;
import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class IWHttpURLFeedFetcher extends HttpURLFeedFetcher {

	private static final Logger LOGGER = Logger.getLogger(IWHttpURLFeedFetcher.class.getName());

	@Autowired
	private RepositoryService repository;

	private RepositoryService getRepositoryService() {
		if (repository == null) {
			ELUtil.getInstance().autowire(this);
		}
		return repository;
	}

	public IWHttpURLFeedFetcher(FeedFetcherCache feedInfoCache) {
		super(feedInfoCache);
	}

	@Override
	public SyndFeed retrieveFeed(URL feedUrl) throws IllegalArgumentException, IOException, FeedException, FetcherException {
		if (feedUrl == null) {
			LOGGER.warning("URL for RSS feed is not provided");
			return null;
		}

		RepositoryService repository = getRepositoryService();
		if (repository == null) {
			return super.retrieveFeed(feedUrl);
		}

		InputStream stream = null;
		SyndFeed feed = null;
		try {
			String pathInRepository = feedUrl.getPath();
			if (!StringUtil.isEmpty(pathInRepository) && pathInRepository.startsWith(CoreConstants.WEBDAV_SERVLET_URI) || pathInRepository.startsWith(CoreConstants.PATH_FILES_ROOT)) {
				if (!repository.getExistence(pathInRepository)) {
					return null;
				}

				stream = repository.getInputStreamAsRoot(pathInRepository);

			    XmlReader reader = new XmlReader(stream, MimeTypeUtil.MIME_TYPE_XML, true);

			    SyndFeedInput syndFeedInput = new SyndFeedInput();
			    syndFeedInput.setPreserveWireFeed(isPreserveWireFeed());

				feed = syndFeedInput.build(reader);
			} else {
				feed = super.retrieveFeed(feedUrl);
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting RSS feed from " + feedUrl, e);
			feed = super.retrieveFeed(feedUrl);
		} finally {
			IOUtil.close(stream);
		}

		if (feed == null) {
			LOGGER.warning("Error getting feed from " + feedUrl);
		}
		return feed;
	}
}