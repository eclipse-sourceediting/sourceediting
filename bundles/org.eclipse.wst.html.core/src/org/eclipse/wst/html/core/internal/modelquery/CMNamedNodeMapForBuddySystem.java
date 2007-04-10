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
package org.eclipse.wst.html.core.internal.modelquery;


import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 */
abstract class CMNamedNodeMapForBuddySystem implements CMNamedNodeMap {


	private boolean isXHTML = false;
	private Hashtable map = new Hashtable();

	/**
	 * Constructor of CMNamedNodeMapForBuddySystem.
	 * CAUTION: Each derived class must call 'makeBuddySystem' method in its
	 * constructor to build up its contents.
	 */
	public CMNamedNodeMapForBuddySystem(boolean isXHTML) {
		super();
		this.isXHTML = isXHTML;
	}

	/*
	 * @see CMNamedNodeMap#getLength()
	 */
	public int getLength() {
		return map.size();
	}

	/*
	 * @see CMNamedNodeMap#getNamedItem(String)
	 */
	public CMNode getNamedItem(String name) {
		String key = canonicalName(name);
		if (!map.containsKey(key))
			return null;
		return (CMNode) map.get(key);
	}

	/*
	 * @see CMNamedNodeMap#item(int)
	 */
	public CMNode item(int index) {
		Iterator iter = iterator();
		while (iter.hasNext()) {
			Object node = iter.next();
			if (--index < 0)
				return (CMNode) node;
		}
		return null;
	}

	/*
	 * @see CMNamedNodeMap#iterator()
	 */
	public Iterator iterator() {
		return map.values().iterator();
	}

	/* package scope. */
	void put(String name, CMNode cmnode) {
		if (name == null || cmnode == null)
			return;
		map.put(canonicalName(name), cmnode);
	}

	abstract protected String getKeyName(CMNode original);

	abstract protected CMNode createBuddySystem(CMNode original);

	protected boolean isXHTML() {
		return isXHTML;
	}

	/**
	 * Each derived class must call this method in its constructor
	 * to build up its contents.
	 */
	protected void makeBuddySystem(CMNamedNodeMap original) {
		Iterator i = original.iterator();
		if (i == null)
			return;
		while (i.hasNext()) {
			CMNode org = (CMNode) i.next();
			String key = getKeyName(org);
			CMNode newNode = createBuddySystem(org);
			put(key, newNode);
		}
	}

	private String canonicalName(String name) {
		return name.toUpperCase();
	}
}
