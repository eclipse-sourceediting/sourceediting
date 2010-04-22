/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.editor;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.*;
import org.eclipse.wst.xsd.ui.internal.adt.editor.ProductCustomizationProvider;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.ExtensionsSchemasRegistry;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom.NodeCustomizationRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

public class XSDEditorPlugin extends AbstractUIPlugin
{
  public static final String PLUGIN_ID = "org.eclipse.wst.xsd.ui"; //$NON-NLS-1$
  public static final String EDITOR_ID = "org.eclipse.wst.xsd.ui.internal.editor.InternalXSDMultiPageEditor";  //$NON-NLS-1$
  public static final String CONST_XSD_DEFAULT_PREFIX_TEXT = "org.eclipse.wst.xmlschema.xsdDefaultPrefixText"; //$NON-NLS-1$
  public static final String CONST_PREFERED_BUILT_IN_TYPES = "org.eclipse.wst.xmlschema.preferedBuiltInTypes";  //$NON-NLS-1$
  public static final String CUSTOM_LIST_SEPARATOR = "\n"; //$NON-NLS-1$
  public static final String EXTENSIONS_SCHEMAS_EXTENSIONID = "org.eclipse.wst.xsd.ui.extensionCategories"; //$NON-NLS-1$
  private static final String DEPRECATED_EXTENSIONS_SCHEMAS_EXTENSIONID = "org.eclipse.wst.xsd.ui.ExtensionsSchemasDescription"; //$NON-NLS-1$  
  public final static String DEFAULT_TARGET_NAMESPACE = "http://www.example.org"; //$NON-NLS-1$
  public final static String XSD_CONTENT_TYPE_ID = "org.eclipse.wst.xsd.core.xsdsource"; //$NON-NLS-1$
  
	//The shared instance.
	private static XSDEditorPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
  private ExtensionsSchemasRegistry registry;  
  private NodeCustomizationRegistry nodeCustomizationRegistry;
  private XSDEditorConfiguration xsdEditorConfiguration = null;
  private static Bundle bundle;
  
  public static final String CONST_USE_SIMPLE_EDIT_MODE = PLUGIN_ID + ".useSimpleEditMode"; //$NON-NLS-1$
  public static final String CONST_SHOW_INHERITED_CONTENT = PLUGIN_ID + ".showInheritedContent"; //$NON-NLS-1$

  public static final String CONST_XSD_LANGUAGE_QUALIFY = "org.eclipse.wst.xmlschema.xsdQualify"; //$NON-NLS-1$
  public static final String CONST_DEFAULT_TARGET_NAMESPACE = "org.eclipse.wst.xmlschema.defaultTargetnamespaceText"; //$NON-NLS-1$
  public static final String CONST_SHOW_EXTERNALS = PLUGIN_ID + ".showExternals"; //$NON-NLS-1$
  public static final String CONST_XSD_IMPORT_CLEANUP = PLUGIN_ID + ".autoImportCleanup"; //$NON-NLS-1$
  public static final String CONST_XSD_AUTO_OPEN_SCHEMA_LOCATION_DIALOG = PLUGIN_ID + ".autoOpenSchemaLocationDialog"; //$NON-NLS-1$
  
  public static String DEFAULT_PAGE = "org.eclipse.wst.xsd.ui.internal.defaultPage";
  public static String DESIGN_PAGE = "org.eclipse.wst.xsd.ui.internal.designPage";
  public static String SOURCE_PAGE = "org.eclipse.wst.xsd.ui.internal.sourcePage";

