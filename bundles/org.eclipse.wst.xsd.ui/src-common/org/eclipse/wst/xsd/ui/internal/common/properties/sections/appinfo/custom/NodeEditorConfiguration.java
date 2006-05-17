package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom;

import org.w3c.dom.Node;

public abstract class NodeEditorConfiguration
{  
  public final static int STYLE_NONE = 0;   
  public final static int STYLE_TEXT = 1; 
  public final static int STYLE_COMBO = 2;
  public final static int STYLE_DIALOG = 4;   
  
  public abstract int getStyle();
  
  private Node node;
  private Node parentNode;

  public Node getNode()
  {
    return node;
  }
  public void setNode(Node node)
  {
    this.node = node;
  }
  public Node getParentNode()
  {
    return parentNode;
  }
  public void setParentNode(Node parentNode)
  {
    this.parentNode = parentNode;
  }
}
