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
package org.eclipse.wst.xsd.ui.internal.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class CreateLocalSimpleTypeAction extends CreateElementAction
{
  XSDSchema xsdSchema;

  /**
   * Constructor for CreateLocalSimpleTypeAction.
   */
  public CreateLocalSimpleTypeAction()
  {
    super();
  }
  /**
   * Constructor for CreateLocalSimpleTypeAction.
   * @param text
   */
  public CreateLocalSimpleTypeAction(String text)
  {
    super(text);
  }

  /**
   * Constructor for CreateLocalSimpleTypeAction.
   * @param text
   * @param image
   */
  public CreateLocalSimpleTypeAction(String text, ImageDescriptor image)
  {
    super(text, image);
  }

  public Element createAndAddNewChildElement()
  {
    Element childNode = super.createAndAddNewChildElement();

    if (XSDDOMHelper.inputEquals(parentNode, XSDConstants.UNION_ELEMENT_TAG, false))
    {
//      parentNode.removeAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE);
    }
    else if (XSDDOMHelper.inputEquals(parentNode, XSDConstants.LIST_ELEMENT_TAG, false))
    {
      parentNode.removeAttribute(XSDConstants.ITEMTYPE_ATTRIBUTE);
    }
    else if (XSDDOMHelper.inputEquals(parentNode, XSDConstants.ATTRIBUTE_ELEMENT_TAG, false))
    {
      parentNode.removeAttribute(XSDConstants.TYPE_ATTRIBUTE);
    }
    else if (XSDDOMHelper.inputEquals(parentNode, XSDConstants.RESTRICTION_ELEMENT_TAG, false))
    {
      Node parent = parentNode.getParentNode();
      if (XSDDOMHelper.inputEquals(parent, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false))
      {
        parentNode.removeAttribute(XSDConstants.BASE_ATTRIBUTE);
      }
    }
    else if (XSDDOMHelper.inputEquals(parentNode, XSDConstants.ELEMENT_ELEMENT_TAG, false))
    {
      parentNode.removeAttribute(XSDConstants.TYPE_ATTRIBUTE);
    }
    
    formatChild(childNode);
    
    return childNode;
  }
}

