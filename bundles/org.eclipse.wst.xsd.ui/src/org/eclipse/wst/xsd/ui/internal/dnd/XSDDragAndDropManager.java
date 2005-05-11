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

import org.eclipse.wst.common.ui.internal.dnd.DragAndDropCommand;
import org.eclipse.wst.common.ui.internal.dnd.DragAndDropManager;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Node;

public class XSDDragAndDropManager implements DragAndDropManager
{
  /**
   * Constructor for XSDDragAndDropManager.
   */
  public XSDDragAndDropManager()
  {
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

  /**
   * @see org.eclipse.wst.common.ui.internal.dnd.DragAndDropManager#createCommand(Object, float, int, int, Collection)
   */
  public DragAndDropCommand createCommand(
    Object target,
    float location,
    int operations,
    int operation,
    Collection source)
  {
    if (target instanceof Node) 
    {
      Node node = (Node) target;
//      if (isDirectSchemaChild(node))
//      {
        return new DragNodesCommand(target, location, operations, operation, source);
//      }
    }
    return null;
  }
}
