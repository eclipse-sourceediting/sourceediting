/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.provisional.contentmodel;


public interface CMDocType {
	/**
	 * CHTML files
	 */
	public final static String CHTML_DOC_TYPE = "CHTML";//$NON-NLS-1$
	/**
	 * HTML files
	 */
	public final static String HTML_DOC_TYPE = "HTML";//$NON-NLS-1$
	/**
	 * HTML5 files
	 */
	public final static String HTML5_DOC_TYPE = "HTML5";//$NON-NLS-1$
	/**
	 * JSP 1.1 files (currently includes 1.2 elements for backward behavioral compatibility)
	 */
	public final static String JSP11_DOC_TYPE = "JSP11";//$NON-NLS-1$
	/**
	 * JSP 1.2 files
	 */
	public final static String JSP12_DOC_TYPE = "JSP12";//$NON-NLS-1$
	/**
	 * JSP 2.0 JSP files
	 */
	public final static String JSP20_DOC_TYPE = "JSP20";//$NON-NLS-1$
	/**
	 * JSP 2.0 Tag files
	 */
	public final static String TAG20_DOC_TYPE = "JSP20.TAG";//$NON-NLS-1$
	/**
	 * JSP 2.1 Tag files
	 */
	public final static String TAG21_DOC_TYPE = "JSP21.TAG";//$NON-NLS-1$
	/**
	 * JSP 2.1 JSP files
	 */
	public final static String JSP21_DOC_TYPE = "JSP21";//$NON-NLS-1$
}
