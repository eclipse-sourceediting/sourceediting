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
package org.eclipse.wst.xsd.ui.internal.graph;

import org.eclipse.wst.xml.core.document.DOMNode;
import org.eclipse.xsd.XSDConcreteComponent;
import org.w3c.dom.Element;
              

public class XSDGraphUtil
{                        
  public static boolean isEditable(Object model)
  {                    
    boolean result = false;
    if (model instanceof XSDConcreteComponent)
    {
      Element element = ((XSDConcreteComponent)model).getElement();
      // this test ensures that we don't attempt to select an element for an external schema
      //
      if (element instanceof DOMNode)
      {
         result = true;
      }
    }                      
    return result;
  }
}
