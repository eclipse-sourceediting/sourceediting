/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.text;

import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XSDModelReconcileAdapter extends DocumentAdapter
{
    INodeNotifier currentNotifier;
    int currentEventType;
    XSDSchema schema;
    
    public XSDModelReconcileAdapter(Document document, XSDSchema schema)
    {
      super(document);
      this.schema = schema;
    }
        
    boolean handlingNotifyChanged = false;

    public void notifyChanged(INodeNotifier notifier, int eventType, Object feature, Object oldValue, Object newValue, int index)
    {
      if (!handlingNotifyChanged)
      {
        handlingNotifyChanged = true;
        try
        {
          // delay handle events only in the source view
          //if (getCurrentPageType() == XSDEditorPlugin.SOURCE_PAGE &&
          //    !(getActivePart() instanceof PropertySheet) && 
          //    !(getActivePart() instanceof org.eclipse.ui.views.contentoutline.ContentOutline)) {
          //  startDelayedEvent(notifier, eventType, feature, oldValue, newValue, index);
          //}
          //else // all other views, just handle the events right away
          {
            handleNotifyChange(notifier, eventType, feature, oldValue, newValue, index);
          }
        }
        catch (Exception e)
        {
//          XSDEditorPlugin.getPlugin().getMsgLogger().write(e);
        }
        handlingNotifyChanged = false;
      }
    }

    public void handleNotifyChange(INodeNotifier notifier, int eventType, Object feature, Object oldValue, Object newValue, int index)
    {
      switch (eventType)
      {
        case INodeNotifier.ADD:
        {
          if (newValue instanceof Element)
          {
            adapt((Element)newValue);
          }
          break;
        }
        case INodeNotifier.REMOVE:
        case INodeNotifier.CHANGE:
        case INodeNotifier.STRUCTURE_CHANGED:
        case INodeNotifier.CONTENT_CHANGED:
        {
          Node node = (Node)notifier;
          XSDConcreteComponent concreteComponent = schema.getCorrespondingComponent(node);
          concreteComponent.elementContentsChanged((Element)node);
          break;
        }             
      }
    }

    protected DelayedEvent delayedTask;
    protected void startDelayedEvent(INodeNotifier notifier, int eventType, Object feature, Object oldValue, Object newValue, int index)
    {
      // check if there is already a delayed task for the same notifier and eventType
//      if (delayedTask != null)
//      {
//        Notifier aNotifier = delayedTask.getNotifier();
//        int anEventType = delayedTask.getEventType();
//        if (notifier == aNotifier && anEventType == eventType)
//        {
//          // same event, just different data, delay new event
//          delayedTask.setCancel(true);
//        }
//      }

      delayedTask = new DelayedEvent();

      delayedTask.setNotifier(notifier);
      delayedTask.setEventType(eventType);
      delayedTask.setFeature(feature);
      delayedTask.setOldValue(oldValue);
      delayedTask.setNewValue(newValue);
      delayedTask.setIndex(index);

      Display.getDefault().timerExec(400,delayedTask);
    }

    class DelayedEvent implements Runnable
    {
      INodeNotifier notifier;
      int eventType;
      Object feature;
      Object oldValue;
      Object newValue;
      int index;
      boolean cancelEvent = false;

      /*
       * @see Runnable#run()
       */
      public void run()
      {
        if (!cancelEvent)
        {
          handleNotifyChange(notifier, eventType, feature, oldValue, newValue, index);
          if (delayedTask == this)
          {
            delayedTask = null;
          }
        }
      }
      
      public void setCancel(boolean flag)
      {
        cancelEvent = flag;
      }

      public void setNotifier(INodeNotifier notifier)
      {
        this.notifier = notifier;
      }
    
      public void setEventType(int eventType)
      {
        this.eventType = eventType;
      }

      public void setFeature(Object feature)
      {
        this.feature = feature;
      }

      public void setOldValue(Object oldValue)
      {
        this.oldValue = oldValue;     
      }

      public void setNewValue(Object newValue)
      {
        this.newValue = newValue;
      }

      public void setIndex(int index)
      {
        this.index = index;     
      }

      public INodeNotifier getNotifier()
      {
        return notifier;
      }

      public int getEventType()
      {
        return eventType;
      }

      public Object getNewValue()
      {
        return newValue;
      }

      public Object getOldValue()
      {
        return oldValue;
      }

    }
}


abstract class DocumentAdapter implements INodeAdapter
{
  Document document;
  
  public DocumentAdapter(Document document)
  {
    this.document = document;
    ((INodeNotifier)document).addAdapter(this);
    adapt(document.getDocumentElement());
  }

  public void adapt(Element element)
  {
    if (((INodeNotifier)element).getExistingAdapter(this) == null)
    {
      ((INodeNotifier)element).addAdapter(this);

      for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling())
      {
        if (child.getNodeType() == Node.ELEMENT_NODE)
        {
          adapt((Element)child);
        }
      }
    }
  }

  public boolean isAdapterForType(Object type)
  {
    return type == this;
  }

  abstract public void notifyChanged
    (INodeNotifier notifier, int eventType, Object feature, Object oldValue, Object newValue, int index);
}