	/**
	 * The constructor.
	 */
	public XSDEditorPlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		bundle = context.getBundle();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static XSDEditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = XSDEditorPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}
  
  public static String getResourceString(String key, Object o)
  {
      return getResourceString(key, new Object[] { o});
  }

  public static String getResourceString(String key, Object[] objects)
  {
      return MessageFormat.format(getResourceString(key), objects);
  }
	
  public static String getResourceString(String key, Object o1, Object o2)
  {
      return getResourceString(key, new Object[] { o1, o2});
  }

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				// resourceBundle = ResourceBundle.getBundle("org.eclipse.wst.xsd.ui.internal.editor.EditorPluginResources");
        resourceBundle = Platform.getResourceBundle(getBundle());
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.wst.xsd.ui", path); //$NON-NLS-1$
	}
	
	public static ImageDescriptor getImageDescriptor(String name, boolean getBaseURL) {
		try {
			URL installURL = getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
			String imageString = getBaseURL ? "icons/" + name : name; //$NON-NLS-1$

			URL imageURL = new URL(installURL, imageString);
			return ImageDescriptor.createFromURL(imageURL);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public Image getIcon(String name)
	{
	  try {
		ImageRegistry imageRegistry = getImageRegistry();

		if (imageRegistry.get(name) != null) {
			return imageRegistry.get(name);
		}
		else {
			URL installURL = getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
			String imageString = "icons/" + name; //$NON-NLS-1$

			URL imageURL = new URL(installURL, imageString);
			imageRegistry.put(name, ImageDescriptor.createFromURL(imageURL));
			return imageRegistry.get(name);
		}

	  } catch (Exception e) {
		return null;
	  }
	}
	
	public static XSDEditorPlugin getPlugin() {
		return plugin;
	}

	public static String getXSDString(String key) {
		return getResourceString(key);
	}

  /**
   * This gets the string resource and does one substitution.
   */
  public String getString(String key, Object s1) {
    return MessageFormat.format(Platform.getResourceBundle(getBundle()).getString(key), new Object[]{s1});
  }
  
	public static Image getXSDImage(String iconName) {
		return getDefault().getImage(iconName);
	}

	public Image getImage(String iconName) {
		ImageRegistry imageRegistry = getImageRegistry();

		if (imageRegistry.get(iconName) != null) {
			return imageRegistry.get(iconName);
		}
		else {
			imageRegistry.put(iconName, ImageDescriptor.createFromFile(getClass(), iconName));
			return imageRegistry.get(iconName);
		}
	}
	
	public URL getBaseURL() {
		return bundle.getEntry("/"); //$NON-NLS-1$
	}

	public Image getIconImage(String object) {
		try {
			return ExtendedImageRegistry.getInstance().getImage(new URL(getBaseURL() + "icons/" + object + ".gif")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (MalformedURLException exception) {

		}
		return null;
	}

  public boolean getShowInheritedContent()
  {
    return getPreferenceStore().getBoolean(CONST_SHOW_INHERITED_CONTENT);
  }
  
  protected void initializeDefaultPreferences(IPreferenceStore store)
  {
    store.setDefault(CONST_SHOW_INHERITED_CONTENT, false);
    store.setDefault(CONST_XSD_DEFAULT_PREFIX_TEXT, "xsd"); //$NON-NLS-1$
    store.setDefault(CONST_XSD_LANGUAGE_QUALIFY, false);
    store.setDefault(DEFAULT_PAGE, DESIGN_PAGE);
    store.setDefault(CONST_DEFAULT_TARGET_NAMESPACE, DEFAULT_TARGET_NAMESPACE);
    store.setDefault(CONST_SHOW_EXTERNALS, false);
    store.setDefault(CONST_XSD_IMPORT_CLEANUP, false);
    store.setDefault(CONST_XSD_AUTO_OPEN_SCHEMA_LOCATION_DIALOG, true);
    
    //Even the last item in the list must contain a trailing List separator
    store.setDefault(CONST_PREFERED_BUILT_IN_TYPES,     		
    		"boolean"+ CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
    		"date" + CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
    		"dateTime" + CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
    		"double" + CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
    		"float" + CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
    		"hexBinary" + CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
    		"int" + CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
    		"string" + CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
    		"time" + CUSTOM_LIST_SEPARATOR); //$NON-NLS-1$
  }

  public ExtensionsSchemasRegistry getExtensionsSchemasRegistry()
  {
    if (registry == null)
    {
      registry = new ExtensionsSchemasRegistry(EXTENSIONS_SCHEMAS_EXTENSIONID);
      registry.__internalSetDeprecatedExtensionId(DEPRECATED_EXTENSIONS_SCHEMAS_EXTENSIONID);
      registry.setPrefStore(XSDEditorPlugin.getPlugin().getPreferenceStore() );
    }
    return registry;
  }

 
  public XSDEditorConfiguration getXSDEditorConfiguration()
  {
    if (xsdEditorConfiguration == null)
    {
      xsdEditorConfiguration = new XSDEditorConfiguration();
    }
    return xsdEditorConfiguration;
  }

  /**
   * Get the xml schema default namespace prefix
   */
  public String getXMLSchemaPrefix()
  {
    return getPreferenceStore().getString(CONST_XSD_DEFAULT_PREFIX_TEXT);
  }

  public boolean getShowExternals()
  {
    return getPreferenceStore().getBoolean(CONST_SHOW_EXTERNALS);
  }
  
  public void setShowExternals(boolean doShow)
  {
    getPreferenceStore().setValue(CONST_SHOW_EXTERNALS, doShow);
  }
  
  public boolean getRemoveImportSetting()
  {
    return getPreferenceStore().getBoolean(CONST_XSD_IMPORT_CLEANUP);
  }

  public boolean getAutomaticallyOpenSchemaLocationDialogSetting()
  {
    return getPreferenceStore().getBoolean(CONST_XSD_AUTO_OPEN_SCHEMA_LOCATION_DIALOG);
  }
  
  /**
   * Get the xml schema default target namespace
   */
  public String getXMLSchemaTargetNamespace() {
    String targetNamespace = getPreferenceStore().getString(CONST_DEFAULT_TARGET_NAMESPACE);
    if (!targetNamespace.endsWith("/")) { //$NON-NLS-1$
      targetNamespace = targetNamespace + "/"; //$NON-NLS-1$
    }
    return targetNamespace;
  }

  /**
   * Get the xml schema language qualification
   */
  public boolean isQualifyXMLSchemaLanguage() {
    return getPreferenceStore().getBoolean(CONST_XSD_LANGUAGE_QUALIFY);
  }

  public static Shell getShell() {
    return getPlugin().getWorkbench().getActiveWorkbenchWindow().getShell();
  }
  
  public void setSourcePageAsDefault()
  {
    getPreferenceStore().setValue(DEFAULT_PAGE, SOURCE_PAGE);
  }

  public void setDesignPageAsDefault()
  {
    getPreferenceStore().setValue(DEFAULT_PAGE, DESIGN_PAGE);
  }
  
  /**
   * Method getDefaultPage.
   * 
   * @return String value of the string constant that is the default page
   *         the editor should turn to when first opened. Changes to the
   *         last visible page when the editor was closed
   */
  public String getDefaultPage() {
    return getPreferenceStore().getString(DEFAULT_PAGE);
  }

  public NodeCustomizationRegistry getNodeCustomizationRegistry()
  {
    if (nodeCustomizationRegistry == null)
    {  
      nodeCustomizationRegistry = new NodeCustomizationRegistry("foo");
    }
    return nodeCustomizationRegistry;
  }
  
  private static final String PRODUCT_CUSTOMIZATION_PROVIDER_PLUGIN_ID = "org.eclipse.wst.xsd.ui.productCustomizationProviderPluginId"; //$NON-NLS-1$
  private static final String PRODUCT_CUSTOMIZATION_PROVIDER_CLASS_NAME = "org.eclipse.wst.xsd.ui.productCustomizationProviderClassName"; //$NON-NLS-1$
  
  private static ProductCustomizationProvider productCustomizationProvider;
  private static boolean productCustomizationProviderInitialized = false;
  
  public ProductCustomizationProvider getProductCustomizationProvider()
  {
    if (!productCustomizationProviderInitialized)
    {
      productCustomizationProviderInitialized = true;
      String pluginName = getPreferenceStore().getString(PRODUCT_CUSTOMIZATION_PROVIDER_PLUGIN_ID);
      String className = getPreferenceStore().getString(PRODUCT_CUSTOMIZATION_PROVIDER_CLASS_NAME);
      if (pluginName != null && pluginName.length() > 0 &&
          className != null && className.length() > 0)
      {
        try
        {
          Bundle bundle = Platform.getBundle(pluginName);
          Class clazz = bundle.loadClass(className);
          productCustomizationProvider = (ProductCustomizationProvider)clazz.newInstance();
        }          
        catch (Exception e)
        {          
        }
      }
    }  
    return productCustomizationProvider;
  } 
}
