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
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

public class XSDAbstractAdapter extends AdapterImpl
  implements ITreeContentProvider, ILabelProvider, IChangeNotifier
{

  protected AdapterFactory adapterFactory;
  /**
   * 
   */
  public XSDAbstractAdapter(AdapterFactory adapterFactory)
  {
    super();
    this.adapterFactory = adapterFactory;
  }
  
  /**
   * The adapter factory is used as the type key.
   * This returns true, only if this adapter was created by the given factory.
   */
  public boolean isAdapterForType(Object type)
  {
    return type == adapterFactory;
  }

  public Image getImage(Object element)
  {
    return null;
  }
  
  public String getText(Object object)
  {
    return object.toString();
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
  
  public Object[] getChildren(Object parentElement)
  {
    List list = new ArrayList();
    return list.toArray();
  }
  
  public boolean hasChildren(Object object)
  {
    return false;
  }

  public Object getParent(Object object)
  {
    return null;
  }

  public Object[] getElements(Object inputElement)
  {
    return getChildren(inputElement);
  }

  public void dispose()
  {
    
  }
  
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    
  }

  /**
   * This is used to implement {@link IChangeNotifier}.
   */
  protected IChangeNotifier changeNotifier;

  
  public void notifyChanged(Notification msg)
  {                        
    if (msg.getEventType() != Notification.RESOLVE)
    {        
      fireNotifyChanged(msg);
    }
  }  

  /**
   * This calls {@link org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged notifyChanged} for each listener.
   */
  public void fireNotifyChanged(Notification notification)
  {
    if (changeNotifier != null)
    {
      changeNotifier.fireNotifyChanged(notification);
    }

    if (adapterFactory instanceof IChangeNotifier)
    {
      IChangeNotifier changeNotifier = (IChangeNotifier)adapterFactory;
      changeNotifier.fireNotifyChanged(notification);
    }
  }


  /**
   * This adds another listener.
   */
  public void addListener(INotifyChangedListener notifyChangedListener)
  {
    if (changeNotifier == null)
    {
      changeNotifier = new ChangeNotifier();
    }
    changeNotifier.addListener(notifyChangedListener);
   
  }

  /**
   * This removes a listener.
   */
  public void removeListener(INotifyChangedListener notifyChangedListener)
  {
    if (changeNotifier != null)
    {
      changeNotifier.removeListener(notifyChangedListener);
    }
   
  }

}
