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

public class CreateAnnotationAction extends CreateElementAction
{
  XSDSchema xsdSchema;
  Element documentationNode;

  public CreateAnnotationAction()
  {
    super();
  }

  public CreateAnnotationAction(String text)
  {
    super(text);
  }

  public CreateAnnotationAction(String text, ImageDescriptor image)
  {
    super(text, image);
  }

  public Element createAndAddNewChildElement()
  {
    Element childNode = super.createAndAddNewChildElement();

    String prefix = parentNode.getPrefix();
    prefix = (prefix == null) ? "" : (prefix + ":");
    documentationNode = getDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + XSDConstants.DOCUMENTATION_ELEMENT_TAG);
    childNode.appendChild(documentationNode);

    formatChild(childNode);
    formatChild(documentationNode);
    formatChild(childNode);
    
    return childNode;
  }
  
}

