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



import java.util.Iterator;

import org.w3c.dom.Element;

/**
 * 
 */
public interface ICSSSelectorList {

	/**
	 * @return java.util.Iterator
	 */
	Iterator getIterator();

	/**
	 * @return int
	 */
	int getLength();

	/**
	 * @return java.util.Vector
	 * @param index
	 *            int
	 */
	ICSSSelector getSelector(int index);

	/**
	 * @return java.lang.String
	 */
	String getString();

	/**
	 * 
	 */
	int getErrorCount();

	/**
	 * 
	 */
	Iterator getErrors();

	/**
	 * @return boolean
	 * @param element
	 *            org.w3c.dom.Element
	 */
	boolean match(Element element, String pseudoName);
}
