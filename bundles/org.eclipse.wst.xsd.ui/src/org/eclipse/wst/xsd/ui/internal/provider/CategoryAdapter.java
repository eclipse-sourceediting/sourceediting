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

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xsd.XSDSchema;


public class CategoryAdapter // extends ItemProvider
  implements ILabelProvider, IChangeNotifier, ITreeContentProvider
{
  
  protected String text;

  /**
   * This is the image returned by {@link #getImage getImage(Object)}.
   */
  protected Image image;

  /**
   * This is the parent returned by {@link #getParent getParent(Object)}.
   */
  protected Object parent;

  public CategoryAdapter(String label, Image image, Collection children, XSDSchema xsdSchema, int groupType)
  {
//    super(label, image, xsdSchema);
    this.text = label;
    this.image = image;
    this.parent = xsdSchema;
    this.xsdSchema = xsdSchema;
    this.children = children;
    this.groupType = groupType;
  }
  
  public final static int ATTRIBUTES = 1;
  public final static int ELEMENTS = 2;
  public final static int TYPES = 3;
  public final static int GROUPS = 5;
  public final static int DIRECTIVES = 6;
  public final static int NOTATIONS = 7;
  public final static int ATTRIBUTE_GROUPS = 8;
  public final static int IDENTITY_CONSTRAINTS = 9;
  public final static int ANNOTATIONS = 10;
                                 
  protected int groupType;
  Collection children;
  XSDSchema xsdSchema;
  
  public XSDSchema getXSDSchema()
  {
    return xsdSchema;
  }
  
  public int getGroupType()
  {
    return groupType;
  }   

//  public boolean hasChildren(Object o)
//  {
//    return !children.isEmpty();
//  }

//  public Collection getChildren(Object o)
//  {
//    return children;
//  }
  
  public Image getImage(Object element)
  {
    return image;
  }
  
  public String getText(Object object)
  {
    // return object.toString();
    return text;
  }
  
  public void addListener(ILabelProviderListener listener)
  {
    
  }
  
  public boolean isLabelProperty(Object element, String property)
  {
    return false;
  }

  public void removeListener(ILabelProviderListener listener)
  {
    
  }

  public void fireNotifyChanged(Notification notification)
  {
    
  }

  /**
   * This adds another listener.
   */
  public void addListener(INotifyChangedListener notifyChangedListener)
  {
    
  }

  /**
   * This removes a listener.
   */
  public void removeListener(INotifyChangedListener notifyChangedListener)
  {
    
  }

  public void dispose()
  {
    
  }
  
  public Object[] getElements(Object inputElement)
  {
    return getChildren(inputElement);
  }

  public Object[] getChildren(Object parentElement)
  {
    return children.toArray();
  }
  
  public void setChildren(Collection list)
  {
    children = list;
  }

  public Object getParent(Object element)
  {
    return xsdSchema;
  }
  
  public boolean hasChildren(Object element)
  {
    return true;
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    
  }
}
