package com.idega.block.rss.business;


public class RSSBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements RSSBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return RSSBusiness.class;
 }


 public RSSBusiness create() throws javax.ejb.CreateException{
  return (RSSBusiness) super.createIBO();
 }



}