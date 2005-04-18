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
import java.util.Iterator;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.format.NodeFormatter;
import org.w3c.dom.Node;

public class DragNodesCommand extends BaseDragNodesCommand
{
  /**
   * Constructor for DragNodesCommand.
   * @param target
   * @param location
   * @param operations
   * @param operation
   * @param sources
   */
  public DragNodesCommand(
    Object target,
    float location,
    int operations,
    int operation,
    Collection sources)
  {
    super(target, location, operations, operation, sources);
  }

  /**
   * @see org.eclipse.wst.common.ui.dnd.DragAndDropCommand#canExecute()
   */
  public boolean canExecute()
  {
    if (sources.size() > 0)
    {
      Node firstSource = (Node) sources.toArray()[0];
      return isSiblingNodes((Node) target, firstSource);
    }
    return false;
//    return isDirectSchemaChild((Node)target);
  }

  
  /**
   * @see org.eclipse.wst.common.ui.dnd.DragAndDropCommand#execute()
   */
  public void execute()
  {
  	NodeFormatter formatProcessor = new NodeFormatter();
    Node referenceNode = (Node) target;
    Iterator iter = sources.iterator();
    beginRecording();

    while (iter.hasNext()) 
    {
      Node node = (Node) iter.next();
      if (isSiblingNodes(referenceNode,node)) 
      {
        moveNode(referenceNode, node, !isAfter());
        formatProcessor.format((IDOMNode)node);
      }
    }
//    formatProcessor.format((XMLNode)referenceNode.getParentNode());
    endRecording();
  }
}
