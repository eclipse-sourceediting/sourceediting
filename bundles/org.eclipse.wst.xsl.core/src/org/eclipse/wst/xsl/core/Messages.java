/**********************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 * Jesper Steen Møller - adapted from org.eclipse.core.internal.content
 **********************************************************************/
package org.eclipse.wst.xsl.core;

import org.eclipse.osgi.util.NLS;

// Runtime plugin message catalog
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.wst.xsl.core.messages"; //$NON-NLS-1$

	// Content type manager
	public static String XSLCorePlugin_parserConfiguration;

	public static String XSLCorePlugin_badInitializationData;

	public static String XSLCorePlugin_coreError;

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}