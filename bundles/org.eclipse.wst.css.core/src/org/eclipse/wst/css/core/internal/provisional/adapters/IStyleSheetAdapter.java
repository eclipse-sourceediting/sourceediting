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
package org.eclipse.wst.css.core.internal.provisional.adapters;



import org.w3c.dom.Element;
import org.w3c.dom.stylesheets.StyleSheet;

/**
 */
public interface IStyleSheetAdapter extends ICSSModelAdapter {

	/**
	 * Returns HTML/XML element that is the owner of this adapter
	 */
	Element getElement();

	/**
	 * Returns CSS document that is related to this element
	 */
	public StyleSheet getSheet();

	/**
	 * This is called at the time of releasing HTML/XML model
	 */
	public void released();

	/**
	 * This is called at the time of removing this Element from the document
	 */
	public void removed();
}
