package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom;

import org.w3c.dom.Node;

public class NodeFilter
{  
  public boolean isApplicableContext(Node parentNode, int nodeType, String namespace, String name)
  {  
    return true;
  }
}
