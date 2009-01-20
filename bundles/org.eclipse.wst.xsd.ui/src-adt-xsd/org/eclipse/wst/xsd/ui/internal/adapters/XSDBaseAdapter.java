/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObjectListener;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IComplexType;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.w3c.dom.Element;

public class XSDBaseAdapter extends AdapterImpl implements IADTObject, ITreeElement
{  
  protected List listenerList = new ArrayList();
  
  public boolean isAdapterForType(Object type)
  {
    return type == XSDAdapterFactory.getInstance();
  }
  
  public void populateAdapterList(List notifierList, List adapterList)
  {
    for (Iterator i = notifierList.iterator(); i.hasNext(); )
    {
      Object obj = i.next();
      if (obj instanceof XSDConcreteComponent)
      {
        XSDConcreteComponent component = (XSDConcreteComponent)obj;
        adapterList.add(XSDAdapterFactory.getInstance().adapt(component));
      }
      else
      {
        adapterList.add(obj);
      }
    }
  }  
  
  public void registerListener(IADTObjectListener listener)
  {
    if (!listenerList.contains(listener))
    {
      listenerList.add(listener);
    }
  }
  
  public void unregisterListener(IADTObjectListener listener)
  {
    listenerList.remove(listener);
  }
  
  public void notifyChanged(Notification msg)
  {
    super.notifyChanged(msg);
    notifyListeners(this, null);
  }
  
  protected void notifyListeners(Object changedObject, String property)
  {
    List clonedListenerList = new ArrayList();
    clonedListenerList.addAll(listenerList);
    for (Iterator i = clonedListenerList.iterator(); i.hasNext(); )
    {
      IADTObjectListener listener = (IADTObjectListener)i.next();
      listener.propertyChanged(this, property);
    }      
  }
    
  public ITreeElement[] getChildren()
  {
    return null;
  }
  
  public Image getImage()
  {
    return null;
  }
  
  public String getText()
  {
    return ""; //$NON-NLS-1$
  }
  
  public ITreeElement getParent()
  {
    return null;
  }
  
  public boolean hasChildren()
  {
    if (getChildren() != null)
    {
      return getChildren().length > 0;
    }
    return false;
  }
  

  /**
   * Implements IField getContainerType.  Get parent Complex Type containing the field
   * @return IComplexType
   */
  public IComplexType getContainerType()
  {
    XSDConcreteComponent xsdConcreteComponent = (XSDConcreteComponent) target;
    XSDConcreteComponent parent = null;
    XSDComplexTypeDefinition ct = null;
    for (parent = xsdConcreteComponent.getContainer(); parent != null; )
    {
      if (parent instanceof XSDComplexTypeDefinition)
      {
        ct = (XSDComplexTypeDefinition)parent;
        break;
      }
      parent = parent.getContainer();
    }
    if (ct != null)
    {
      return (IComplexType)XSDAdapterFactory.getInstance().adapt(ct);
    }
    return null;
  }
  
  public boolean isReadOnly()
  {
    XSDSchema xsdSchema = null;
    try
    {
      IEditorPart editorPart = null;
      IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      if (window != null)
      {
        IWorkbenchPage page = window.getActivePage();
        if (page != null)
        {
          editorPart = page.getActiveEditor();
        }
      }
      if (target instanceof XSDConcreteComponent)
      {
        xsdSchema = ((XSDConcreteComponent)target).getSchema();
      }
      if (editorPart == null)
      {
        return fallBackCheckIsReadOnly();
      }
      
      XSDSchema editorSchema = (XSDSchema)editorPart.getAdapter(XSDSchema.class);
      if (xsdSchema != null && xsdSchema == editorSchema)
      {
        return false;
      }
      else
      {
        return fallBackCheckIsReadOnly();
      }
    }
    catch(Exception e)
    {

    }
    return true;
  }
  
  private boolean fallBackCheckIsReadOnly()
  {
    Element element = ((XSDConcreteComponent)target).getElement();
    if (element instanceof IDOMNode
        || element instanceof ElementImpl)
    {
       return false;
    }
    return true;
  }

  protected Object getEditorSchema()
  {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
    IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
    return editorPart.getAdapter(XSDSchema.class);
  }
  
  protected IADTObject getGlobalXSDContainer(XSDConcreteComponent component)
  {
    XSDConcreteComponent c = component.getContainer();
    // We want the top most structural component
    while (c != null && 
           !(c.getContainer() instanceof XSDSchema) && 
           !(c instanceof XSDComplexTypeDefinition) &&
           !(c instanceof XSDSimpleTypeDefinition) &&
           !(c instanceof XSDModelGroupDefinition) &&
           !(c instanceof XSDAttributeGroupDefinition))
    {
      c = c.getContainer();
    }
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(c);
    if (adapter instanceof IADTObject)
      return (IADTObject)adapter;
    return null;
  }

  /**
   * Indicates the presence of an abstract attribute
   * @return true if the component's abstract attribute is true
   */
  public boolean isAbstract()
  {
    return false;
  }
}
