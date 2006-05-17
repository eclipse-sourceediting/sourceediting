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
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class RemoveExtensionNodeCommand extends Command
{
  Node node;
  
  public RemoveExtensionNodeCommand(String label, Node node)
  {
    super(label);
    this.node = node;
  }  

  public void execute()
  {
    super.execute();
    if (node.getNodeType() == Node.ATTRIBUTE_NODE)
    {
      Attr attr = (Attr)node;
      attr.getOwnerElement().removeAttributeNode(attr);
    }
    else if (node.getNodeType() == Node.ELEMENT_NODE)
    {  
      Node parent = node.getParentNode();
      if (parent != null)
      {
        parent.removeChild(node);
        // if parent is an AppInfo node then we should remove the appinfo
        //
        if (XSDConstants.APPINFO_ELEMENT_TAG.equals(parent.getLocalName()))
        {
          Node grandpa = parent.getParentNode();
          grandpa.removeChild(parent);
        }  
      }      
    }   
  }
}  
