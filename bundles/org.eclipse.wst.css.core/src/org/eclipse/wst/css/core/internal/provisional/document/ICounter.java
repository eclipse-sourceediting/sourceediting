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
package org.eclipse.wst.css.core.internal.provisional.document;



import org.w3c.dom.css.Counter;

/**
 * 
 */
public interface ICounter extends ICSSNode, Counter {

	java.lang.String IDENTIFIER = "identifier"; //$NON-NLS-1$
	java.lang.String LISTSTYLE = "liststyle"; //$NON-NLS-1$
	java.lang.String SEPARATOR = "separator"; //$NON-NLS-1$

	/**
	 * @param identifier
	 *            java.lang.String
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	void setIdentifier(String identifier) throws org.w3c.dom.DOMException;

	/**
	 * @param listStyle
	 *            java.lang.String
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	void setListStyle(String listStyle) throws org.w3c.dom.DOMException;

	/**
	 * @param Separator
	 *            java.lang.String
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	void setSeparator(String separator) throws org.w3c.dom.DOMException;
}
