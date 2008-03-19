/**********************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 * Jesper Steen M�ller - adapted from org.eclipse.core.internal.content
 **********************************************************************/
package org.eclipse.wst.xsl.core.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author Jesper Steen
 *
 */
// Runtime plugin message catalog

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.wst.xsl.core.internal.messages"; //$NON-NLS-1$

	/**
	 * TODO: Add JavaDoc
	 */
	// Content type manager
	public static String XSLCorePlugin_parserConfiguration;

	/**
	 * TODO: Add JavaDoc
	 */
	public static String XSLCorePlugin_badInitializationData;

	/**
	 * TODO: Add JavaDoc
	 */
	public static String XSLCorePlugin_coreError;

	static {
		// load message values from bundle file
		reloadMessages();
	}

	/**
	 * TODO: Add JavaDoc
	 */
	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}