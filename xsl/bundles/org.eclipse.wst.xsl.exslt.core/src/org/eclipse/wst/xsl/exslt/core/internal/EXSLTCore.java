/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.exslt.core.internal;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class EXSLTCore extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.wst.xsl.exslt.core";
	
	public static final String EXSLT_MATH_NAMESPACE = "http://exslt.org/math";
	public static final String EXSLT_FUNC_NAMESPACE = "http://exslt.org/functions";
	public static final String EXSLT_COMMON_NAMESPACE = "http://exslt.org/common";
	public static final String EXSLT_DYNAMIC_NAMESPACE = "http://exslt.org/dynamic";
	public static final String EXSLT_DATE_NAMESPACE = "http://www.exslt.org/date/index.html";
	public static final String EXSLT_RANDOM_NAMESPACE = "http://exslt.org/random";
	public static final String EXSLT_REGEX_NAMESPACE = "http://exslt.org/regular-expressions";
	public static final String EXSLT_SETS_NAMESPACE = "http://exslt.org/sets";
	public static final String EXSLT_STRING_NAMESPACE = "http://exslt.org/strings";
	

	// The shared instance
	private static EXSLTCore plugin;
	
	/**
	 * The constructor
	 */
	public EXSLTCore() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static EXSLTCore getDefault() {
		return plugin;
	}

}
