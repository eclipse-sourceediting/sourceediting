/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.contenttype.ContentTypeIdForCSS
 *                                           modified in order to process JSON Objects.          
 *******************************************************************************/
package org.eclipse.wst.json.core.contenttype;

/**
 * This class, with its one field, is a convience to provide compile-time safety
 * when refering to a contentType ID. The value of the contenttype id field must
 * match what is specified in plugin.xml file.
 */

public class ContentTypeIdForJSON {
	/**
	 * The value of the contenttype id field must match what is specified in
	 * plugin.xml file. Note: this value is intentially set with default
	 * protected method so it will not be inlined.
	 */
	public final static String ContentTypeID_JSON = getConstantString();

	/**
	 * Don't allow instantiation.
	 */
	private ContentTypeIdForJSON() {
		super();
	}

	static String getConstantString() {
		return "org.eclipse.wst.json.core.jsonsource"; //$NON-NLS-1$
	}

}
