package com.idega.block.rss.data;


public class RSSHeadlineHomeImpl extends com.idega.data.IDOFactory implements RSSHeadlineHome
{
 protected Class getEntityInterfaceClass(){
  return RSSHeadline.class;
 }


 public RSSHeadline create() throws javax.ejb.CreateException{
  return (RSSHeadline) super.createIDO();
 }


public java.util.Collection findHeadlines(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((RSSHeadlineBMPBean)entity).ejbFindHeadlines(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public RSSHeadline findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (RSSHeadline) super.findByPrimaryKeyIDO(pk);
 }



}