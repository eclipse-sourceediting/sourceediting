/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Benjamin Muskalla, b.muskalla@gmx.net - [158660] character entities should have their own syntax highlighting preference      
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.style;

/**
 * Contains the symbolic name of styles used by LineStyleProvider,
 * ColorManager, and any others who may be interested
 */
public interface IStyleConstantsXSL {
	public static final String BACKGROUND = "background"; //$NON-NLS-1$
	public static final String DECL_BORDER = "declBoder";//$NON-NLS-1$

	public static final String FOREGROUND = "foreground"; //$NON-NLS-1$

	public static final String TAG_ATTRIBUTE_EQUALS = "tagAttributeEquals"; //$NON-NLS-1$
	public static final String TAG_ATTRIBUTE_NAME = "tagAttributeName";//$NON-NLS-1$
	public static final String TAG_ATTRIBUTE_VALUE = "tagAttributeValue";//$NON-NLS-1$
	public static final String TAG_BORDER = "tagBorder";//$NON-NLS-1$
	public static final String TAG_NAME = "tagName";//$NON-NLS-1$
}
