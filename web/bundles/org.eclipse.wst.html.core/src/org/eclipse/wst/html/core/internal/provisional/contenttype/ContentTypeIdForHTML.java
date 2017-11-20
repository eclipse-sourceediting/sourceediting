/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.html.core.internal.provisional.contenttype;

/**
 * The value of the contenttype id field must match what is specified in
 * plugin.xml file. Note: this value is intentially set with default protected
 * method so it will not be inlined.
 */

public class ContentTypeIdForHTML {
	/**
	 * The value of the contenttype id field must match what is specified in
	 * plugin.xml file. Note: this value is intentially not declared as final,
	 * so it will not be inlined.
	 */
	public final static String ContentTypeID_HTML = getConstantString();

	/**
	 * Don't allow instantiation.
	 */
	private ContentTypeIdForHTML() {
		super();
	}

	static String getConstantString() {
		return "org.eclipse.wst.html.core.htmlsource"; //$NON-NLS-1$
	}

}
