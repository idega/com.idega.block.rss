package com.idega.block.rss.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class RSSBusinessHomeImpl extends IBOHomeImpl implements RSSBusinessHome {

	private static final long serialVersionUID = 527507479336735892L;

	public Class getBeanInterfaceClass() {
		return RSSBusiness.class;
	}

	public RSSBusiness create() throws CreateException {
		return (RSSBusiness) super.createIBO();
	}
}