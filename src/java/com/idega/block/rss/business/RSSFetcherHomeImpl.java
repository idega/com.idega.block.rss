package com.idega.block.rss.business;


public class RSSFetcherHomeImpl extends com.idega.business.IBOHomeImpl implements RSSFetcherHome
{
 protected Class getBeanInterfaceClass(){
  return RSSFetcher.class;
 }


 public RSSFetcher create() throws javax.ejb.CreateException{
  return (RSSFetcher) super.createIBO();
 }



}