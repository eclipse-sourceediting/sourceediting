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
package org.eclipse.wst.xsd.ui.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;


public class XSDEditorPlugin extends AbstractUIPlugin {
	public final static String PLUGIN_ID = "org.eclipse.wst.xsd.ui";
	public final static String XSD_EDITOR_ID = "org.eclipse.wst.xsd.ui.XSDEditor";

	public final static String DEFAULT_TARGET_NAMESPACE = "http://www.example.org";

	protected static XSDEditorPlugin plugin;

	// protected XMLSchemaPackage xmlschemaPackage;
	// KCPort private static MsgLogger myMsgLogger;

	public XSDEditorPlugin() {
		super();
		plugin = this;
		// KCPort myMsgLogger = getMsgLogger();
		// myMsgLogger.write(Level.CONFIG, new BuildInfo());
		// myMsgLogger.write(Level.CONFIG, BuildInfo.getWSABuildLevel());
	}

	/**
	 * @deprecated use StructuredModelManager.getModelManager(); instead.
	 */
	public static IModelManager getModelManager() {
		return StructuredModelManager.getModelManager();
	}


	/**
	 * Get the Install URL
	 */
	public static URL getInstallURL() {
		try
	    {
	      return FileLocator.resolve(plugin.getBundle().getEntry("/"));
	    }
	    catch (IOException e)
	    {
	      return null;
	    }
	}

	/**
	 * Return the plugin physical directory location
	 */
	public static IPath getPluginLocation() {
		try {
			IPath installPath = new Path(getInstallURL().toExternalForm()).removeTrailingSeparator();
			String installStr = FileLocator.toFileURL(new URL(installPath.toString())).getFile();
			return new Path(installStr);
		}
		catch (IOException e) {

		}
		return null;
	}

	/**
	 * Get the metadata directory for this plugin
	 */
	public static String getMetaDataDirectory() {
		return getPlugin().getStateLocation().toOSString();
	}

	/**
	 * Get the one xmlschema package.
	 */
	// public XMLSchemaPackage getXMLSchemaPackage()
	// {
	// return xmlschemaPackage;
	// }
	// /**
	// * Get the one xmlschema factory.
	// */
	// public XMLSchemaFactory getXMLSchemaFactory()
	// {
	// return (XMLSchemaFactory)xmlschemaPackage.getEFactoryInstance();
	// }
	/**
	 * Get the singleton instance.
	 */
	public static XSDEditorPlugin getPlugin() {
		return plugin;
	}

