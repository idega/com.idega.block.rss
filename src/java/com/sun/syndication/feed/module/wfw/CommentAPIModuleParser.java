package com.sun.syndication.feed.module.wfw;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleParser;
import org.jdom.Element;
import org.jdom.Namespace;

public class CommentAPIModuleParser implements ModuleParser
{
   private static final Namespace NS = Namespace.getNamespace("wfw", "http://wellformedweb.org/CommentAPI/");

  public String getNamespaceUri() {
     return "http://wellformedweb.org/CommentAPI/";
  }

  public Module parse(Element dcRoot) {
     boolean foundSomething = false;
     CommentAPIModule wfw = new CommentAPIModuleImpl();

     Element comments = dcRoot.getChild("comment", NS);
     if (comments != null) {
       foundSomething = true;
       wfw.setComment(comments.getText());
    }

     Element commentRss = dcRoot.getChild("commentRss", NS);
     if (commentRss != null) {
       foundSomething = true;
       wfw.setCommentRss(commentRss.getText());
    }

     return foundSomething ? wfw : null;
  }
}