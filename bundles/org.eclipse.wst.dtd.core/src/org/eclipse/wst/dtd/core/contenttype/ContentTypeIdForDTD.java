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

package org.eclipse.wst.dtd.core.contenttype;

/**
 * This class, with its one field, is a convenience to provide compile-time
 * safety when referring to a contentType ID. The value of the
 * ContentTypeID_DTD field must match what is specified in the
 * org.eclipse.wst.dtd.core/plugin.xml file.
 * 
 * This class is not meant to be instantiated or subclassed.
 */

public class ContentTypeIdForDTD {
	/**
	 * The value of the ContentTypeID_DTD id field must match what is
	 * specified in org.eclipse.wst.dtd.core/plugin.xml for the DTD content
	 * type
	 * 
	 * Note: this value is intentionally set with default protected method so
	 * it will not be inlined.
	 */
	public final static String ContentTypeID_DTD = getConstantString();

	private ContentTypeIdForDTD() {
		super();
	}

	static String getConstantString() {
		return "org.eclipse.wst.dtd.core.dtdsource"; //$NON-NLS-1$
	}
}
