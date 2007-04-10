/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional;

import java.util.List;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;

public interface TLDDocument extends CMDocument {

	String CM_KIND = "Content Model Kind"; //$NON-NLS-1$
	String JSP_TLD = "JSP Tag Library Descriptor"; //$NON-NLS-1$

	/**
	 * @see  JSP 2.0
	 * 
	 * @return
	 */
	String getBaseLocation();
	
	/**
	 * @return String - The contents of the "description" element of a JSP 1.2 tag library descriptor; a simple string describing the "use" of this taglib, should be user discernable.
	 * @see  JSP 1.2
	 */
	String getDescription();

	/**
	 * @return String - The contents of the "display-name" element of a JSP 1.2 tag library descriptor; it is a short name that is intended to be displayed by tools
	 * @see  JSP 1.2
	 */
	String getDisplayName();

	/**
	 * @return List - A list of extension elements describing the tag library
	 * @see  JSP 2.0
	 */
	List getExtensions();
	
	/**
	 * @return List - A list of TLDFunctions describing the declared functions
	 * @see  JSP 2.0
	 */
	List getFunctions();
	
	/**
	 * @return String - The contents of the "info" element of a JSP 1.1 tag library descriptor; a simple string describing the "use" of this taglib, should be user discernable.
	 * @see  JSP 1.1
	 */
	String getInfo();

	/**
	 * @return String - The version of JSP the tag library depends upon
	 * @see  JSP 1.1
	 */
	String getJspversion();

	/**
	 * @return String - The contents of the "large-icon" element of a JSP 1.2 tag library descriptor; optional large-icon that can be used by tools
	 * @see  JSP 1.2
	 */
	String getLargeIcon();

	/**
	 * @see  JSP 1.2
	 * @return List - a List of TLDListeners
	 */
	List getListeners();

	/**
	 * @return String - A simple default short name that could be used by a JSP authoring tool to create names with a mnemonic value; for example, it may be used as the preferred prefix value in taglib directives
	 * @see  JSP 1.1
	 */
	String getShortname();

	/**
	 * @return String - The contents of the "small-icon" element of a JSP 1.2 tag library descriptor; optional small-icon that can be used by tools
	 * @see  JSP 1.2
	 */
	String getSmallIcon();

	/**
	 * @return String - The version of the tag library (it's implementation)
	 * @see  JSP 1.1
	 */
	String getTlibversion();

	/**
	 * @return String - the URI declared within the descriptor
	 * @see  JSP 1.1
	 */
	String getUri();

	/**
	 * @see  JSP 1.2
	 */
	TLDValidator getValidator();
}
