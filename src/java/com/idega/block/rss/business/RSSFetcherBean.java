/*
 * Created on 2003-jun-04
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.rss.business;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.idega.business.IBOServiceBean;
import com.idega.util.ListUtil;
import com.idega.block.rss.data.RSSHeadline;
import com.idega.block.rss.data.RSSHeadlineHome;
import com.idega.block.rss.data.RSSHeadlineBMPBean;

/**
 * @author wmgobom
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RSSFetcherBean extends IBOServiceBean implements RSSFetcher {

	public Collection getHeadlinesFromRSS(String urlStr) {
		SAXBuilder builder = new SAXBuilder();
		Collection theReturn = new ArrayList();
		try {
			/* http://slashdot.org/slashdot.rss - xml-struktur
			 * <rss>
			 *  <channel>
			 *      <item>
			 *          <litem>
			 */
			URL url = new URL(urlStr);
			Document doc = builder.build(url);
			Element root = doc.getRootElement();
			Element channel = root.getChild("channel");
			Collection items = channel.getChildren("item");
			Iterator iter = items.iterator();
			int size = items.size();
			while (iter.hasNext()) {
				Element item = (Element) iter.next();
				Element eTitle = (Element) item.getChild("title");
				Element eLink = (Element) item.getChild("link");
				String sTitle = eTitle.getText();
				String sLink = eLink.getText();
				RSSHeadline h = new RSSHeadlineBMPBean();
				h.setHeadline(sTitle);
				h.setLink(sLink);
				theReturn.add(h);
			}
		} catch (MalformedURLException mue) {
			String msg = mue.getMessage();
			mue.printStackTrace();
		} catch (Exception ioe) {
			String msg = ioe.getMessage();
			ioe.printStackTrace();
		}
		return theReturn;
	}
    
    public Collection getLinksAndHeadlines(String sourceURL) {
        Collection headlines = null;
        try {
            headlines = getHeadlinesFromDB(sourceURL);                    
        } catch (RemoteException re) {
            re.printStackTrace();
            headlines = ListUtil.getEmptyList();
        } catch (FinderException fe) {
            fe.printStackTrace();
            headlines = ListUtil.getEmptyList();            
        }
        return headlines;
    }

	public void saveLinksAndHeadlines(String sourceURL) {
		try {
			RSSHeadlineHome hHome = (RSSHeadlineHome) getIDOHome(RSSHeadline.class);
			Collection headlines = getHeadlinesFromRSS(sourceURL);
			Iterator loop = headlines.iterator();
			while (loop.hasNext()) {
                try{
    				RSSHeadlineBMPBean elem = (RSSHeadlineBMPBean) loop.next();
    				RSSHeadline bmp = hHome.create();
    				bmp.setHeadline(elem.getHeadline());
    				bmp.setLink(elem.getLink());
                    bmp.setSourceURL(sourceURL);
    				bmp.store();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
			}
		} catch (RemoteException re) {
			re.printStackTrace();
			System.out.println(re.getMessage());
        }
	}
    
    public Collection getHeadlinesFromDB(String sourceURL) throws RemoteException, FinderException {
        RSSHeadlineHome hHome = (RSSHeadlineHome) getIDOHome(RSSHeadline.class);
        return hHome.findHeadlines(sourceURL);
    }
}
