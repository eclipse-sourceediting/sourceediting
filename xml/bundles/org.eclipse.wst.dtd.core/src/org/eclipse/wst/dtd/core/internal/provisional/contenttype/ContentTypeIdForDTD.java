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

package org.eclipse.wst.dtd.core.internal.provisional.contenttype;

/**
 * <p>
 * This class, with its one field, is a convenience to provide compile-time
 * safety when referring to the DTD contentType ID.
 * </p>
 * 
 * <p>
 * This class is not meant to be instantiated or subclassed.
 * </p>
 */

public final class ContentTypeIdForDTD {
	/**
	 * The value of the ContentTypeID_DTD id field will match what is
	 * specified in org.eclipse.wst.dtd.core/plugin.xml for the DTD content
	 * type.
	 * 
	 * This value is intentionally set through a default protected method so
	 * that it will not be inlined.
	 */
	public final static String ContentTypeID_DTD = getConstantString();

	private ContentTypeIdForDTD() {
		super();
	}

	/**
	 * @return the DTD Content Type Identifier ID as a String
	 */
	static String getConstantString() {
		return "org.eclipse.wst.dtd.core.dtdsource"; //$NON-NLS-1$
	}
}
