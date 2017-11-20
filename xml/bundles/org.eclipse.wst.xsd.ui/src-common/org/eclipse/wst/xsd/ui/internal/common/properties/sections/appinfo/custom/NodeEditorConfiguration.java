/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
