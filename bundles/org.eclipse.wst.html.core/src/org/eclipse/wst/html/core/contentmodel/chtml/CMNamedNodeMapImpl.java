/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.contentmodel.chtml;



import java.util.Iterator;

import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * An implementation of the CMNamedNodeMap interface.
 * This class is intented to be used as a container of attribute declarations.
 * If someone wants to use this class for other purpose, he must pay attention
 * to the fact that this class is tolerant of the key name case.  That is, this
 * class does not distinguish "name", "NAME", and "Name" as a key name.
 */
class CMNamedNodeMapImpl implements org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap {

	private java.util.Hashtable items = null;

	/**
	 */
	public CMNamedNodeMapImpl() {
		super();
		items = new java.util.Hashtable();
	}

	/**
	 * getLength method
	 * @return int
	 */
	public int getLength() {
		return items.size();
	}

	/**
	 * getNamedItem method
	 * @return CMNode <code>null</code> for unknown keys.
	 * @param name java.lang.String
	 */
	public CMNode getNamedItem(String name) {
		String cookedName = makeCanonicalForm(name);
		if (!items.containsKey(cookedName))
			return null;
		return (CMNode) items.get(cookedName);
	}

	/**
	 * item method
	 * @return CMNode
	 * @param index int
	 */
	public CMNode item(int index) {
		Iterator iter = iterator();
		while (iter.hasNext()) {
			CMNode node = (CMNode) iter.next();
			if (--index < 0)
				return node;
		}
		return null;
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator iterator() {
		return items.values().iterator();
	}

	/**
	 * @return java.lang.String
	 * @param rawForm java.lang.String
	 */
	private String makeCanonicalForm(String rawForm) {
		return rawForm.toUpperCase();
	}

	/**
	 * @param key java.lang.String
	 * @param item java.lang.String
	 */
	void putNamedItem(String name, CMNode item) {
		String cookedName = makeCanonicalForm(name);
		if (items.containsKey(cookedName))
			return; // already registered.
		items.put(cookedName, item);
	}
}