/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.provisional;

/**
 * New names for JSP 2.0 spec.
 */

public interface JSP20Namespace extends JSP12Namespace {
	/**
	 * New elements for JSP 2.0 spec.
	 */
	public static interface ElementName extends JSP12Namespace.ElementName {
		String DIRECTIVE_TAG = "jsp:directive.tag"; //$NON-NLS-1$
		String DIRECTIVE_ATTRIBUTE = "jsp:directive.attribute"; //$NON-NLS-1$
		String DIRECTIVE_VARIABLE = "jsp:directive.variable"; //$NON-NLS-1$
	}

	String ATTR_NAME_TAGDIR = "tagdir"; //$NON-NLS-1$
}
