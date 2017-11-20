/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQueryAssociationProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 *
 */
public abstract class BaseAssociationProvider implements ModelQueryAssociationProvider
{                    
  public BaseAssociationProvider()
  {
  }

  public CMNode getCMNode(Node node)
  {
    CMNode result = null;
    switch (node.getNodeType())
    {
      case Node.ATTRIBUTE_NODE :
      {
        result = getCMAttributeDeclaration((Attr)node);
        break;
      }
      case Node.ELEMENT_NODE :
      {
        result = getCMElementDeclaration((Element)node);
        break;
      }
      case Node.CDATA_SECTION_NODE :
      case Node.TEXT_NODE :
      {
        result = getCMDataType((Text)node);
        break;
      }
    }
    return result;
  }


  public CMDataType getCMDataType(Text text)
  {
    CMDataType result = null;
    Node parentNode = text.getParentNode();
    if (parentNode != null && parentNode.getNodeType() == Node.ELEMENT_NODE)
    {
      CMElementDeclaration ed = getCMElementDeclaration((Element)parentNode);
      result = ed.getDataType();
    }
    return result;
  }


  public CMAttributeDeclaration getCMAttributeDeclaration(Attr attr)
  {
    CMAttributeDeclaration result = null;
    Element element = attr.getOwnerElement();
    if (element != null)
    {
      CMElementDeclaration ed = getCMElementDeclaration(element);
      if (ed != null)
      {
        result = (CMAttributeDeclaration)ed.getAttributes().getNamedItem(attr.getName());
      }
    }
    return result;
  }          
}
