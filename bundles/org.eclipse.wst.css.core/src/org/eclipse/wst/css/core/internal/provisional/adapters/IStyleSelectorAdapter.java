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



import org.eclipse.wst.css.core.internal.provisional.document.ICSSSimpleSelector;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.w3c.dom.Element;


/**
 * 
 */
public interface IStyleSelectorAdapter extends INodeAdapter {

	/**
	 * @return boolean
	 * @param element
	 *            org.w3c.dom.Element
	 * @param pseudoName
	 *            java.lang.String
	 */
	boolean match(ICSSSimpleSelector selector, Element element, String pseudoName);
}
