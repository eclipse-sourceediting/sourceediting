/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;
import org.eclipse.wst.xsd.ui.internal.common.commands.ExtensibleAddExtensionCommand;
import org.w3c.dom.Element;

public class ExtensionsSchemasRegistry
{
  public static final String USER_ADDED_EXT_SCHEMAS = "USER-ADDED-EXT-SCHEMAS";  // TODO externalize
  private static final String LOCATION_PREFIX = "platform:/plugin/"; //$NON-NLS-1$
  public static final String DESCRIPTION = "description"; //$NON-NLS-1$
  public static final String DISPLAYNAME = "displayName"; //$NON-NLS-1$
  public static final String NAMESPACEURI = "namespaceURI"; //$NON-NLS-1$
  public static final String XSDFILEURL = "xsdFileURL"; //$NON-NLS-1$
  public static final String LABELPROVIDER = "labelProviderClass"; //$NON-NLS-1$
  public static final String ADD_COMMAND_CLASS = "addCommandClass"; //$NON-NLS-1$
  
  protected IPreferenceStore prefStore;
  protected String extensionId;

  HashMap propertyMap, commandMap;
  ArrayList nsURIProperties;
  private ICatalog systemCatalog;
  private String deprecatedExtensionId;
  
  public ExtensionsSchemasRegistry(String appinfo_extensionid)
  {
    extensionId = appinfo_extensionid;
  }
  
  public void __internalSetDeprecatedExtensionId(String deprecatedId)
  {
    deprecatedExtensionId = deprecatedId;
  }
  
  public void setPrefStore(IPreferenceStore store)
  {
	  prefStore = store;
  }

  public List getAllExtensionsSchemasContribution()
  {  
    // If we read the registry, then let's not do it again.
    if (nsURIProperties != null)
    {
      return nsURIProperties;
    }
 
    nsURIProperties = new ArrayList();
    propertyMap = new HashMap();
    commandMap = new HashMap();

    getAllExtensionsSchemasContribution(extensionId);
    if (deprecatedExtensionId != null)
    {
      getAllExtensionsSchemasContribution(deprecatedExtensionId);     
    }  
    
    // get user-added schemas stored in preference
    if (prefStore != null)
    {
      String value = prefStore.getString(USER_ADDED_EXT_SCHEMAS);
      StringTokenizer tokenizer = new StringTokenizer(value, "\n");
   
      while ( tokenizer.hasMoreTokens() )
      {
        nsURIProperties.add( new SpecificationForExtensionsSchema( tokenizer.nextToken() ) );
      }
    }
    
    return nsURIProperties;
  }
  
