package com.idega.block.rss.data;


public class RSSHeadlineHomeImpl extends com.idega.data.IDOFactory implements RSSHeadlineHome
{
 protected Class getEntityInterfaceClass(){
  return RSSHeadline.class;
 }


 public RSSHeadline create() throws javax.ejb.CreateException{
  return (RSSHeadline) super.createIDO();
 }


 public RSSHeadline findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (RSSHeadline) super.findByPrimaryKeyIDO(pk);
 }



}