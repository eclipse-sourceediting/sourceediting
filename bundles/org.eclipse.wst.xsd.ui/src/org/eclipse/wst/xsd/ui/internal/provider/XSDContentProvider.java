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
package org.eclipse.wst.xsd.ui.internal.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xsd.ui.internal.text.XSDModelAdapter;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDWildcard;
import org.w3c.dom.Node;

public class XSDContentProvider implements ITreeContentProvider, INotifyChangedListener
{
  XSDModelAdapterFactoryImpl xsdModelAdapterFactory;

  public XSDContentProvider(XSDModelAdapterFactoryImpl xsdModelAdapterFactoryImpl)
  {
    this.xsdModelAdapterFactory = xsdModelAdapterFactoryImpl;
    
    if (xsdModelAdapterFactory instanceof IChangeNotifier)
    {
      ((IChangeNotifier)xsdModelAdapterFactory).addListener(this);
    }
  }
  
  /*
   * @see ITreeContentProvider#getChildren(Object)
   */
  public Object[] getChildren(Object parentElement)
  {
    XSDConcreteComponent xsdComp = null;
    List list = null;
    
    // root/input is structuredmodel
    if (parentElement instanceof IDOMModel) {
		IDOMDocument domDoc = ((IDOMModel) parentElement).getDocument();
		if (domDoc != null) {
			XSDModelAdapter modelAdapter = (XSDModelAdapter) domDoc.getExistingAdapter(XSDModelAdapter.class);
			/*
			 * ISSUE: Didn't want to go through initializing
			 * schema if it does not already exist, so just
			 * attempted to get existing adapter. If doesn't
			 * exist, just don't bother working.
			 */
			if (modelAdapter != null)
				xsdComp = modelAdapter.getSchema();
			if (xsdComp != null) {
		      xsdModelAdapterFactory.adapt(xsdComp, xsdModelAdapterFactory);

		      list = new ArrayList();
		      list.add(xsdComp);
		      return list.toArray();
			}
		}
    }
    else if (parentElement instanceof XSDConcreteComponent)
    {
      xsdComp = (XSDConcreteComponent)parentElement;
      list = new ArrayList();
    }
    else if (parentElement instanceof ITreeItemContentProvider)
    {
      return ((ITreeItemContentProvider)parentElement).getChildren(parentElement).toArray();
    }
    else if (parentElement instanceof ITreeContentProvider)
    {
      return ((ITreeContentProvider)parentElement).getChildren(parentElement);
    }
    
    if (xsdComp != null)
    {
      XSDAbstractAdapter a = (XSDAbstractAdapter)xsdModelAdapterFactory.adapt(xsdComp, xsdModelAdapterFactory);
      
      if (xsdComp instanceof XSDElementDeclaration || xsdComp instanceof XSDModelGroup || xsdComp instanceof XSDWildcard)
      {
        xsdModelAdapterFactory.adapt(((XSDParticleContent)xsdComp).getContainer(), xsdModelAdapterFactory);
      }

      if (a != null)
      {
 	    Object [] obj = a.getChildren(xsdComp);
 	    if (obj != null)
 	    {
 	      list = Arrays.asList(obj);
 	    }
 	  }
    }
    
    list =  list != null ? list : Collections.EMPTY_LIST;
    return list.toArray();
  }

  /*
   * @see ITreeContentProvider#getParent(Object)
   */
  public Object getParent(Object element)
  {
    if (element instanceof Node)
    {
      return ((Node)element).getParentNode();
    }
    else if (element instanceof XSDConcreteComponent)
    {
      return ((XSDConcreteComponent)element).getContainer();
    }
    return null;
  }

  /*
   * @see ITreeContentProvider#hasChildren(Object)
   */
  public boolean hasChildren(Object element)
  {
    Object[] children = getChildren(element);
    return children != null && children.length > 0;   
  }

  /*
   * @see IStructuredContentProvider#getElements(Object)
   */
  public Object[] getElements(Object inputElement)
  {
    return getChildren(inputElement);
  }

  public void notifyChanged(Notification notification)
  {
    if (viewer != null)
    {
      if (viewer instanceof StructuredViewer)
      {
        if (notification.getFeature() instanceof EAttribute)
        {
          ((StructuredViewer)viewer).update(notification.getNotifier(), null);
        }
        else
        {
          ((StructuredViewer)viewer).refresh(notification.getNotifier());
        }
      }
      else
      {
        viewer.refresh();
      }
    }

  }

  /*
   * @see IContentProvider#dispose()
   */
  public void dispose()
  {
    viewer = null;
    xsdModelAdapterFactory.removeListener(this);
  }
  
  protected Viewer viewer = null;

  /*
   * @see IContentProvider#inputChanged(Viewer, Object, Object)
   */
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    this.viewer = viewer;
  }
  
	/**
	 * Gets the xsd schema from document
	 * 
	 * @param document
	 * @return XSDSchema or null of one does not exist yet for document
	 */
	private XSDSchema getXSDSchema(IDocument document) {
		XSDSchema schema = null;
		IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
		if (model != null) {
			try {
				if (model instanceof IDOMModel) {
					IDOMDocument domDoc = ((IDOMModel) model).getDocument();
					if (domDoc != null) {
						XSDModelAdapter modelAdapter = (XSDModelAdapter) domDoc.getExistingAdapter(XSDModelAdapter.class);
						/*
						 * ISSUE: Didn't want to go through initializing
						 * schema if it does not already exist, so just
						 * attempted to get existing adapter. If doesn't
						 * exist, just don't bother working.
						 */
						if (modelAdapter != null)
							schema = modelAdapter.getSchema();
					}
				}
			}
			finally {
				model.releaseFromRead();
			}
		}
		return schema;
	}
}
