/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.provisional;



/**
 * XML Namespace constants
 * 
 * @plannedfor 1.0
 */
public interface IXMLCharEntity {
	static final String AMP_NAME = "amp";//$NON-NLS-1$
	static final String AMP_REF = "&amp;";//$NON-NLS-1$
	static final String AMP_VALUE = "&";//$NON-NLS-1$
	static final String APOS_NAME = "apos";//$NON-NLS-1$
	static final String APOS_REF = "&apos;";//$NON-NLS-1$
	static final String APOS_VALUE = "'";//$NON-NLS-1$
	static final String GT_NAME = "gt";//$NON-NLS-1$
	static final String GT_REF = "&gt;";//$NON-NLS-1$
	static final String GT_VALUE = ">";//$NON-NLS-1$

	static final String LT_NAME = "lt";//$NON-NLS-1$
	static final String LT_REF = "&lt;";//$NON-NLS-1$
	static final String LT_VALUE = "<";//$NON-NLS-1$
	static final String QUOT_NAME = "quot";//$NON-NLS-1$
	static final String QUOT_REF = "&quot;";//$NON-NLS-1$
	static final String QUOT_VALUE = "\"";//$NON-NLS-1$
}
