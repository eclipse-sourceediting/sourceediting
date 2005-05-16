package org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class ModelQueryExtension
{  
  protected static final String[] EMPTY_STRING_ARRAY = {};
  
  public String[] getAttributeValues(Element ownerElement, String namespace, String name)
  {
    return EMPTY_STRING_ARRAY;
  }
  
  public String[] getElementValues(Node parentNode, String namespace, String name)
  {
    return EMPTY_STRING_ARRAY;
  }
  
  public boolean isApplicableChildElement(Node parentNode, String namespace, String name)
  {
    return true;
  }
}
