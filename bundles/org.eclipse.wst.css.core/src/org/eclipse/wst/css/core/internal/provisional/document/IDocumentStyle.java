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



import org.w3c.dom.stylesheets.DocumentStyle;
import org.w3c.dom.stylesheets.StyleSheet;

/**
 * 
 */
public interface IDocumentStyle extends DocumentStyle {

	/**
	 * @return org.w3c.dom.stylesheets.StyleSheet
	 * @param newSheet
	 *            org.w3c.dom.stylesheets.StyleSheet
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	StyleSheet appendSheet(StyleSheet newSheet) throws org.w3c.dom.DOMException;

	/**
	 * @return org.w3c.dom.stylesheets.StyleSheet
	 * @param newSheet
	 *            org.w3c.dom.stylesheets.StyleSheet
	 * @param refSheet
	 *            org.w3c.dom.stylesheets.StyleSheet
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	StyleSheet insertSheetBefore(StyleSheet newSheet, StyleSheet refSheet) throws org.w3c.dom.DOMException;

	/**
	 * @return org.w3c.dom.stylesheets.StyleSheet
	 * @param oldSheet
	 *            org.w3c.dom.stylesheets.StyleSheet
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	StyleSheet removeSheet(StyleSheet oldSheet) throws org.w3c.dom.DOMException;

	/**
	 * @return org.w3c.dom.stylesheets.StyleSheet
	 * @param newSheet
	 *            org.w3c.dom.stylesheets.StyleSheet
	 * @param oldSheet
	 *            org.w3c.dom.stylesheets.StyleSheet
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	StyleSheet replaceSheet(StyleSheet newSheet, StyleSheet oldSheet) throws org.w3c.dom.DOMException;
}
