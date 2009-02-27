/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom;

import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ILabelProvider;

public class NodeCustomizationRegistry
{
  private static final String NAMESPACE = "namespace"; //$NON-NLS-1$  
  private static final String LABEL_PROVIDER_CLASS_ATTRIBUTE_NAME = "labelProviderClass";     
  private static final String NODE_EDITOR_PROVIDER_CLASS_ATTRIBUTE_NAME = "nodeEditorProviderClass"; //$NON-NLS-1$
  private static final String FILTER_CLASS_ATTRIBUTE_NAME = "filterClass"; 


  protected String extensionId;
  protected HashMap map;

  public NodeCustomizationRegistry(String propertyEditorExtensionId)
  {
    extensionId = "org.eclipse.wst.xsd.ui.extensibilityNodeCustomizations";//propertyEditorExtensionId;
  }  
  
  private class Descriptor
  {
    IConfigurationElement configurationElement;
    NodeEditorProvider nodeEditorProvider;
    NodeFilter nodeFilter;    
    boolean nodeEditorProviderFailedToLoad = false;
    boolean labelProviderFailedToLoad = false;
    
    Descriptor(IConfigurationElement element)
    {
      this.configurationElement = element;
    }
    
    NodeEditorProvider lookupOrCreateNodeEditorProvider()
    {
      if (nodeEditorProvider == null && !nodeEditorProviderFailedToLoad)
      {
        try
        {
          nodeEditorProvider = (NodeEditorProvider)configurationElement.createExecutableExtension(NODE_EDITOR_PROVIDER_CLASS_ATTRIBUTE_NAME);
        }
        catch (Exception e) 
        {
          nodeEditorProviderFailedToLoad = true;
        }
      } 
      return nodeEditorProvider;
    }
    
    ILabelProvider createLabelProvider()
    {
      if (!labelProviderFailedToLoad)
      {  
        try
        {
          return (ILabelProvider)configurationElement.createExecutableExtension(LABEL_PROVIDER_CLASS_ATTRIBUTE_NAME);
        }
        catch (Exception e) 
        {
          labelProviderFailedToLoad = true;
        }
      }
      return null;
    }

    public NodeFilter getNodeFilter()
    {
      if (!nodeEditorProviderFailedToLoad)
      {  
        try
        {
          nodeFilter = (NodeFilter)configurationElement.createExecutableExtension(FILTER_CLASS_ATTRIBUTE_NAME);
        }
        catch (Exception e) 
        {
          nodeEditorProviderFailedToLoad = true;
        }
      }
      return nodeFilter;
    }
  }

  
  private HashMap initMap()
  {
    HashMap theMap = new HashMap();
    IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.wst.xsd.ui.extensibilityNodeCustomizations");
    for (int i = 0; i < extensions.length; i++)
    {
      IConfigurationElement configurationElement = extensions[i];
      String namespace = configurationElement.getAttribute(NAMESPACE);
      if (namespace != null)
      {
        theMap.put(namespace, new Descriptor(configurationElement));
      }     
    }    
    return theMap;
  }

  private Descriptor getDescriptor(String namespace)
  {
    map = null;
    if (namespace != null)
    { 
      if (map == null)
      {  
        map = initMap();
      }  
      return (Descriptor)map.get(namespace);
    }
    return null;     
  }
  
  public NodeEditorProvider getNodeEditorProvider(String namespace)
  {     
    Descriptor descriptor = getDescriptor(namespace);
    if (descriptor != null)
    {  
      return descriptor.lookupOrCreateNodeEditorProvider();       
    }
    return null;
  }
  
  public ILabelProvider getLabelProvider(String namespace)
  {
    Descriptor descriptor = getDescriptor(namespace);
    if (descriptor != null)
    {  
      return descriptor.createLabelProvider();       
    }
    return null;    
  }
  
  public NodeFilter getNodeFilter(String namespace)
  {
    Descriptor descriptor = getDescriptor(namespace);
    if (descriptor != null)
    {  
      return descriptor.getNodeFilter();       
    }
    return null;    
  }
}
