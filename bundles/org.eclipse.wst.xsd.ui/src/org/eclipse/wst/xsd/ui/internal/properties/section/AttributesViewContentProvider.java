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
package org.eclipse.wst.xsd.ui.internal.properties.section;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.xsd.ui.internal.provider.XSDAbstractAdapter;
import org.eclipse.wst.xsd.ui.internal.provider.XSDAdapterFactoryLabelProvider;
import org.eclipse.wst.xsd.ui.internal.provider.XSDModelAdapterFactoryImpl;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;


public class AttributesViewContentProvider implements ITreeContentProvider, ITableLabelProvider, ILabelProvider, INotifyChangedListener
{
  IEditorPart editorPart;

  static XSDModelAdapterFactoryImpl xsdModelAdapterFactory = new XSDModelAdapterFactoryImpl(); // XSDTextEditor.getXSDModelAdapterFactory();
  static XSDAdapterFactoryLabelProvider adapterFactoryLabelProvider = new XSDAdapterFactoryLabelProvider(xsdModelAdapterFactory);  // XSDTextEditor.getLabelProvider();
  
  XSDAbstractAdapter elementAdapter;
  XSDAbstractAdapter ctAdapter;
  XSDComplexTypeDefinition ct;
  
  /**
   * 
   */
  public AttributesViewContentProvider()
  {
    super();
//    if (xsdModelAdapterFactory instanceof IChangeNotifier)
//    {
//      ((IChangeNotifier)xsdModelAdapterFactory).addListener(this);
//    }

  }
  
  Viewer attributesViewer;
  public AttributesViewContentProvider(IEditorPart editorPart, Viewer viewer)
  {
    super();
    this.editorPart = editorPart;
    this.attributesViewer = viewer;
  }
  
  
  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
   */
  public Object[] getChildren(Object inputElement)
  {
    List list = new ArrayList();
    ct = null;
    if (inputElement instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration xsdElement = (XSDElementDeclaration)inputElement;
      
      if (elementAdapter != null)
      {
        elementAdapter.removeListener((INotifyChangedListener)this);
      }
      elementAdapter = (XSDAbstractAdapter)xsdModelAdapterFactory.adapt(xsdElement, xsdModelAdapterFactory);
      elementAdapter.addListener((INotifyChangedListener)this);

      if (xsdElement.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition)
      {
        ct = (XSDComplexTypeDefinition)xsdElement.getAnonymousTypeDefinition();
      }
      else
      {
        XSDTypeDefinition xsdType = xsdElement.getTypeDefinition();
        if (xsdType instanceof XSDComplexTypeDefinition)
        {
          ct = (XSDComplexTypeDefinition)xsdType;
        }
      }
    }
    else if (inputElement instanceof XSDComplexTypeDefinition)
    {
      ct = (XSDComplexTypeDefinition)inputElement;
    }

    if (ct != null)
    {
      if (ctAdapter != null)
      {
        ctAdapter.removeListener((INotifyChangedListener)this);
      }
      ctAdapter = (XSDAbstractAdapter)xsdModelAdapterFactory.adapt(ct, xsdModelAdapterFactory);
      ctAdapter.addListener((INotifyChangedListener)this);
      
      XSDWildcard wildcard = ct.getAttributeWildcard();
      if (wildcard != null)
      {
        list.add(wildcard);
      }
      
      Iterator i = ct.getAttributeContents().iterator();
      while (i.hasNext())
      {
        XSDAttributeGroupContent attributeGroupContent = (XSDAttributeGroupContent)i.next();
        if (attributeGroupContent instanceof XSDAttributeGroupDefinition)
        {
          XSDAttributeGroupDefinition attributeGroupDefinition = (XSDAttributeGroupDefinition)attributeGroupContent;
          XSDAbstractAdapter a = (XSDAbstractAdapter)xsdModelAdapterFactory.adapt(attributeGroupDefinition, xsdModelAdapterFactory);
          a.removeListener((INotifyChangedListener)this);
 	        a.addListener((INotifyChangedListener)this);
 	        list.add(attributeGroupDefinition);
        }
        else if (attributeGroupContent instanceof XSDAttributeUse)
        {
          XSDAttributeUse attributeUse = (XSDAttributeUse)attributeGroupContent;
          
          XSDAttributeDeclaration attribute = attributeUse.getAttributeDeclaration();
          
          boolean isRef = XSDDOMHelper.isAttributeRef(ct.getElement(), attribute.getQName(), attribute.getTargetNamespace());
         
          if (isRef)
          {
            XSDAbstractAdapter a = (XSDAbstractAdapter)xsdModelAdapterFactory.adapt(attributeUse, xsdModelAdapterFactory);            
            a.removeListener((INotifyChangedListener)this);
   	        a.addListener((INotifyChangedListener)this);
            list.add(attributeUse);
          }
          else
          {
            XSDAbstractAdapter a = (XSDAbstractAdapter)xsdModelAdapterFactory.adapt(attribute, xsdModelAdapterFactory);            
            a.removeListener((INotifyChangedListener)this);
   	        a.addListener((INotifyChangedListener)this);
            list.add(attribute);
          }
        }
      }
    }
    return list.toArray();
  }
  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
   */
  public Object getParent(Object element)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
   */
  public boolean hasChildren(Object element)
  {
    return false;
  }
  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
   */
  public Image getColumnImage(Object element, int columnIndex)
  {
    if (element instanceof XSDConcreteComponent)
    {
	    return adapterFactoryLabelProvider.getImage((XSDConcreteComponent)element);
    }
    return null;
  }
  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
   */
  public String getColumnText(Object element, int columnIndex)
  {
    if (element instanceof XSDConcreteComponent)
    {
	    XSDAbstractAdapter a = (XSDAbstractAdapter)xsdModelAdapterFactory.adapt((XSDConcreteComponent)element, xsdModelAdapterFactory);
	    return a.getText((XSDConcreteComponent)element);
    }
    return ""; 
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
   */
  public Image getImage(Object element)
  {
    if (element instanceof XSDConcreteComponent)
    {
	    return adapterFactoryLabelProvider.getImage((XSDConcreteComponent)element);
    }
    return null;
  }
  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
   */
  public String getText(Object element)
  {
    if (element instanceof XSDConcreteComponent)
    {
	    XSDAbstractAdapter a = (XSDAbstractAdapter)xsdModelAdapterFactory.adapt((XSDConcreteComponent)element, xsdModelAdapterFactory);
	    return a.getText((XSDConcreteComponent)element);
    }
    return ""; 
  }
  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
   */
  public Object[] getElements(Object inputElement)
  {
    return getChildren(inputElement);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
   */
  public void dispose()
  {
    if (elementAdapter != null)
    {
      elementAdapter.removeListener(this);
    }
    if (ctAdapter != null)
    {
      ctAdapter.removeListener((INotifyChangedListener)this);
    }
  }
  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
   */
  Viewer viewer;
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
//    System.out.println("input changed " + oldInput + "\n" + newInput);
    this.viewer = viewer;
  }
  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
   */
  public void addListener(ILabelProviderListener listener)
  {
  }
  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
   */
  public boolean isLabelProperty(Object element, String property)
  {
    return false;
  }
  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
   */
  public void removeListener(ILabelProviderListener listener)
  {
  }

  public void notifyChanged(Notification notification)
  {
    if (attributesViewer != null && !attributesViewer.getControl().isDisposed())
    {
      if (attributesViewer instanceof StructuredViewer)
      {
        attributesViewer.refresh();
      }
    }
  }

}
