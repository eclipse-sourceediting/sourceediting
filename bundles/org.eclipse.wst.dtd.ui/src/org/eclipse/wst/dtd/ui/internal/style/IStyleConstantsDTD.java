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
package org.eclipse.wst.dtd.ui.internal.style;

/**
 * Contains the symbolic name of styles used by LineStyleProvider,
 * ColorManager, and any others who may be interested
 */
public interface IStyleConstantsDTD {

	public static final String DTD_COMMENT = "dtdComment"; //$NON-NLS-1$

	// Data are variables
	public static final String DTD_DATA = "dtdData"; //$NON-NLS-1$
	public static final String DTD_DEFAULT = "dtdDefault"; //$NON-NLS-1$

	// keywords are constants like IMPLIED or PCDATA
	public static final String DTD_KEYWORD = "dtdKeyword"; //$NON-NLS-1$

	// strings are anything in quotes
	public static final String DTD_STRING = "dtdString"; //$NON-NLS-1$

	// All the remaining symbols
	public static final String DTD_SYMBOL = "dtdSymbol"; //$NON-NLS-1$

	// tags are '<', '!', or '>'
	public static final String DTD_TAG = "dtdTag"; //$NON-NLS-1$

	// tagnames are like ELEMENT, ATTLIST, etc.
	public static final String DTD_TAGNAME = "dtdTagName"; //$NON-NLS-1$
}
