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

import com.ibm.icu.util.StringTokenizer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.core.internal.Logger;




public class CMDocumentFactoryRegistryReader
{
  protected static final String EXTENSION_POINT_ID = "documentFactories"; //$NON-NLS-1$
  protected static final String TAG_NAME = "factory"; //$NON-NLS-1$
  protected static final String ATT_CLASS = "class"; //$NON-NLS-1$
  protected static final String ATT_TYPE = "type";   //$NON-NLS-1$
  protected String pluginId, extensionPointId;
  
  protected CMDocumentFactoryRegistry registry;
  
  public CMDocumentFactoryRegistryReader(CMDocumentFactoryRegistry registry)
  {
  	this.registry = registry;
  }

  public void readRegistry()
  {
    String bundleid = "org.eclipse.wst.xml.core"; //$NON-NLS-1$
    IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(bundleid, EXTENSION_POINT_ID);
    if (point != null)
    {
      IConfigurationElement[] elements = point.getConfigurationElements();
      for (int i = 0; i < elements.length; i++)
      {
        readElement(elements[i]);
      }
    }
  }

  protected void readElement(IConfigurationElement element)
  {
    if (element.getName().equals(TAG_NAME))
    {
      String factoryClass = element.getAttribute(ATT_CLASS);
      String filenameExtensions = element.getAttribute(ATT_TYPE);
      if (factoryClass != null && filenameExtensions != null)
      {
        try
        {
          CMDocumentFactoryDescriptor descriptor = new CMDocumentFactoryDescriptor(element);
          for (StringTokenizer st = new StringTokenizer(filenameExtensions, ","); st.hasMoreTokens(); ) //$NON-NLS-1$
          {
          	String token = st.nextToken().trim();
			registry.putFactory(token, descriptor);	
          }         
        }
        catch (Exception e)
        {
			Logger.logException(e);
        }
      }
    }
  }
}
