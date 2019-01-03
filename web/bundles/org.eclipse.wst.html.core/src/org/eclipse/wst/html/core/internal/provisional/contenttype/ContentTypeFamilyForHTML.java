/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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

public class ContentTypeFamilyForHTML {
	/**
	 * The value of the contenttype id field must match what is specified in
	 * plugin.xml file. Note: this value is intentially not declared as final,
	 * so it will not be inlined.
	 */
	public final static String HTML_FAMILY = getConstantString();

	/**
	 * Don't allow instantiation.
	 */
	private ContentTypeFamilyForHTML() {
		super();
	}

	static String getConstantString() {
		return "org.eclipse.wst.html.core.contentfamily.html"; //$NON-NLS-1$
	}

}
