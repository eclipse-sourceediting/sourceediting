/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.project.facet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.web.internal.WSTWebPlugin;

public class ProductManager {

	/**
	 * Default values for WTP level product
	 */
	private static final String APPLICATION_CONTENT_FOLDER = "EarContent"; //$NON-NLS-1$
	private static final String WEB_CONTENT_FOLDER = "WebContent"; //$NON-NLS-1$
	private static final String EJB_CONTENT_FOLDER = "ejbModule"; //$NON-NLS-1$
	private static final String APP_CLIENT_CONTENT_FOLDER = "appClientModule"; //$NON-NLS-1$
	private static final String JCA_CONTENT_FOLDER = "connectorModule"; //$NON-NLS-1$
	private static final String DEFAULT_SOURCE_FOLDER = "src"; //$NON-NLS-1$
	private static final String ADD_TO_EAR_BY_DEFAULT = "false"; //$NON-NLS-1$
	private static final String ADD_TO_EAR_RUNTIME_EXCEPTIONS = ""; //$NON-NLS-1$
	private static final String OUTPUT_FOLDER = "build/classes"; //$NON-NLS-1$
	private static final String USE_SINGLE_ROOT_STRUCTURE = "false"; //$NON-NLS-1$
	private static final String VIEWER_SYNC_FOR_WEBSERVICES = "true"; //$NON-NLS-1$
	private static final String ID_PERSPECTIVE_HIERARCHY_VIEW = "org.eclipse.ui.navigator.ProjectExplorer"; //$NON-NLS-1$
	private static final String SHOW_JAVA_EE_MODULE_DEPENDENCY_PAGE = "true"; //$NON-NLS-1$
	private static final String FINAL_PERSPECTIVE = "org.eclipse.jst.j2ee.J2EEPerspective"; //$NON-NLS-1$
	private static final String FINAL_WEB_PERSPECTIVE = "org.eclipse.wst.web.ui.webDevPerspective"; //$NON-NLS-1$
	private static final String DYNAMIC_WEB_GENERATE_DD = "true"; //$NON-NLS-1$
	private static final char RUNTIME_SEPARATOR = ':';
	private static final String[] DEFAULT_RUNTIME_KEYS = 
							new String[]{IProductConstants.DEFAULT_RUNTIME_1,
										IProductConstants.DEFAULT_RUNTIME_2,
										IProductConstants.DEFAULT_RUNTIME_3,
										IProductConstants.DEFAULT_RUNTIME_4};
	
	/**
	 * Return the value for the associated key from the Platform Product registry or return the
	 * WTP default for the J2EE cases.
	 * 
	 * @param key
	 * @return String value of product's property
	 */
	public static String getProperty(String key) {
		if (key == null)
			return null;
		String value = null;
		if (Platform.getProduct()!=null)
			value = Platform.getProduct().getProperty(key);
		if (value == null)
		{
			value = Platform.getPreferencesService().getString(WSTWebPlugin.PLUGIN_ID, key, null, null);
		}
		if (value == null) {
			if (key.equals(IProductConstants.APPLICATION_CONTENT_FOLDER))
				return APPLICATION_CONTENT_FOLDER;
			else if (key.equals(IProductConstants.WEB_CONTENT_FOLDER))
				return WEB_CONTENT_FOLDER;
			else if (key.equals(IProductConstants.EJB_CONTENT_FOLDER))
				return EJB_CONTENT_FOLDER;
			else if (key.equals(IProductConstants.APP_CLIENT_CONTENT_FOLDER))
				return APP_CLIENT_CONTENT_FOLDER;
			else if (key.equals(IProductConstants.JCA_CONTENT_FOLDER))
				return JCA_CONTENT_FOLDER;
			else if (key.equals(IProductConstants.DEFAULT_SOURCE_FOLDER))
				return DEFAULT_SOURCE_FOLDER;
			else if (key.equals(IProductConstants.ADD_TO_EAR_BY_DEFAULT))
				return ADD_TO_EAR_BY_DEFAULT;
			else if (key.equals(IProductConstants.ADD_TO_EAR_RUNTIME_EXCEPTIONS))
				return ADD_TO_EAR_RUNTIME_EXCEPTIONS;
			else if (key.equals(IProductConstants.USE_SINGLE_ROOT_STRUCTURE))
				return USE_SINGLE_ROOT_STRUCTURE;
			else if (key.equals(IProductConstants.VIEWER_SYNC_FOR_WEBSERVICES))
				return VIEWER_SYNC_FOR_WEBSERVICES;
			else if (key.equals(IProductConstants.OUTPUT_FOLDER))
				return OUTPUT_FOLDER;
			else if (key.equals(IProductConstants.ID_PERSPECTIVE_HIERARCHY_VIEW))
				return ID_PERSPECTIVE_HIERARCHY_VIEW;
			else if (key.equals(IProductConstants.FINAL_PERSPECTIVE_APPCLIENT))
				return FINAL_PERSPECTIVE;
			else if (key.equals(IProductConstants.FINAL_PERSPECTIVE_EAR))
				return FINAL_PERSPECTIVE;
			else if (key.equals(IProductConstants.FINAL_PERSPECTIVE_EJB))
				return FINAL_PERSPECTIVE;
			else if (key.equals(IProductConstants.FINAL_PERSPECTIVE_JCA))
				return FINAL_PERSPECTIVE;
			else if (key.equals(IProductConstants.FINAL_PERSPECTIVE_STATICWEB))
				return FINAL_WEB_PERSPECTIVE;
			else if (key.equals(IProductConstants.FINAL_PERSPECTIVE_UTILITY))
				return FINAL_PERSPECTIVE;
			else if (key.equals(IProductConstants.FINAL_PERSPECTIVE_WEB))
				return FINAL_PERSPECTIVE;
			else if (key.equals(IProductConstants.SHOW_JAVA_EE_MODULE_DEPENDENCY_PAGE))
				return SHOW_JAVA_EE_MODULE_DEPENDENCY_PAGE;
			else if (key.equals(IProductConstants.DYNAMIC_WEB_GENERATE_DD))
				return DYNAMIC_WEB_GENERATE_DD;
			else if (key.equals(IProductConstants.EJB_INTERFACE_PACKAGE_SUFFIX))
				return ""; //$NON-NLS-1$
		}
		return value;
	}
	
