/*
 * Copyright (c) 2002 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *   IBM - Initial API and implementation
 *   Jens Lukowski/Innoopract - initial renaming/restructuring
 * 
 */
package org.eclipse.wst.xml.core.internal.catalog;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.XMLCoreMessages;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;
import org.osgi.framework.Bundle;

public class CatalogContributorRegistryReader
{
  protected static final String EXTENSION_POINT_ID = "catalogContributions";
  protected static final String TAG_CONTRIBUTION = "catalogContribution";
  /*
   * this is a sample exptension 
   * <extension point="org.eclipse.wst.xml.core.catalogContributions"> 
   * <catalogContribution>
   *    <uri
   *             name="http://schemas.xmlsoap.org/wsdl/soap/" 
   *             uri="xsd/soap.xsd"/> 
   *   	<public
   *          publicId="-//W3C//DTD XHTML 1.1//EN"
   *          uri="dtds/xhtml11-flat.dtd"/> 
   *  	<nextCatalog id="nestedCatalog" catalog="data/catalog1.xml"/> 
   *  </catalogContribution> 
   *  </extension>
   *  
   */
  protected ICatalog catalog;

  protected String declaringExtensionId;
  
  protected CatalogContributorRegistryReader(ICatalog aCatalog)
  {
    catalog = aCatalog;
  }

  /**
   * read from plugin registry and parse it.
   */
  protected void readRegistry()
  {
    IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
    IExtensionPoint point = pluginRegistry.getExtensionPoint(XMLCorePlugin.getDefault().getBundle().getSymbolicName(), EXTENSION_POINT_ID);
    if (point != null)
    {
      IConfigurationElement[] elements = point.getConfigurationElements();
      for (int i = 0; i < elements.length; i++)
      {
        readElement(elements[i]);
      }
    }
    
  }
  
  public static String resolvePath(URL platformURL, String path) 
  {         
    if (path.startsWith("platform:/plugin"))
    {
      // this is the speclial case, where the resource is located relative 
      // to another plugin (not the one that declares the extension point)
      //
		URL resolvedURL;
		try
		{
			resolvedURL = Platform.resolve(new URL(path));
			return resolvedURL.toString();
		} catch (IOException e)
		{
			return path;
		}
      
    }
    // this is the typical case, where the resource is located relative
    // to the plugin that declares the extension point
    try {
    	URL resolvedURL = Platform.resolve(platformURL);
    	URL absoluteURL = new URL(resolvedURL, path);
    	return absoluteURL.toString();
    } catch (IOException e) {
    	return path;
    }
     
  }
  
  public static URL getPlatformURL(String pluginId){
	  Bundle bundle = Platform.getBundle(pluginId);
		if (bundle != null)
		{
			URL bundleEntry = bundle.getEntry("/");
			
			if(bundleEntry != null){
				try
				{
					return Platform.resolve(bundleEntry);
				} catch (IOException e)
				{
					Logger.logException(e);
				}
			}
		}
		return null;
  }

  private String resolvePath(String path)
  {
	  return resolvePath(getPlatformURL(declaringExtensionId), path);
  }

  protected void readElement(IConfigurationElement element)
  {
    try
    {
	  declaringExtensionId = element.getDeclaringExtension().getNamespace();
    }
    catch (InvalidRegistryObjectException e)
    {
      Logger.logException(e);
    }
    
    if (TAG_CONTRIBUTION.equals(element.getName())){
    	IConfigurationElement[] mappingInfoElementList = element.getChildren(OASISCatalogConstants.TAG_PUBLIC);
    	processMappingInfoElements(mappingInfoElementList);
    	mappingInfoElementList = element.getChildren(OASISCatalogConstants.TAG_SYSTEM);
    	processMappingInfoElements(mappingInfoElementList);
    	mappingInfoElementList = element.getChildren(OASISCatalogConstants.TAG_URI);
    	processMappingInfoElements(mappingInfoElementList);
    	IConfigurationElement[] nextCatalogElementList = element.getChildren(OASISCatalogConstants.TAG_NEXT_CATALOG);
    	processNextCatalogElements(nextCatalogElementList);
    	
    }
    
  }

