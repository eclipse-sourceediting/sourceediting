/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dnd;

import java.util.Collection;

import org.eclipse.wst.common.ui.dnd.DefaultDragAndDropCommand;
import org.eclipse.wst.xml.core.document.DOMModel;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Node;


public abstract class BaseDragNodesCommand extends DefaultDragAndDropCommand
{
  /**
   * Constructor for BaseDragNodesCommand.
   * @param target
   * @param location
   * @param operations
   * @param operation
   * @param sources
   */
  public BaseDragNodesCommand(
    Object target,
    float location,
    int operations,
    int operation,
    Collection sources)
  {
    super(target, location, operations, operation, sources);
  }

  protected boolean isDirectSchemaChild(Node node)
  {
    Node parent = node.getParentNode();
    if (XSDDOMHelper.inputEquals(parent, XSDConstants.SCHEMA_ELEMENT_TAG, false) &&
        parent.getParentNode().equals(parent.getOwnerDocument()))
    {
      return true;
    }
    return false;
  } 
  
  protected boolean isSiblingNodes(Node first, Node second)
  {
    if (first.getParentNode() != null)
    {
      return first.getParentNode().equals(second.getParentNode());
    }
    return false;
  }
  
  protected void beginRecording()
  {
    if (target != null)
    {
      DOMModel model = getModel((Node)target);
      
      if (model != null)
      {
        model.beginRecording(this, "Move");
      }
    }
  }
  
  protected void endRecording()
  {
    if (target != null)
    {
      DOMModel model = getModel((Node)target);
      
      if (model != null)
      {
        model.endRecording(this);
      }
    }
  }
  protected DOMModel getModel(Node node)
  {
    Object object = node.getOwnerDocument();
    if (object instanceof DocumentImpl)
    {
      return ((DocumentImpl) object).getModel();
    }
    return null;
  }
  
  protected void moveNode(Node referenceNode, Node nodeToMove, boolean isBefore)
  {
    XSDDOMHelper.moveNode(referenceNode, nodeToMove, isBefore);
  }
}
