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
 * @since 1.0
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
	
	/**
	 * TODO: Add JavaDoc
	 */
	public static String XIncludeTask_0;
	/**
	 * TODO: Add JavaDoc
	 */
	public static String XIncludeTask_1;
	
	/**
	 * TODO: Add JavaDoc
	 */
	public static String XIncludeTask_2;
	
	/**
	 * TODO: Add JavaDoc
	 */
	public static String XIncludeTask_3;
	
	/**
	 * TODO: Add JavaDoc
	 */
	public static String XIncludeTask_4;
	
	/**
	 * TODO: Add JavaDoc
	 */
	public static String XIncludeTask_5;
	
	/**
	 * TODO: Add JavaDoc
	 */
	public static String XIncludeTask_6;
	
	/**
	 * TODO: Add JavaDoc
	 */
	public static String XIncludeTask_7;
	
	/**
	 * TODO: Add JavaDoc
	 */
	public static String XIncludeTask_8;
	
	/**
	 * TODO: Add JavaDoc
	 */
	public static String XIncludeTask_9;
	public static String XSLValidator_1;
	/**
	 * Stylesheet importing itself
	 */
	public static String XSLValidator_10;
	/**
	 * Template conflict (internal)
	 */
	public static String XSLValidator_11;
	/**
	 * Template conflict (included)
	 */
	public static String XSLValidator_12;
	/**
	 * Template conflict (external)
	 */
	public static String XSLValidator_13;
	/**
	 * Parameter missing name attribute
	 */
	public static String XSLValidator_14;
	/**
	 * Parameter with empty name attribute
	 */
	public static String XSLValidator_15;
	/**
	 * Duplicate parameter
	 */
	public static String XSLValidator_16;
	/**
	 * Unresolved named template
	 */
	public static String XSLValidator_18;
	/**
	 * Circular reference
	 */
	public static String XSLValidator_2;
	/**
	 * Parameter without default value
	 */
	public static String XSLValidator_20;
	/**
	 * Parameter does not exist
	 */
	public static String XSLValidator_22;
	/**
	 * href attribute is required
	 */
	public static String XSLValidator_23;
	/**
	 * Missing parameter
	 */
	public static String XSLValidator_3;
	/**
	 * Unresolved include
	 */
	public static String XSLValidator_4;
	/**
	 * Stylesheet includes itself
	 */
	public static String XSLValidator_6;
	/**
	 *  Unresolved import
	 */
	public static String XSLValidator_8;
	
	

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