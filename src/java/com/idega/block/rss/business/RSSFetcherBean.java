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
			URL url = new URL(urlStr);
			Document doc = builder.build(url);
			Collection items = new ArrayList(8);
			collectItems(doc.getRootElement(), items);
			Iterator iter = items.iterator();
			while (iter.hasNext()) {
				Element item = (Element) iter.next();
				Element eTitle = (Element) item.getChild("title", item.getNamespace());
				Element eLink = (Element) item.getChild("link", item.getNamespace());
				if(eTitle==null || eLink==null) {
					continue;
				}
				String sTitle = eTitle.getText();
				String sLink = eLink.getText();
				if(sTitle==null || sLink==null) {
					continue;
				}
				RSSHeadline h = new RSSHeadlineBMPBean();
				h.setHeadline(sTitle);
				h.setLink(sLink);
				theReturn.add(h);
			}
		} catch (MalformedURLException mue) {
			//String msg = mue.getMessage();
			mue.printStackTrace();
		} catch (Exception e) {
			//String msg = e.getMessage();
			e.printStackTrace();
		}
		return theReturn;
	}
	
	// Collects all elements with name "item" into a collection
	private void collectItems(Element element, Collection col) {
		if("item".equals(element.getName())) {
			col.add(element);
		}
		java.util.List children = element.getChildren();
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			Element item = (Element) iter.next();
			collectItems(item, col);
		}
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
