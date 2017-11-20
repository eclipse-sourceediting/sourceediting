/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class RemoveExtensionNodeCommand extends BaseCommand
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
    try
    {
      beginRecording(node);
      if (node.getNodeType() == Node.ATTRIBUTE_NODE)
      {
        Attr attr = (Attr) node;
        attr.getOwnerElement().removeAttributeNode(attr);
      }
      else if (node.getNodeType() == Node.ELEMENT_NODE)
      {
        Node parent = node.getParentNode();
        if (parent != null)
        {
          XSDDOMHelper.removeNodeAndWhitespace(node);
          
          if (XSDDOMHelper.hasOnlyWhitespace(parent))
          {
            if (XSDConstants.APPINFO_ELEMENT_TAG.equals(parent.getLocalName()))
            {
              XSDDOMHelper.removeNodeAndWhitespace(parent);
            }
          }
        }
      }
    }
    finally
    {
      endRecording();
    }
  }
}  
