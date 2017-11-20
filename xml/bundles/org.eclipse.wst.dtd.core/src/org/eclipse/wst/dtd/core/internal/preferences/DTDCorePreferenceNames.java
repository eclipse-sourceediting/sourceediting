/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.preferences;

/**
 * Common preference keys used by DTD core
 * 
 * @plannedfor 1.0
 */
public class DTDCorePreferenceNames {
	private DTDCorePreferenceNames() {
		// empty private constructor so users cannot instantiate class
	}

	/**
	 * The default extension to use when none is specified in the New File
	 * Wizard.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String DEFAULT_EXTENSION = "defaultExtension"; //$NON-NLS-1$
}
