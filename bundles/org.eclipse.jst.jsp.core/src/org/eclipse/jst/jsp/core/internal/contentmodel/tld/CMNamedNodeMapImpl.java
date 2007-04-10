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
package org.eclipse.jst.jsp.core.internal.contentmodel.tld;



import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

public class CMNamedNodeMapImpl implements CMNamedNodeMap {

	public static CMNamedNodeMapImpl EMPTY_NAMED_NODE_MAP = new CMNamedNodeMapImpl();
	protected Hashtable table = new Hashtable();

	/**
	 * CMNamedNodeMapImpl constructor comment.
	 */
	public CMNamedNodeMapImpl() {
		super();
	}

	Hashtable getHashtable() {
		return table;
	}

	/**
	 * getLength method
	 * @return int
	 */
	public int getLength() {
		return table.size();
	}

	/**
	 * getNamedItem method
	 * @return CMNode
	 * @param name java.lang.String
	 */
	public CMNode getNamedItem(String name) {
		return (CMNode) table.get(name);
	}

	/**
	 * item method
	 * @return CMNode
	 * @param index int
	 */
	public CMNode item(int index) {
		Object result = null;
		int size = table.size();
		if (index < size) {
			Iterator values = iterator();
			for (int i = 0; i <= index; i++) {
				result = values.next();
			}
		}
		return (CMNode) result;
	}

	public Iterator iterator() {
		return table.values().iterator();
	}

	/**
	 * getNamedItem method
	 * @return
	 * @param name java.lang.String
	 * @param aNode CMNode
	 */
	public void setNamedItem(String name, CMNode aNode) {
		if (name != null && aNode != null)
			table.put(name, aNode);
	}
}
