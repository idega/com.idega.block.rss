package com.sun.syndication.feed.module.wfw;

import com.sun.syndication.feed.module.ModuleImpl;

public class CommentAPIModuleImpl extends ModuleImpl implements CommentAPIModule
{
  private static final long serialVersionUID = 6087476111816517918L;
  private String comment;
  private String commentRss;

  public CommentAPIModuleImpl()
  {
     super(CommentAPIModule.class, "http://wellformedweb.org/CommentAPI/");
  }

  public String getComment()
  {
     return this.comment;
  }

  public void setComment(String comment) {
     this.comment = comment;
  }

  public String getCommentRss() {
     return this.commentRss;
  }

  public void setCommentRss(String commentRss) {
     this.commentRss = commentRss;
  }

  public Class<CommentAPIModule> getInterface()
  {
     return CommentAPIModule.class;
  }

  public void copyFrom(Object obj) {
     CommentAPIModule m = (CommentAPIModule)obj;
     setComment(m.getComment());
     setCommentRss(m.getCommentRss());
  }
}