	public static Image getXSDImage(String iconName) {
		return getPlugin().getImage(iconName);
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

	public static String getXSDString(String key) {
		return Platform.getResourceBundle(plugin.getBundle()).getString(key);
	}

	/**
	 * This gets the string resource and does one substitution.
	 */
	public String getString(String key, Object s1) {
		return MessageFormat.format(Platform.getResourceBundle(getBundle()).getString(key), new Object[]{s1});
	}

	// public IWorkspace getWorkspace()
	// {
	// return ResourcesPlugin.getWorkspace();
	// }

	public static Shell getShell() {
		return getPlugin().getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	/**
	 * Get the xml schema default namespace prefix
	 */
	public String getXMLSchemaPrefix() {
		return getPreferenceStore().getString(CONST_XSD_DEFAULT_PREFIX_TEXT);
	}

	/**
	 * Get the xml schema default target namespace
	 */
	public String getXMLSchemaTargetNamespace() {
		String targetNamespace = getPreferenceStore().getString(CONST_DEFAULT_TARGET_NAMESPACE);
		if (!targetNamespace.endsWith("/")) {
			targetNamespace = targetNamespace + "/";
		}
		return targetNamespace;
	}

	/**
	 * Get the xml schema language qualification
	 */
	public boolean isQualifyXMLSchemaLanguage() {
		return getPreferenceStore().getBoolean(CONST_XSD_LANGUAGE_QUALIFY);
	}

	/**
	 * Method isCombinedDesignAndSourceView.
	 * 
	 * @return boolean if the editor should have a single page that is a
	 *         combined design and source page
	 */
	public boolean isCombinedDesignAndSourceView() {
		return COMBINED_LAYOUT.equals(getPreferenceStore().getString(EDITOR_LAYOUT));
	}

	public int getDesignLayout() {
		if (TOP_LAYOUT.equals(getPreferenceStore().getString(DESIGN_LAYOUT))) {
			return SWT.VERTICAL;
		}
		else if (BOTTOM_LAYOUT.equals(getPreferenceStore().getString(DESIGN_LAYOUT))) {
			return SWT.VERTICAL;
		}
		else if (LEFT_LAYOUT.equals(getPreferenceStore().getString(DESIGN_LAYOUT))) {
			return SWT.HORIZONTAL;
		}
		else if (RIGHT_LAYOUT.equals(getPreferenceStore().getString(DESIGN_LAYOUT))) {
			return SWT.HORIZONTAL;
		}
		return SWT.HORIZONTAL;
	}

	public String getDesignLayoutPosition() {
		if (TOP_LAYOUT.equals(getPreferenceStore().getString(DESIGN_LAYOUT))) {
			return TOP_LAYOUT;
		}
		else if (BOTTOM_LAYOUT.equals(getPreferenceStore().getString(DESIGN_LAYOUT))) {
			return BOTTOM_LAYOUT;
		}
		else if (LEFT_LAYOUT.equals(getPreferenceStore().getString(DESIGN_LAYOUT))) {
			return LEFT_LAYOUT;
		}
		else if (RIGHT_LAYOUT.equals(getPreferenceStore().getString(DESIGN_LAYOUT))) {
			return RIGHT_LAYOUT;
		}
		return RIGHT_LAYOUT;
	}

	/*---------------------------------------------------------------------------*/
	/* the following methods are impls for the IPluginHelper interface */
	/*---------------------------------------------------------------------------*/
	// public void setMsgLoggerConfig(Hashtable msgLoggerConfig)
	// {
	// getMsgLogger().setMsgLoggerConfig(msgLoggerConfig);
	// }
	//
	// public Hashtable getMsgLoggerConfig(Plugin plugin)
	// {
	// return (new PluginHelperImpl().getMsgLoggerConfig(plugin));
	// }
	//
	// public Hashtable getMsgLoggerConfig()
	// {
	// return (getMsgLoggerConfig(this));
	// }
	//
	// /**
	// * XSDEditor and XSDModel use the same logger. See plugin.xml
	// */
	// public MsgLogger getMsgLogger()
	// {
	// if (myMsgLogger == null)
	// {
	// myMsgLogger = (MsgLogger) MsgLogger.getFactory().getLogger(new
	// PluginHelperImpl().getMsgLoggerName(this), this);
	// }
	// return (myMsgLogger);
	// }
	public static final String CONST_XSD_DEFAULT_PREFIX_TEXT = "org.eclipse.wst.xmlschema.xsdDefaultPrefixText";
	public static final String CONST_XSD_LANGUAGE_QUALIFY = "org.eclipse.wst.xmlschema.xsdQualify";
	public static final String CONST_DEFAULT_TARGET_NAMESPACE = "org.eclipse.wst.xmlschema.defaultTargetnamespaceText";

	// Preference to store which page should come up as the default page in
	// the editor. This setting is based
	// on the page that was left showing the last time the editor was closed.
	public static String DEFAULT_PAGE = "org.eclipse.wst.xsd.ui.internal.defaultPage";
	public static String DESIGN_PAGE = "org.eclipse.wst.xsd.ui.internal.designPage";
	public static String SOURCE_PAGE = "org.eclipse.wst.xsd.ui.internal.sourcePage";
	public static String GRAPH_PAGE = "org.eclipse.wst.xsd.ui.internal.graphPage";

	public static String EDITOR_LAYOUT = "org.eclipse.wst.xsd.ui.internal.editorlayout";
	public static String COMBINED_LAYOUT = "org.eclipse.wst.xsd.ui.internal.combined";
	public static String SEPARATE_LAYOUT = "org.eclipse.wst.xsd.ui.internal.separate";

	public static String DESIGN_LAYOUT = "org.eclipse.wst.xsd.ui.internal.designlayout";
	public static String TOP_LAYOUT = "org.eclipse.wst.xsd.ui.internal.top";
	public static String BOTTOM_LAYOUT = "org.eclipse.wst.xsd.ui.internal.bottom";
	public static String LEFT_LAYOUT = "org.eclipse.wst.xsd.ui.internal.left";
	public static String RIGHT_LAYOUT = "org.eclipse.wst.xsd.ui.internal.right";

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeDefaultPreferences(IPreferenceStore)
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		super.initializeDefaultPreferences(store);

		store.setDefault(CONST_XSD_DEFAULT_PREFIX_TEXT, "xsd");
		store.setDefault(CONST_XSD_LANGUAGE_QUALIFY, false);

		store.setDefault(DEFAULT_PAGE, DESIGN_PAGE);
		store.setDefault(EDITOR_LAYOUT, COMBINED_LAYOUT);
		store.setDefault(DESIGN_LAYOUT, RIGHT_LAYOUT);

		store.setDefault(XSDEditorPlugin.CONST_DEFAULT_TARGET_NAMESPACE, DEFAULT_TARGET_NAMESPACE);
	}

	public void setDefaultPage(String page) {
		getPreferenceStore().setValue(DEFAULT_PAGE, page);
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

	protected URL baseURL;

	public URL getBaseURL() {
		try
	    {
	      return FileLocator.resolve(getBundle().getEntry("/"));
	    }
	    catch (IOException e)
	    {
	      return null;
	    }
	}

	public Image getIconImage(String object) {
		try {
			return ExtendedImageRegistry.getInstance().getImage(new URL(getBaseURL() + "icons/" + object + ".gif"));
		}
		catch (MalformedURLException exception) {
			System.out.println("Failed to load image for '" + object + "'");
		}
		return null;
	}
}
