/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.factory;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.xml.core.internal.Logger;


public class CMDocumentFactoryDescriptor
{
  private IConfigurationElement fElement;
  private CMDocumentFactory factory;

  public CMDocumentFactoryDescriptor(IConfigurationElement element)
  {
    this.fElement = element;
  }

  public CMDocumentFactory getFactory()
  {
    if (factory == null)
    {
      try
      {
        factory = (CMDocumentFactory) fElement.createExecutableExtension("class"); //$NON-NLS-1$
      }
      catch (Exception e)
      {
        Logger.logException("Exception loading CMDocumentFactory",e); //$NON-NLS-1$
      }
    }
    return factory;
  }
}
