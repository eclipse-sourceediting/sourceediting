/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.core.internal.provisional.contenttype;

/**
 * This class, with its one field, is a convience to provide compile-time
 * safety when refering to a contentType ID. The value of the contenttype id
 * field must match what is specified in plugin.xml file.
 */

public class ContentTypeIdForJSP {
	/**
	 * The value of the contenttype id field must match what is specified in
	 * plugin.xml file. Note: this value is intentially set with default
	 * protected method so it will not be inlined.
	 */
	public final static String ContentTypeID_JSP = getConstantString();
	/**
	 * The value of the contenttype id field must match what is specified in
	 * plugin.xml file. Note: this value is intentially set with default
	 * protected method so it will not be inlined.
	 */
	public final static String ContentTypeID_JSPFRAGMENT = getFragmentConstantString();

	/**
	 * The value of the contenttype id field must match what is specified in
	 * plugin.xml file. Note: this value is intentially set with default
	 * protected method so it will not be inlined.
	 */
	public final static String ContentTypeID_JSPTAG = getTagConstantString();

	/**
	 * Don't allow instantiation.
	 */
	private ContentTypeIdForJSP() {
		super();
	}

	static String getConstantString() {
		return "org.eclipse.jst.jsp.core.jspsource"; //$NON-NLS-1$
	}
	
	static String getFragmentConstantString() {
		return "org.eclipse.jst.jsp.core.jspfragmentsource"; //$NON-NLS-1$
	}
	
	static String getTagConstantString() {
		return "org.eclipse.jst.jsp.core.tagsource"; //$NON-NLS-1$
	}

}
