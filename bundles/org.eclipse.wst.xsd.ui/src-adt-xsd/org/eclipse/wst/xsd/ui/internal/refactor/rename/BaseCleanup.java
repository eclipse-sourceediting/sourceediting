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
package org.eclipse.wst.xsd.ui.internal.refactor.rename;

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Element;

public abstract class BaseCleanup extends XSDVisitor
{
  /**
   * @see org.eclipse.wst.xsd.ui.internal.refactor.XSDVisitor#visitSchema(XSDSchema)
   */
  public void visitSchema(XSDSchema schema)
  {
    super.visitSchema(schema);
    
    // now remove all children that were queued up for removal
    
    for (Iterator iter = childrenToRemove.iterator(); iter.hasNext(); )
    {
      Element domElement = (Element) iter.next();
      XSDDOMHelper.removeNodeAndWhitespace(domElement);
//      domElement.getParentNode().removeChild(domElement);
    }
  }


  protected ArrayList messages = new ArrayList();

  public ArrayList getMessages()
  {
  	return messages;
  }
  

  protected void addMessage(String msg, XSDConcreteComponent component)
  {
////    ErrorMessage message = new ErrorMessage();
//    
//    XSDConcreteComponent currComp = component;
//    while (!(currComp instanceof XSDSchemaContent) && currComp.getContainer() != null)
//    {
//      currComp = currComp.getContainer();
//    }
//
//    Element domElement = currComp.getElement();
//    Node parent = domElement;
//    while (!(parent instanceof NodeImpl) && parent != null)
//    {
//      parent = parent.getParentNode();
//    }
//    if (parent instanceof NodeImpl)
//    {
//      // message.setModelObject(currComp.getElement());
//			message.setTargetObject(currComp.getElement());
//    }
//    message.setLocalizedMessage(msg);
//  ???
//    addMessage(message);
  }
    

// // protected void addMessage(ErrorMessage message)
//	protected void addMessage(Message message)
//  {
//    messages.add(message);
//  }
  

  protected ArrayList childrenToRemove = new ArrayList();
  

  protected String getNamedComponentName(XSDConcreteComponent concrete)
  {
    String name = null;
    if (concrete instanceof XSDNamedComponent)
    {
      name = ((XSDNamedComponent)concrete).getName();
    }
    
    XSDConcreteComponent comp = concrete;
    while (comp != null && name == null)
    {
      comp = comp.getContainer();
      if (comp instanceof XSDNamedComponent)
      {
        name = ((XSDNamedComponent)comp).getName();
      }
    }
    return name != null ? name : "";
  }
}
