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
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.xsd.ui.internal.common.commands.ExtensibleAddExtensionCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.ExtensibleRemoveExtensionNodeCommand;
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
  public static final String DELETE_COMMAND_CLASS = "deleteCommandClass"; //$NON-NLS-1$
  public static final String CATEGORY_PROVIDER_CLASS = "class"; //$NON-NLS-1$
  
  protected IPreferenceStore prefStore;
  protected String extensionId;

  protected ArrayList nsURIProperties, categoryProviderList;
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
    categoryProviderList = new ArrayList();

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
        String elementName = asiPropertiesElement.getName();

        if ("category".equals(elementName))
        {
          String description = asiPropertiesElement.getAttribute(DESCRIPTION);
          String displayName = asiPropertiesElement.getAttribute(DISPLAYNAME);
          String namespaceURI = asiPropertiesElement.getAttribute(NAMESPACEURI);
          String xsdFileURL = asiPropertiesElement.getAttribute(XSDFILEURL);
          String labelProviderClass = asiPropertiesElement.getAttribute(LABELPROVIDER);
          String addCommandClass = asiPropertiesElement.getAttribute(ADD_COMMAND_CLASS);
          String deleteCommandClass = asiPropertiesElement.getAttribute(DELETE_COMMAND_CLASS);
          
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
                  extensionsSchemaSpec.setLabelProvider(labelProvider);
                }
              }
            }
            catch (Exception e)
            {

            }
          }
          
          if (addCommandClass != null)
          {
            try
            {
              ExtensibleAddExtensionCommand addCommand = (ExtensibleAddExtensionCommand)asiPropertiesElement.createExecutableExtension(ADD_COMMAND_CLASS);
              if (addCommand != null)
              {
                extensionsSchemaSpec.setExtensibleAddExtensionCommand(addCommand);
              }
            }
            catch (Exception e)
            {
            }
          }

          if (deleteCommandClass != null)
          {
            try
            {
              ExtensibleRemoveExtensionNodeCommand deleteCommand = (ExtensibleRemoveExtensionNodeCommand)asiPropertiesElement.createExecutableExtension(DELETE_COMMAND_CLASS);
              if (deleteCommand != null)
              {
                extensionsSchemaSpec.setExtensibleRemoveExtensionNodeCommand(deleteCommand);
              }
            }
            catch (Exception e)
            {
            }
          }

          extensionsSchemaSpec.setLocation(LOCATION_PREFIX + pluginId + "/" + xsdFileURL); //$NON-NLS-1$

          nsURIProperties.add(extensionsSchemaSpec);

        }
        else if ("categoryProvider".equals(elementName))
        {
          String categoryProviderClass = asiPropertiesElement.getAttribute(CATEGORY_PROVIDER_CLASS);
          
          if (categoryProviderClass != null)
          {
            try
            {
              CategoryProvider categoryProvider = (CategoryProvider)asiPropertiesElement.createExecutableExtension(CATEGORY_PROVIDER_CLASS);
              if (categoryProvider != null)
              {
                categoryProviderList.add(categoryProvider);
              }
            }
            catch (Exception e)
            {
            }
          }
          
        }
        
      }

    }
    
    return nsURIProperties;
  }
  
  public ExtensibleAddExtensionCommand getAddExtensionCommand(String namespace)
  {
    // Didn't retrieve the config elements yet.
    if (nsURIProperties == null)
    {
      getAllExtensionsSchemasContribution();
    }
    
    for (Iterator i = nsURIProperties.iterator(); i.hasNext(); )
    {
      SpecificationForExtensionsSchema spec = (SpecificationForExtensionsSchema)i.next();
      String nsURI = spec.getNamespaceURI();
      if (nsURI != null && nsURI.equals(namespace))
      {
        return spec.getExtensibleAddExtensionCommand();
      }
    }

    for (Iterator i = categoryProviderList.iterator(); i.hasNext(); )
    {
      CategoryProvider categoryProvider = (CategoryProvider)i.next();
      for (Iterator j = categoryProvider.getCategories().iterator(); j.hasNext(); )
      {
        SpecificationForExtensionsSchema spec = (SpecificationForExtensionsSchema) j.next();
        String namespaceURI = spec.getNamespaceURI();
        if (namespaceURI != null && namespaceURI.equals(namespace))
        {
          return spec.getExtensibleAddExtensionCommand();
        }
      }
    }

    return null;
  }

  public ExtensibleRemoveExtensionNodeCommand getRemoveExtensionNodeCommand(String namespace)
  {
    // Didn't retrieve the config elements yet.
    if (nsURIProperties == null)
    {
      getAllExtensionsSchemasContribution();
    }
    
    for (Iterator i = nsURIProperties.iterator(); i.hasNext(); )
    {
      SpecificationForExtensionsSchema spec = (SpecificationForExtensionsSchema)i.next();
      String nsURI = spec.getNamespaceURI();
      if (nsURI != null && nsURI.equals(namespace))
      {
        return spec.getExtensibleRemoveExtensionNodeCommand();
      }
    }
    
    for (Iterator i = categoryProviderList.iterator(); i.hasNext(); )
    {
      CategoryProvider categoryProvider = (CategoryProvider)i.next();
      for (Iterator j = categoryProvider.getCategories().iterator(); j.hasNext(); )
      {
        SpecificationForExtensionsSchema spec = (SpecificationForExtensionsSchema) j.next();
        String namespaceURI = spec.getNamespaceURI();
        if (namespaceURI != null && namespaceURI.equals(namespace))
        {
          return spec.getExtensibleRemoveExtensionNodeCommand();
        }
      }
    }
    
    return null;
  }
  
  public List getCategoryProviders()
  {
    if (nsURIProperties == null)
    {
      getAllExtensionsSchemasContribution();
    }
    
    return categoryProviderList;
  }

  /**
   * @deprecated
   */
  public ILabelProvider getLabelProvider(Element element)
  {
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
    URIResolver resolver = URIResolverPlugin.createResolver();
    String result = resolver.resolve("", namespaceURI, "");
    return resolver.resolvePhysicalLocation("", namespaceURI, result);
  }

}