	public static boolean shouldAddToEARByDefault() {
		String value = getProperty(IProductConstants.ADD_TO_EAR_BY_DEFAULT);
		return Boolean.valueOf(value).booleanValue();
	}
	
	public static boolean shouldUseSingleRootStructure() {
		String value = getProperty(IProductConstants.USE_SINGLE_ROOT_STRUCTURE);
		return Boolean.valueOf(value).booleanValue();
	}
	
	public static boolean shouldUseViewerSyncForWebservices() {
		String value = getProperty(IProductConstants.VIEWER_SYNC_FOR_WEBSERVICES);
		return Boolean.valueOf(value).booleanValue();
	}

	public static List/*<IRuntime>*/ getDefaultRuntimes() {
		List theRuntimes = null;
		Set runtimes = RuntimeManager.getRuntimes();
		if (!runtimes.isEmpty()) {
			IRuntime defaultRuntime = null;
			//	First check if defaults are defined
			for (int i = 0; i < DEFAULT_RUNTIME_KEYS.length; i++) {
				defaultRuntime = getMatchingRuntime(DEFAULT_RUNTIME_KEYS[i], runtimes);
				if (defaultRuntime != null) {
					if (theRuntimes == null) {
						theRuntimes = new ArrayList(DEFAULT_RUNTIME_KEYS.length);
					}
					theRuntimes.add(defaultRuntime);
				}
			}
		}
		if (theRuntimes == null) {
			theRuntimes = Collections.EMPTY_LIST;
		}
		return theRuntimes;
	}
	
	private static IRuntime getMatchingRuntime(String defaultProductRuntimeProperty, Set runtimes) {
		String defaultProductRuntimeKey = getProperty(defaultProductRuntimeProperty);
		if (defaultProductRuntimeKey == null || defaultProductRuntimeKey.length() == 0) {
			return null;
		}
		//The defaultProductRuntimeKey needs to be in the following format
		//<facet runtime id>:<facet version>.
		int seperatorIndex = defaultProductRuntimeKey.indexOf(RUNTIME_SEPARATOR);
		if (seperatorIndex < 0 && seperatorIndex < defaultProductRuntimeKey.length()) {
			//Consider throwing an exception here.
			WSTWebPlugin.logError("Invalid default product runtime id.  It should follow the format <facet runtime id>:<facet version>.  Id processed: " + defaultProductRuntimeKey); //$NON-NLS-1$
			return null;
		}
		String defaultRuntimeID = defaultProductRuntimeKey.substring(0, seperatorIndex);
		String defaultFacetVersion = defaultProductRuntimeKey.substring(seperatorIndex + 1);
		for (Iterator runtimeIt = runtimes.iterator(); runtimeIt.hasNext();) {
			IRuntime runtime = (IRuntime) runtimeIt.next();
			List runtimeComps = runtime.getRuntimeComponents();
			if (!runtimeComps.isEmpty()) {
				for (Iterator compsIter = runtimeComps.iterator(); compsIter.hasNext();) {
					IRuntimeComponent runtimeComp = (IRuntimeComponent) compsIter.next();
					if (defaultRuntimeID.equals(runtimeComp.getRuntimeComponentType().getId()) &&
						(defaultFacetVersion.equals(runtimeComp.getRuntimeComponentVersion().getVersionString()))) {
							return runtime;
					}
				}
			}
		}
		//No matches found.
		return null;
	}
}
