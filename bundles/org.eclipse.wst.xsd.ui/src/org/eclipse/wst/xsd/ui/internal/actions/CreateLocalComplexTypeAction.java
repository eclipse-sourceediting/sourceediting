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
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;


public class CreateLocalComplexTypeAction extends CreateElementAction
{
  /**
   * Constructor for CreateLocalComplexTypeAction.
   */
  public CreateLocalComplexTypeAction()
  {
    super();
  }
  /**
   * Constructor for CreateLocalComplexTypeAction.
   * @param text
   */
  public CreateLocalComplexTypeAction(String text)
  {
    super(text);
  }
  /**
   * Constructor for CreateLocalComplexTypeAction.
   * @param text
   * @param image
   */
  public CreateLocalComplexTypeAction(String text, ImageDescriptor image)
  {
    super(text, image);
  }

  public Element createAndAddNewChildElement()
  {  
    XSDConcreteComponent xsdComp = xsdSchema.getCorrespondingComponent(parentNode);
    
    Element childNode = super.createAndAddNewChildElement();
  
    if (XSDDOMHelper.inputEquals(parentNode, XSDConstants.ELEMENT_ELEMENT_TAG, false))  
    {  
      parentNode.removeAttribute(XSDConstants.TYPE_ATTRIBUTE);  
    }  
                                                      
    String prefix = parentNode.getPrefix();
    prefix = (prefix == null) ? "" : (prefix + ":");
    childNode.appendChild(getDocument().createElement(prefix + XSDConstants.SEQUENCE_ELEMENT_TAG));
    
    formatChild(childNode);
    
    xsdComp.setElement(parentNode);

    return childNode;
  }
}