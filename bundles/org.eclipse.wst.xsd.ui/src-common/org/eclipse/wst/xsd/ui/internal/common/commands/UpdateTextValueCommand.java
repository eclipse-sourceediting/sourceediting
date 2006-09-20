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

import org.eclipse.wst.xml.ui.internal.tabletree.TreeContentHelper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/*
 * This command is used from the extension view to edit extension elements
 * and which are implemented as DOM objects (not part of the EMF model)
 */
public class UpdateTextValueCommand  extends BaseCommand
{
  Element element;
  String value;
  
  public UpdateTextValueCommand(Element element, String value)
  {
    this.element = element;
    this.value = value;
  }

  
  public void execute()
  {
    try
    {
      beginRecording(element);      
      Node textNode = null;
      TreeContentHelper helper = new TreeContentHelper();
      for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) 
      {
        if (node.getNodeType() == Node.TEXT_NODE &&
            !helper.isIgnorableText(node)) 
        {
          textNode = node;
          break;
        }         
      }       
      if (textNode == null)
      {
        textNode = element.getOwnerDocument().createTextNode("");
        element.appendChild(textNode);
      }  
      helper.setNodeValue(textNode, value);        
    }
    finally
    {
      endRecording();
    }
  } 
}
