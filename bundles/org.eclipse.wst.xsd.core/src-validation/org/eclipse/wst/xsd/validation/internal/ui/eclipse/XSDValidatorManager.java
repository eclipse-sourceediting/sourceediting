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

package org.eclipse.wst.xsd.validation.internal.ui.eclipse;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.xsd.core.internal.XSDCorePlugin;

public class XSDValidatorManager extends AbstractUIPlugin 
{
  public static final String copyright = "(c) Copyright IBM Corporation 2000, 2002.";
  protected static XSDValidatorManager plugin;
 
  public XSDValidatorManager(IPluginDescriptor descriptor) 
  {
    super(descriptor);
    plugin = this;

  }

  /**
   * Copy the w3c XMLSchema.dtd and datatypes.dtd into the plugin metadata directory
   * for validation purposes
   */
  public void startup()
  {
    //ModelManagerPlugin plugin = (ModelManagerPlugin) Platform.getPlugin(ModelManagerPlugin.ID);
    //modelManager = plugin.getModelManager();
  }

  //private static ModelManager modelManager;
  //public static ModelManager getModelManager()
  //{
  //  return modelManager;
  //}

  /**
   * Get the Install URL
   */
  public static URL getInstallURL()
  {
    return XSDCorePlugin.getDefault().getDescriptor().getInstallURL();
  }

  /**
   * Return the plugin physical directory location
   */
  public static IPath getPluginLocation()
  {
    try 
    {
      IPath installPath = new Path(getInstallURL().toExternalForm()).removeTrailingSeparator();
      String installStr = Platform.asLocalURL(new URL(installPath.toString())).getFile();
      return new Path(installStr);
    } 
    catch (IOException e) 
    {

    }
    return null;
  }

  /**
   * Get the metadata directory for this plugin
   */
  //public static String getMetaDataDirectory()
  //{
  //  return getPlugin().getStateLocation().toOSString();
  //}

  /** 
   *  Get the one xmlschema package.
   */
//   public XMLSchemaPackage getXMLSchemaPackage()
//   {
//     return xmlschemaPackage;
//   }

//   /** 
//    *  Get the one xmlschema factory.
//    */
//   public XMLSchemaFactory getXMLSchemaFactory()
//   {
//     return (XMLSchemaFactory)xmlschemaPackage.getEFactoryInstance();
//   }

  /**
   * Get the singleton instance.
   */
  public static XSDValidatorManager getInstance()
  {
    return plugin;
  }


  //public static Image getXSDImage(String iconName)
  //{
  //  return getPlugin().getImage(iconName);
  //}

  /**
   * Get resource string
   */
  public static String getString(String key)
  {
    return XSDCorePlugin.getDefault().getDescriptor().getResourceBundle().getString(key);
  }

  /**
   * Get resource string
   */
  public static String getString(String key, String arg0)
  {
    return MessageFormat.format(getString(key), new Object [] { arg0 });
  }
  
  /**
   * Get resource string
   */
  public static String getString(String key, String arg0, String arg1)
  {
    return MessageFormat.format(getString(key), new Object [] { arg0, arg1 });
  }
  public IWorkspace getWorkspace()
  {
    return ResourcesPlugin.getWorkspace();
  }

  //public static Shell getShell() 
  //{	
  //  return getPlugin().getWorkbench().getActiveWorkbenchWindow().getShell();
  //}

  /**
   * Get the xml schema default namespace prefix
   */
  //public String getXMLSchemaPrefix()
  //{
  //  return getPreferenceStore().getString(CONST_XSD_DEFAULT_PREFIX_TEXT);
  //}

  /**
   * Get the xml schema language qualification
   */
  //public boolean isQualifyXMLSchemaLanguage()
  //{
  //  return getPreferenceStore().getBoolean(CONST_XSD_LANGUAGE_QUALIFY);
  //}
  

  
  public static final String CONST_XSD_DEFAULT_PREFIX_TEXT = "org.eclipse.wst.xmlschema.xsdDefaultPrefixText";
  public static final String CONST_XSD_LANGUAGE_QUALIFY = "org.eclipse.wst.xmlschema.xsdQualify";
  

}
