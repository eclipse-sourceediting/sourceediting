/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.xml.core.contenttype;

/**
 * This class, with its one field, is a convience to provide compile-time
 * safety when refering to a contentType ID. The value of the contenttype id
 * field must match what is specified in plugin.xml file.
 */

public class ContentTypeIdForXML {
	/**
	 * This content type is actually supplied by base Eclipse. Its given here
	 * just as documentation for WTP based clients. Typically, clients should
	 * use the values/constants supplied by base Eclipse.
	 */
	public final static String ContentTypeID_XML = "org.eclipse.core.runtime.xml"; //$NON-NLS-1$
	/**
	 * This value is public only for testing and special infrastructure The
	 * constant nor is value should not be referenced by clients.
	 * 
	 * The value of the contenttype id field must match what is specified in
	 * plugin.xml file. Note: this value is intentially not declared as final,
	 * so it will not be inlined.
	 */
	public static String ContentTypeID_SSEXML = "org.eclipse.wst.xml.core.xmlsource"; //$NON-NLS-1$

}
