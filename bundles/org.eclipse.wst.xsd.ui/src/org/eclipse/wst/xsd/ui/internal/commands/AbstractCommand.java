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
package org.eclipse.wst.xsd.ui.internal.commands;

import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.xml.core.document.DOMModel;
import org.eclipse.wst.xml.core.document.DOMNode;
import org.eclipse.wst.xml.core.format.FormatProcessorXML;
import org.eclipse.xsd.XSDConcreteComponent;
import org.w3c.dom.Element;

public abstract class AbstractCommand 
{
  private XSDConcreteComponent parent;
  private XSDConcreteComponent model;

  protected AbstractCommand(XSDConcreteComponent parent)
  {
    this.parent = parent;
  }
  
  public abstract void run();

  protected XSDConcreteComponent getParent()
  {
    return parent;
  }
  
  public XSDConcreteComponent getModelObject()
  {
    return model;
  }
  
  protected void setModelObject(XSDConcreteComponent model)
  {
    this.model = model;
  }
  
  // Establish part-whole relationship
  protected abstract boolean adopt(XSDConcreteComponent model);

  protected void formatChild(Element child)
  {
    if (child instanceof DOMNode)
    {
      DOMModel model = ((DOMNode)child).getModel();
      try
      {
        // tell the model that we are about to make a big model change
        model.aboutToChangeModel();
        
        IStructuredFormatProcessor formatProcessor = new FormatProcessorXML();
        formatProcessor.formatNode(child);
      }
      finally
      {
        // tell the model that we are done with the big model change
        model.changedModel(); 
      }
    }
  }

}
