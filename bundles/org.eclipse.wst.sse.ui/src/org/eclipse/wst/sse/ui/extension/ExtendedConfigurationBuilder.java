/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.Logger;
import org.eclipse.wst.sse.ui.internal.extension.RegistryReader;


/**
 * Simple generic ID to class to mapping. Loads a specified class defined in a
 * configuration element with the matching element name and target ID. Example
 * plugin.xml section: <extension
 * point="org.eclipse.wst.sse.ui.extendedconfiguration">
 * <contentoutlineconfiguration
 * target="org.eclipse.wst.sse.ui.dtd.StructuredTextEditorDTD"
 * class="org.eclipse.wst.sse.ui.dtd.views.contentoutline.DTDContentOutlineConfiguration"/>
 * </extension> Used in code by
 * getConfiguration("contentoutlineconfiguration",
 * "org.eclipse.wst.sse.ui.dtd.StructuredTextEditorDTD");
 */
public class ExtendedConfigurationBuilder extends RegistryReader {
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$
	private static final String ATT_TARGET = "target"; //$NON-NLS-1$
	private static Map configurationMap = null;
	private final static boolean debugTime = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.ui/extendedconfigurationbuilder/time")); //$NON-NLS-1$  //$NON-NLS-2$
	private static final String EP_EXTENDEDCONFIGURATION = "extendedconfiguration"; //$NON-NLS-1$
	private static ExtendedConfigurationBuilder instance = null;

	/**
	 * Creates an extension. If the extension plugin has not been loaded a
	 * busy cursor will be activated during the duration of the load.
	 * 
	 * @param element
	 *            the config element defining the extension
	 * @param classAttribute
	 *            the name of the attribute carrying the class
	 * @returns the extension object if successful. If an error occurs when
	 *          createing executable extension, the exception is logged, and
	 *          null returned.
	 */
	public static Object createExtension(final IConfigurationElement element, final String classAttribute, final String targetID) {
		final Object[] result = new Object[1];
		// If plugin has been loaded create extension.
		// Otherwise, show busy cursor then create extension.
		IPluginDescriptor plugin = element.getDeclaringExtension().getDeclaringPluginDescriptor();
		if (plugin.isPluginActivated()) {
			try {
				result[0] = element.createExecutableExtension(classAttribute);
			} catch (Exception e) {
				// catch and log ANY exception from extension point
				Logger.logException("error loading class " + classAttribute + " for " + targetID, e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			BusyIndicator.showWhile(null, new Runnable() {
				public void run() {
					try {
						result[0] = element.createExecutableExtension(classAttribute);
					} catch (Exception e) {
						// catch and log ANY exception from extension point
						Logger.logException("error loading class " + classAttribute + " for " + targetID, e); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			});
		}
		return result[0];
	}

	/**
	 * @return Returns the instance.
	 */
	public synchronized static ExtendedConfigurationBuilder getInstance() {
		if (instance == null)
			instance = new ExtendedConfigurationBuilder();
		return instance;
	}

	long time0 = 0;

	private ExtendedConfigurationBuilder() {
		super();
	}

	private Object createConfiguration(List configurations, String extensionType, String targetID) {
		if (configurations == null)
			return null;
		Object result = null;
		for (int i = 0; i < configurations.size(); i++) {
			IConfigurationElement element = (IConfigurationElement) configurations.get(i);
			if (element.getName().equals(extensionType) && element.getAttribute(ATT_TARGET).equals(targetID)) {
				result = createExtension(element, ATT_CLASS, targetID);
			}
			if (result != null) {
				if (result instanceof IExtendedConfiguration) {
					((IExtendedConfiguration) result).setDeclaringID(targetID);
				}
				return result;
			}
		}
		return result;
	}

	private IConfigurationElement findConfigurationElement(List configurations, String extensionType, String targetID) {
		if (configurations == null)
			return null;
		IConfigurationElement result = null;
		for (int i = 0; i < configurations.size(); i++) {
			IConfigurationElement element = (IConfigurationElement) configurations.get(i);
			if (element.getName().equals(extensionType) && element.getAttribute(ATT_TARGET).equals(targetID)) {
				result = element;
			}
		}
		return result;
	}

	public Object getConfiguration(String extensionType, String targetID) {
		if (targetID == null || targetID.length() == 0)
			return null;
		if (debugTime) {
			time0 = System.currentTimeMillis();
		}
		if (configurationMap == null) {
			configurationMap = new HashMap(0);
			synchronized (configurationMap) {
				readRegistry(Platform.getPluginRegistry(), EditorPlugin.ID, EP_EXTENDEDCONFIGURATION);
				if (debugTime) {
					System.out.println(getClass().getName() + "#readRegistry():  " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
					time0 = System.currentTimeMillis();
				}
			}
		}
		List configurations = (List) configurationMap.get(extensionType);
		Object o = createConfiguration(configurations, extensionType, targetID);
		if (debugTime) {
			if (o != null)
				System.out.println(getClass().getName() + "#getConfiguration(" + extensionType + ", " + targetID + "): configuration loaded in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			else
				System.out.println(getClass().getName() + "#getConfiguration(" + extensionType + ", " + targetID + "): ran in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		return o;
	}

	public IConfigurationElement getConfigurationElement(String extensionType, String targetID) {
		if (targetID == null || targetID.length() == 0)
			return null;
		if (debugTime) {
			time0 = System.currentTimeMillis();
		}
		if (configurationMap == null) {
			configurationMap = new HashMap(0);
			synchronized (configurationMap) {
				readRegistry(Platform.getPluginRegistry(), EditorPlugin.ID, EP_EXTENDEDCONFIGURATION);
				if (debugTime) {
					System.out.println(getClass().getName() + "#readRegistry():  " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
					time0 = System.currentTimeMillis();
				}
			}
		}
		List configurations = (List) configurationMap.get(extensionType);
		IConfigurationElement element = findConfigurationElement(configurations, extensionType, targetID);
		if (debugTime) {
			if (element != null)
				System.out.println(getClass().getName() + "#getConfigurationElement(" + extensionType + ", " + targetID + "): configuration loaded in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			else
				System.out.println(getClass().getName() + "#getConfigurationElement(" + extensionType + ", " + targetID + "): ran in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		return element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.internal.extension.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
	 */
	protected boolean readElement(IConfigurationElement element) {
		String name = element.getName();
		List configurations = (List) configurationMap.get(name);
		if (configurations == null) {
			configurations = new ArrayList(1);
			configurationMap.put(name, configurations);
		}
		configurations.add(element);
		return true;
	}
}
