/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by CSS Core
 * 
 * @plannedfor 1.0
 */
public class CSSCoreMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.css.core.internal.CSSCorePluginResources";//$NON-NLS-1$

	public static String You_cannot_use_CSSStyleShe_UI_;
	public static String _1concat_ERROR_;
	public static String _2concat_ERROR_;
	public static String _3concat_ERROR_;
	public static String _4concat_ERROR_;
	public static String _25concat_ERROR_;
	public static String _26concat_ERROR_;
	public static String _27concat_ERROR_;
	public static String _28concat_ERROR_;
	public static String _5concat_ERROR_;
	public static String _6concat_ERROR_;
	public static String _7concat_ERROR_;
	public static String _8concat_ERROR_;
	public static String CSSContentPropertiesManager_Updating;
	public static String CSSContentPropertiesManager_Problems_Updating;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, CSSCoreMessages.class);
	}
}
