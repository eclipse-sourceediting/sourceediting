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
package org.eclipse.wst.xsd.ui.internal.adt.outline;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class ADTLabelProvider implements ILabelProvider
{

  public ADTLabelProvider()
  {
    super();
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
   */
  public Image getImage(Object element)
  {
    if (element instanceof ITreeElement)
    {
      return ((ITreeElement)element).getImage();
    }
    return null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
   */
  public String getText(Object element)
  {
    if (element instanceof ITreeElement)
    {
      return ((ITreeElement)element).getText();
    }
    return ""; //$NON-NLS-1$
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
   */
  public void addListener(ILabelProviderListener listener)
  {
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
   */
  public void dispose()
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

}
