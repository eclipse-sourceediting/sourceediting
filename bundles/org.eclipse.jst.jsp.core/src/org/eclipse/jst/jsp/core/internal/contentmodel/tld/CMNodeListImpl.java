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



import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;

public class CMNodeListImpl implements CMNodeList {
	static CMNodeListImpl EMPTY_NODE_LIST = new CMNodeListImpl(Collections.EMPTY_LIST);
	protected List list;

	public CMNodeListImpl() {
		this(new ArrayList());
	}

	public CMNodeListImpl(List list) {
		this.list = list;
	}

	public void appendItem(CMNode node) {
		list.add(node);
	}

	/**
	 * getLength method
	 * @return int
	 */
	public int getLength() {
		return list.size();
	}

	public List getList() {
		return list;
	}

	/**
	 * item method
	 * @return CMNode
	 * @param index int
	 */
	public CMNode item(int index) {
		return (CMNode) list.get(index);
	}

	public Iterator iterator() {
		return list.iterator();
	}
}
