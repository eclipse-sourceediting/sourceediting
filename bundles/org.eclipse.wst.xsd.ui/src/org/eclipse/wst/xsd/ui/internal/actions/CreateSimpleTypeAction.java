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
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
public class CreateSimpleTypeAction extends CreateElementAction
{
  /**
   * 
   */
  public CreateSimpleTypeAction()
  {
    super();
  }

  /**
   * @param text
   */
  public CreateSimpleTypeAction(String text)
  {
    super(text);
  }
  
  public CreateSimpleTypeAction(String text, XSDSchema xsdSchema)
  {
    super(text);
    this.xsdSchema = xsdSchema;
  }

  /**
   * @param text
   * @param image
   */
  public CreateSimpleTypeAction(String text, ImageDescriptor image)
  {
    super(text, image);
  }
  
  public Element createAndAddNewChildElement()
  {
    XSDConcreteComponent xsdComp = xsdSchema.getCorrespondingComponent(parentNode);
    Element childNode = super.createAndAddNewChildElement();

    String prefix = parentNode.getPrefix();
    prefix = (prefix == null) ? "" : (prefix + ":");
    Element contentModelNode = getDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + XSDConstants.RESTRICTION_ELEMENT_TAG);
    contentModelNode.setAttribute(XSDConstants.BASE_ATTRIBUTE, prefix + "string");
    childNode.appendChild(contentModelNode);

    formatChild(childNode);
    
    xsdComp.setElement(parentNode);
    
    return childNode;
  }
}
