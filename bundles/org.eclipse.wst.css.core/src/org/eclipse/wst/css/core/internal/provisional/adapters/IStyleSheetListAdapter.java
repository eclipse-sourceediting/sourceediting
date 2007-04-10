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



import java.util.Enumeration;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.stylesheets.StyleSheetList;

/**
 */
public interface IStyleSheetListAdapter extends INodeAdapter {

	/**
	 */
	Enumeration getClasses();

	/**
	 */
	public CSSStyleDeclaration getOverrideStyle(Element element, String pseudoName);

	/**
	 */
	public StyleSheetList getStyleSheets();

	/**
	 */
	public void releaseStyleSheets();
}
