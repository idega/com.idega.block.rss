package com.sun.syndication.feed.module.wfw;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleGenerator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jdom.Element;
import org.jdom.Namespace;

public class CommentAPIModuleGenerator
  implements ModuleGenerator
{
  public static final Namespace NS = Namespace.getNamespace("wfw", "http://wellformedweb.org/CommentAPI/");
  private static final Set<Namespace> NAMESPACES;

  static
  {
     Set<Namespace> nss = new HashSet<Namespace>();
     nss.add(NS);
     NAMESPACES = Collections.unmodifiableSet(nss);
  }

  public String getNamespaceUri()
  {
     return "http://wellformedweb.org/CommentAPI/";
  }

  public Set<Namespace> getNamespaces()
  {
     return NAMESPACES;
  }

  public void generate(Module module, Element element)
  {
     Element root = element;
     while ((root.getParent() != null) && ((root.getParent() instanceof Element))) {
       root = (Element)element.getParent();
    }
     root.addNamespaceDeclaration(NS);

     CommentAPIModule wfw = (CommentAPIModuleImpl)module;

     if (wfw.getComment() != null) {
       element.addContent(generateNamespacedElement("comment", wfw.getComment()));
    }

     if (wfw.getCommentRss() != null)
       element.addContent(generateNamespacedElement("commentRss", wfw.getCommentRss()));
  }

  protected Element generateNamespacedElement(String name, String value)
  {
     Element element = new Element(name, NS);
     element.addContent(value);
     return element;
  }
}