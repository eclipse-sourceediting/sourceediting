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
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class CreateIdentityConstraintsAction extends CreateElementAction
{
  XSDSchema xsdSchema;

  /**
   * Constructor for CreateIdentityConstraintsAction.
   */
  public CreateIdentityConstraintsAction()
  {
    super();
  }
  /**
   * Constructor for CreateIdentityConstraintsAction.
   * @param text
   */
  public CreateIdentityConstraintsAction(String text)
  {
    super(text);
  }
  /**
   * Constructor for CreateIdentityConstraintsAction.
   * @param text
   * @param XSDSchema
   */
  public CreateIdentityConstraintsAction(String text, XSDSchema xsdSchema)
  {
    super(text);
    this.xsdSchema = xsdSchema;
  }
  /**
   * Constructor for CreateIdentityConstraintsAction.
   * @param text
   * @param image
   */
  public CreateIdentityConstraintsAction(String text, ImageDescriptor image)
  {
    super(text, image);
  }

  public Element createAndAddNewChildElement()
  {
    Element childNode = super.createAndAddNewChildElement();

    String prefix = parentNode.getPrefix();
    prefix = (prefix == null) ? "" : (prefix + ":");
    Element selectorNode = getDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + XSDConstants.SELECTOR_ELEMENT_TAG);

    DOMAttribute attr = new DOMAttribute(XSDConstants.XPATH_ATTRIBUTE, "");
    selectorNode.setAttribute(attr.getName(), attr.getValue());

    childNode.appendChild(selectorNode);

    return childNode;
  }
	
}

