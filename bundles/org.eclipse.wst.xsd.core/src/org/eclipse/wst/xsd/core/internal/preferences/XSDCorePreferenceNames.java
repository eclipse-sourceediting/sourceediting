/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsd.core.internal.preferences;

/**
 * Common preference keys used by XSD core
 */
public class XSDCorePreferenceNames {
	private XSDCorePreferenceNames() {
		// empty private constructor so users cannot instantiate class
	}
	/**
	 * Indicates whether or not all schema locations should be honoured
	 * during XSD validation.
	 * <p>
	 * Value is of type <code>boolean</code>.<br />
	 * Possible values: {TRUE, FALSE}
	 * </p>
	 * @deprecated
	 */
	public static final String HONOUR_ALL_SCHEMA_LOCATIONS = "honourAllSchemaLocations";//$NON-NLS-1$
	public static final String FULL_SCHEMA_CONFORMANCE = "fullSchemaConformance";//$NON-NLS-1$
}
