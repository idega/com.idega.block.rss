package com.idega.block.rss.data;


public class RSSSourceHomeImpl extends com.idega.data.IDOFactory implements RSSSourceHome
{
 protected Class getEntityInterfaceClass(){
  return RSSSource.class;
 }


 public RSSSource create() throws javax.ejb.CreateException{
  return (RSSSource) super.createIDO();
 }


public java.util.Collection findSourceById(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((RSSSourceBMPBean)entity).ejbFindSourceById(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findSourceByName(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((RSSSourceBMPBean)entity).ejbFindSourceByName(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findSources()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((RSSSourceBMPBean)entity).ejbFindSources();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public RSSSource findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (RSSSource) super.findByPrimaryKeyIDO(pk);
 }



}