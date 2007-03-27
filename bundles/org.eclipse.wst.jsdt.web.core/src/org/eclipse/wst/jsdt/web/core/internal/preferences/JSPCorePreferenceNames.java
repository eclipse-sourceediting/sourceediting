/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.preferences;

/**
 * Common preference keys used by JSP core
 */
public class JSPCorePreferenceNames {
	/**
	 * The default extension to use when none is specified in the New JSP File
	 * Wizard.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String DEFAULT_EXTENSION  = "defaultExtension"; //$NON-NLS-1$
																		 
	/**
	 * Indicates if JSP fragments should be compiled/validated. JSP fragments
	 * will be validated when true.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String VALIDATE_FRAGMENTS = "validateFragments"; //$NON-NLS-1$
																		 
	private JSPCorePreferenceNames() {
		// empty private constructor so users cannot instantiate class
	}
}
