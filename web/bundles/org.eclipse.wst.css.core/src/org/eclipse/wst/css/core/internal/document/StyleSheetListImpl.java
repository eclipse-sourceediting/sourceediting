/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;



import org.w3c.dom.stylesheets.StyleSheet;
import org.w3c.dom.stylesheets.StyleSheetList;

/**
 * 
 */
class StyleSheetListImpl extends AbstractCSSNodeList implements StyleSheetList {

	/**
	 * StyleSheetListImpl constructor comment.
	 */
	StyleSheetListImpl() {
		super();
	}

	/**
	 * Used to retrieve a style sheet by ordinal index. If index is greater
	 * than or equal to the number of style sheets in the list, this returns
	 * <code>null</code>.
	 * 
	 * @param indexIndex
	 *            into the collection
	 * @return The style sheet at the <code>index</code> position in the
	 *         <code>StyleSheetList</code>, or <code>null</code> if that
	 *         is not a valid index.
	 */
	public StyleSheet item(int index) {
		return (StyleSheet) itemImpl(index);
	}
}