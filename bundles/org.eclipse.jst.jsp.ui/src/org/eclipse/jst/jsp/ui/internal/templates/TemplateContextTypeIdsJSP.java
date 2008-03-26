/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.templates;


public class TemplateContextTypeIdsJSP {

	public static final String ALL = getAll();

	public static final String ATTRIBUTE = getAttribute();

	public static final String ATTRIBUTE_VALUE = getAttributeValue();

	public static final String NEW = getNew();

	public static final String TAG = getTag();

	public static final String NEW_TAG = "tag_new";

	private static String getAll() {
		return getPrefix() + "_all"; //$NON-NLS-1$
	}

	private static String getAttribute() {
		return getPrefix() + "_attribute"; //$NON-NLS-1$
	}

	private static String getAttributeValue() {
		return getPrefix() + "_attribute_value"; //$NON-NLS-1$
	}

	private static String getNew() {
		return getPrefix() + "_new"; //$NON-NLS-1$
	}

	private static String getPrefix() {
		return "jsp"; //$NON-NLS-1$
	}

	private static String getTag() {
		return getPrefix() + "_tag"; //$NON-NLS-1$
	}
}
