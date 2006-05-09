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
package org.eclipse.wst.xsd.ui.internal.util;

import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class ModelReconcileAdapter extends DocumentAdapter
{
  protected boolean handlingNotifyChanged = false;
  
  public ModelReconcileAdapter(Document document)
  {
    super(document);
  }
      
  public void notifyChanged(INodeNotifier notifier, int eventType, Object feature, Object oldValue, Object newValue, int index)
  {
    if (!handlingNotifyChanged)
    {
      handlingNotifyChanged = true;
      try
      {
        handleNotifyChange(notifier, eventType, feature, oldValue, newValue, index);       
      }
      catch (Exception e)
      {
//        XSDEditorPlugin.getPlugin().getMsgLogger().write(e);
      }
      handlingNotifyChanged = false;
    }
  }
  
  protected void handleNodeChanged(Node node)
  {    
  }
  
  public void handleNotifyChange(INodeNotifier notifier, int eventType, Object feature, Object oldValue, Object newValue, int index)
  {
    switch (eventType)
    {
      case INodeNotifier.ADD:
      {
        if (newValue instanceof Element)
        {
          Element element = (Element)newValue;
          adapt(element);
        }  
        break;
      }
      case INodeNotifier.REMOVE:
      case INodeNotifier.CHANGE:
      case INodeNotifier.STRUCTURE_CHANGED:
      case INodeNotifier.CONTENT_CHANGED:
      {
        Node node = (Node)notifier;
        handleNodeChanged(node);
        break;
      }             
    }
  }
}