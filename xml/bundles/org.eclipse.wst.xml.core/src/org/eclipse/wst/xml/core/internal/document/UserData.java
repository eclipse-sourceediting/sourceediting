/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.w3c.dom.Node;

/**
 * Collects user data associated to a Node.
 * 
 */
final class UserData {

	private static UserData fInstance;

	/**
	 * Mapping of a Node to its User Data table (represented by a Map)
	 */
	private Map fData;

	private UserData() {
		fData = new WeakHashMap(0);
	}

	public static synchronized UserData getInstance() {
		if (fInstance == null)
			fInstance = new UserData();
		return fInstance;
	}

	/**
	 * Get the user data table associated with <code>node</code>.
	 * 
	 * @param node the node
	 * @return the user data table associated with the <code>node</code>
	 */
	public synchronized Map getUserDataTable(Node node) {
		if (fData.containsKey(node))
			return (Map) fData.get(node);
		Map table = new HashMap();
		fData.put(node, table);
		return table;
	}
}