  private List getAllExtensionsSchemasContribution(String id)
  {
    IConfigurationElement[] asiPropertiesList = Platform.getExtensionRegistry().getConfigurationElementsFor(id);

    boolean hasASIProperties = (asiPropertiesList != null) && (asiPropertiesList.length > 0);

    if (hasASIProperties)
    {
      for (int i = 0; i < asiPropertiesList.length; i++)
      {
        IConfigurationElement asiPropertiesElement = asiPropertiesList[i];
        String description = asiPropertiesElement.getAttribute(DESCRIPTION);
        String displayName = asiPropertiesElement.getAttribute(DISPLAYNAME);
        String namespaceURI = asiPropertiesElement.getAttribute(NAMESPACEURI);
        String xsdFileURL = asiPropertiesElement.getAttribute(XSDFILEURL);
        String labelProviderClass = asiPropertiesElement.getAttribute(LABELPROVIDER);
        String actionHandlerClass = asiPropertiesElement.getAttribute(ADD_COMMAND_CLASS);

        if (displayName == null)
        {
          // If there is no display name, force the user
          // to manually create a name. Therefore, we ignore entry without
          // a display name.
          continue;
        }

        if (xsdFileURL == null)
        {
          xsdFileURL = locateFileUsingCatalog(namespaceURI);
        }

        SpecificationForExtensionsSchema extensionsSchemaSpec = createEntry();
        extensionsSchemaSpec.setDescription(description);
        extensionsSchemaSpec.setDisplayName(displayName);
        extensionsSchemaSpec.setNamespaceURI(namespaceURI);
        extensionsSchemaSpec.setDefautSchema();

        String pluginId = asiPropertiesElement.getDeclaringExtension().getContributor().getName();

        if (labelProviderClass != null)
        {
          ILabelProvider labelProvider = null;
          try
          {
            Class theClass = Platform.getBundle(pluginId).loadClass(labelProviderClass);
            if (theClass != null)
            {
              labelProvider = (ILabelProvider) theClass.newInstance();
              if (labelProvider != null)
              {
                propertyMap.put(namespaceURI, labelProvider);
                extensionsSchemaSpec.setLabelProvider(labelProvider);
              }
            }
          }
          catch (Exception e)
          {

          }
        }
        
        if (actionHandlerClass != null)
        {
          try
          {
            ExtensibleAddExtensionCommand actionHandler = (ExtensibleAddExtensionCommand)asiPropertiesElement.createExecutableExtension(ADD_COMMAND_CLASS);
            if (actionHandler != null)
            {
              commandMap.put(namespaceURI, actionHandler);
              extensionsSchemaSpec.setExtensibleAddExtensionCommand(actionHandler);
            }
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
        
        extensionsSchemaSpec.setLocation(LOCATION_PREFIX + pluginId + "/" + xsdFileURL); //$NON-NLS-1$

        nsURIProperties.add(extensionsSchemaSpec);
      }

    }
    
    return nsURIProperties;
  }
  
  public ExtensibleAddExtensionCommand getAddExtensionHandler(String namespace)
  {
    // Didn't retrieve the config elements yet.
    if (commandMap == null)
    {
      getAllExtensionsSchemasContribution();
    }

    Object object = commandMap.get(namespace);
    if (object instanceof ExtensibleAddExtensionCommand)
    {
      return (ExtensibleAddExtensionCommand) object;
    }
    return null;
  }

  /**
   * @deprecated
   */
  public ILabelProvider getLabelProvider(Element element)
  {/*
    String uri = element.getNamespaceURI();
    if (uri == null)
      uri = ""; //$NON-NLS-1$

    // Didn't retrieve the config elements yet.
    if (propertyMap == null)
    {
      getAllExtensionsSchemasContribution();
    }

    Object object = propertyMap.get(uri);
    if (object instanceof ILabelProvider)
    {
      return (ILabelProvider) object;
    }*/
    return null;
  }

  public SpecificationForExtensionsSchema createEntry()
  {
    return new SpecificationForExtensionsSchema();
  }

  /**
   * Returns the String location for the schema with the given namespaceURI by
   * looking at the XML catalog. We look only in the plugin specified entries of
   * the catalog.
   * 
   * @param namespaceURI
   * @return String representing the location of the schema.
   */
  private String locateFileUsingCatalog(String namespaceURI)
  {
    retrieveCatalog();

    ICatalogEntry[] entries = systemCatalog.getCatalogEntries();
    for (int i = 0; i < entries.length; i++)
    {
      if (entries[i].getKey().equals(namespaceURI))
        return entries[i].getURI();
    }

    return null;
  }

  private void retrieveCatalog()
  {
    if (systemCatalog != null)
      return;

    ICatalog defaultCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
    INextCatalog[] nextCatalogs = defaultCatalog.getNextCatalogs();
    for (int i = 0; i < nextCatalogs.length; i++)
    {
      INextCatalog catalog = nextCatalogs[i];
      ICatalog referencedCatalog = catalog.getReferencedCatalog();
      if (referencedCatalog != null)
      {
        if (XMLCorePlugin.SYSTEM_CATALOG_ID.equals(referencedCatalog.getId()))
        {
          systemCatalog = referencedCatalog;
        }
      }
    }
  }

}
