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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.xsd.XSDConcreteComponent;

public class AbstractAction extends Action
{
  XSDConcreteComponent xsdConcreteComponent;

  /**
   * @param text
   */
  public AbstractAction(String text, XSDConcreteComponent xsdConcreteComponent)
  {
    super(text);
    this.xsdConcreteComponent = xsdConcreteComponent;
  }

  /**
   * @param text
   * @param image
   */
  public AbstractAction(String text, ImageDescriptor image, XSDConcreteComponent xsdConcreteComponent)
  {
    super(text, image);
    this.xsdConcreteComponent = xsdConcreteComponent;
  }

  /**
   * @param text
   * @param style
   */
  public AbstractAction(String text, int style)
  {
    super(text, style);
  }
  
  public DocumentImpl getDocument()
  {
    return (DocumentImpl) xsdConcreteComponent.getElement().getOwnerDocument();
  }
    
  public void beginRecording(String description)
  {
    getDocument().getModel().beginRecording(this, description);
  }
  
  public void endRecording()
  {
    DocumentImpl doc = (DocumentImpl) getDocument();
    
    doc.getModel().endRecording(this);    
  }


}
