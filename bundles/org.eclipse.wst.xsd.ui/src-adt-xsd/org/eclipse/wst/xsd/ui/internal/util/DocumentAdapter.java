/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.util;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

abstract class DocumentAdapter implements INodeAdapter
{
  Document document;

  public DocumentAdapter(Document document)
  {
    this.document = document;
    ((INodeNotifier) document).addAdapter(this);
    adaptChildElements(document);
  }

  private void adaptChildElements(Node parentNode)
  {
    for (Node child = parentNode.getFirstChild(); child != null; child = child.getNextSibling())
    {
      if (child.getNodeType() == Node.ELEMENT_NODE)
      {
        adapt((Element) child);
      }
    }    
  }
  
  public void adapt(Element element)
  {
    if (((INodeNotifier) element).getExistingAdapter(this) == null)
    {
      ((INodeNotifier) element).addAdapter(this);
      adaptChildElements(element);  
    }
  }

  public boolean isAdapterForType(Object type)
  {
    return type == this;
  }

  abstract public void notifyChanged(INodeNotifier notifier, int eventType, Object feature, Object oldValue, Object newValue, int index);
  {
  }
}
