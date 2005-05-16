package org.eclipse.wst.xsd.ui.internal.text;

import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
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
//      System.out.println(eventType + " : HandleNotifyChange " + notifier.hashCode() + " notifier " + notifier);
      switch (eventType)
      {
        case INodeNotifier.ADD:
        {
          if (newValue instanceof Element)
          {
            adapt((Element)newValue);
//  Add     updateParentForDerivation(node, listener);
          }
          break;
        }
        case INodeNotifier.REMOVE:
        {
          Node node = (Node)notifier;
          XSDConcreteComponent listener = schema.getCorrespondingComponent(node);
         
          if (listener instanceof XSDSchema)
          {
            // we want to reset the schema's external elements when the directive is deleted
            if (feature instanceof Element)
            {
              Element elem = (Element)feature;
              if (XSDDOMHelper.inputEquals(elem, XSDConstants.INCLUDE_ELEMENT_TAG, false) ||
                  XSDDOMHelper.inputEquals(elem, XSDConstants.IMPORT_ELEMENT_TAG, false) ||
                  XSDDOMHelper.inputEquals(elem, XSDConstants.REDEFINE_ELEMENT_TAG, false))
              {
                schema.reset();
                schema.update();
              }
            }
          }          
        }
        case INodeNotifier.CHANGE:
        {
          Node node = (Node)notifier;
          XSDConcreteComponent listener = schema.getCorrespondingComponent(node);
          if (node.getNodeType() == Node.ELEMENT_NODE)
          {
            listener.elementAttributesChanged((Element)node);
            listener.elementChanged((Element)node);
          }
          else if (node.getNodeType() == Node.DOCUMENT_NODE)
          {
            listener.elementAttributesChanged(((Document)node).getDocumentElement());
            listener.elementChanged(((Document)node).getDocumentElement());
          }
          break;
        }
        case INodeNotifier.STRUCTURE_CHANGED:
        case INodeNotifier.CONTENT_CHANGED:
        {
          Node node = (Node)notifier;
          XSDConcreteComponent listener = schema.getCorrespondingComponent(node);
          if (node.getNodeType() == Node.ELEMENT_NODE)
          {
            listener.elementContentsChanged((Element)node);
            break;
          }
          else if (node.getNodeType() == Node.DOCUMENT_NODE)
          {
            Element docElement = ((Document)node).getDocumentElement();
            // Need to add check if doc element is being edited in the source
            if (docElement != null)
            {
              String prefix = docElement.getPrefix();
              String xmlnsString = prefix == null? "xmlns" : "xmlns:" + prefix;
              Attr attr = docElement.getAttributeNode(xmlnsString);
              boolean doParse = false;
              if (attr != null)
              {
                if (attr.getValue().equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001) && docElement.getLocalName().equals("schema"))
                {
                  // We have a viable schema so parse it
                  doParse = true;
                }
              }
              
              if (doParse)
              {
                adapt(docElement);
                schema.setElement(docElement);
              }
            }
          }
          break;
        }
      }
    }

    protected DelayedEvent delayedTask;
    protected void startDelayedEvent(INodeNotifier notifier, int eventType, Object feature, Object oldValue, Object newValue, int index)
    {
//      System.out.println("start delayed event");
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