  private void processMappingInfoElements(IConfigurationElement[] childElementList)
  {
    if (catalog == null)
      return;
    for (int i = 0; i < childElementList.length; i++)
    {
      IConfigurationElement childElement = childElementList[i];
      String name = childElement.getName();
      String key = null;
	
      int type = ICatalogEntry.ENTRY_TYPE_PUBLIC;
      if (OASISCatalogConstants.TAG_PUBLIC.equals(name))
      {
        key = childElement.getAttribute(OASISCatalogConstants.ATTR_PUBLIC_ID);
      }
      else if (OASISCatalogConstants.TAG_SYSTEM.equals(name))
      {
        key = childElement.getAttribute(OASISCatalogConstants.ATTR_SYSTEM_ID);
        type = ICatalogEntry.ENTRY_TYPE_SYSTEM;
      }
      else if (OASISCatalogConstants.TAG_URI.equals(name))
      {
        key = childElement.getAttribute(OASISCatalogConstants.ATTR_NAME);
        type = ICatalogEntry.ENTRY_TYPE_URI;
      }
      else if (OASISCatalogConstants.TAG_NEXT_CATALOG.equals(name))
      {
        processNextCatalogElements(new IConfigurationElement[]{childElement});
        continue;
      }
      if (key == null || key.equals(""))
      {
         Logger.log(Logger.ERROR, XMLCoreMessages.Catalog_entry_key_not_set);
        continue;
      }
      String entryURI = childElement.getAttribute(OASISCatalogConstants.ATTR_URI); // mandatory
      if (entryURI == null || entryURI.equals(""))
      {
       Logger.log(Logger.ERROR, XMLCoreMessages.Catalog_entry_uri_not_set);
        continue;
      }
      ICatalogElement catalogElement = catalog.createCatalogElement(type);
      if (catalogElement instanceof ICatalogEntry)
      {
        ICatalogEntry entry = (ICatalogEntry) catalogElement;
        entry.setKey(key);
		String resolvedPath = resolvePath(entryURI);
		entry.setURI(resolvedPath);
        String id = childElement.getAttribute(OASISCatalogConstants.ATTR_ID); // optional
        if (id != null && !id.equals(""))
        {
          entry.setId(id);
        }
      }
      // process any other attributes
      for (int j = 0; j < childElement.getAttributeNames().length; j++)
      {
        String attrName = childElement.getAttributeNames()[j];
        if (!attrName.equals(OASISCatalogConstants.ATTR_URI) && !attrName.equals(OASISCatalogConstants.ATTR_NAME) && !attrName.equals(OASISCatalogConstants.ATTR_PUBLIC_ID)
            && !attrName.equals(OASISCatalogConstants.ATTR_SYSTEM_ID) && !attrName.equals(OASISCatalogConstants.ATTR_CATALOG) && !attrName.equals(OASISCatalogConstants.ATTR_ID)
            && !attrName.equals(OASISCatalogConstants.ATTR_BASE))
        {
          String attrValue = childElement.getAttribute(attrName);
          if (attrValue != null && !attrValue.equals(""))
          {
            catalogElement.setAttributeValue(attrName, attrValue);
          }
        }
      }
      catalog.addCatalogElement(catalogElement);
    }
  }

  private void processNextCatalogElements(IConfigurationElement[] childElementList)
  {
    if (catalog == null)
      return;
    for (int i = 0; i < childElementList.length; i++)
    {
      IConfigurationElement childElement = childElementList[i];
      String location = childElement.getAttribute(OASISCatalogConstants.ATTR_CATALOG); // mandatory
      if (location == null || location.equals(""))
      {
        Logger.log(Logger.ERROR, XMLCoreMessages.Catalog_next_catalog_location_uri_not_set);
        continue;
      }
      INextCatalog nextCatalog = new NextCatalog();
	  String resolvedPath = resolvePath(location);
      nextCatalog.setCatalogLocation(resolvedPath);
	  String id = childElement.getAttribute(OASISCatalogConstants.ATTR_ID);
	  if (id != null && !id.equals(""))
      { 
		  nextCatalog.setId(id);
      }
      catalog.addCatalogElement(nextCatalog);
    }
  }
  
 
 
}
