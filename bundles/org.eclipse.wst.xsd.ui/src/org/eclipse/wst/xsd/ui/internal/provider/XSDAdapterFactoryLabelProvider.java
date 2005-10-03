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
import java.util.Collection;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class XSDAdapterFactoryLabelProvider implements ILabelProvider, INotifyChangedListener, ITableLabelProvider
{
  protected XSDModelAdapterFactoryImpl adapterFactory;
  protected Collection labelProviderListeners;
  
  private static final Class ILabelProviderClass = ILabelProvider.class;

  /**
   * 
   */
  public XSDAdapterFactoryLabelProvider(XSDModelAdapterFactoryImpl adapterFactory)
  {
    this.adapterFactory = adapterFactory;
    labelProviderListeners = new ArrayList();
  }

  /**
   * Return the wrapped AdapterFactory.
   */
  public AdapterFactory getAdapterFactory()
  {
    return adapterFactory;
  }

  /**
   * Set the wrapped AdapterFactory.
   */
  public void setAdapterFactory(XSDModelAdapterFactoryImpl adapterFactory)
  {
    if (this.adapterFactory instanceof IChangeNotifier)
    {
      ((IChangeNotifier)this.adapterFactory).removeListener(this);
    }

    if (adapterFactory instanceof IChangeNotifier)
    {
      ((IChangeNotifier)adapterFactory).addListener(this);
    }

    this.adapterFactory = adapterFactory;
  }

  /**
   * Since we won't ever generate these notifications, we can just ignore this.
   */
  public void addListener(ILabelProviderListener listener) 
  {
    labelProviderListeners.add(listener);
  }

  /**
   * Since we won't ever add listeners, we can just ignore this.
   */
  public void removeListener(ILabelProviderListener listener)
  {
    labelProviderListeners.remove(listener);
  }

  /**
   * This discards the content provider and removes this as a listener to the {@link #adapterFactory}.
   */
  public void dispose()
  {
    if (this.adapterFactory instanceof IChangeNotifier)
    {
      ((IChangeNotifier)adapterFactory).removeListener(this);
    }
  }

  /**
   * This always returns true right now.
   */
  public boolean isLabelProperty(Object object, String id)
  {
    return true;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
   */
  public Image getImage(Object object)
  {
    ILabelProvider labelProvider = (ILabelProvider)adapterFactory.adapt(object, ILabelProviderClass);

    return 
      labelProvider != null ?
        labelProvider.getImage(object) : null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
   */
  public String getText(Object object)
  {
    // Get the adapter from the factory.
    //
    ILabelProvider labelProvider = (ILabelProvider)adapterFactory.adapt(object, ILabelProviderClass);

    return
      labelProvider != null ?
        labelProvider.getText(object) :
        object == null ? 
          "" :
          object.toString();
  }

  /* (non-Javadoc)
   * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
   */
  public void notifyChanged(Notification notification)
  {
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
   */
  public Image getColumnImage(Object element, int columnIndex)
  {
    return getImage(element);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
   */
  public String getColumnText(Object element, int columnIndex)
  {
    return getText(element);
  }

}
