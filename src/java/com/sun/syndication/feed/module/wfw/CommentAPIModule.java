package com.sun.syndication.feed.module.wfw;

import com.sun.syndication.feed.module.Module;

public abstract interface CommentAPIModule extends Module
{
  public static final String URI = "http://wellformedweb.org/CommentAPI/";

  public abstract String getComment();

  public abstract void setComment(String paramString);

  public abstract String getCommentRss();

  public abstract void setCommentRss(String paramString);
}