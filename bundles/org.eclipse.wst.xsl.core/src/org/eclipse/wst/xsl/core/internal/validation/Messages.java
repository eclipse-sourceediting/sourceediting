/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.validation;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for the validator.
 * 
 * @author dcarver
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.eclipse.wst.xsl.core.internal.validation.messages"; //$NON-NLS-1$
	/**
	 * Invalid xpath.
	 */
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

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
