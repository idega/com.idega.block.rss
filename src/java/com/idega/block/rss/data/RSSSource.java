package com.idega.block.rss.data;


public interface RSSSource extends com.idega.data.IDOEntity
{
 public java.lang.String getName();
 public java.lang.String getSourceURL();
 public void initializeAttributes();
 public void setName(java.lang.String p0);
 public void setSourceURL(java.lang.String p0);
}
