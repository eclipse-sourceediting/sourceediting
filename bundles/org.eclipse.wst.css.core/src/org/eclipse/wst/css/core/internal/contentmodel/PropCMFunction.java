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
package org.eclipse.wst.css.core.internal.contentmodel;



import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

/**
 * 
 */
public class PropCMFunction extends PropCMNode {

	private static java.util.Hashtable instances = null;
	public final static String VAL_ATTR = "attr";//$NON-NLS-1$
	public final static String VAL_COUNTER = "counter";//$NON-NLS-1$
	public final static String VAL_RGB = "rgb";//$NON-NLS-1$
	public final static String VAL_SHAPE = "rect";//$NON-NLS-1$
	public final static String VAL_URI = "url";//$NON-NLS-1$
	public final static String VAL_FORMAT = "format";//$NON-NLS-1$
	public final static String VAL_LOCAL = "local";//$NON-NLS-1$

	/**
	 * 
	 */
	protected PropCMFunction(String name) {
		super(name);
	}

	/**
	 * 
	 */
	void getIdentifiers(Set indents) {
	}

	/**
	 * 
	 */
	static public PropCMFunction getInstanceOf(String name) {
		if (name == null)
			return null;

		// initialize
		if (instances == null)
			instances = new Hashtable(10);

		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		// query
		Object node = instances.get(name);
		if (node != null)
			return (PropCMFunction) node;

		// register
		if (PropCMNode.isLoading()) {
			node = new PropCMFunction(name);
			instances.put(name, node);
		}

		return (PropCMFunction) node;
	}

	/**
	 * 
	 */
	public short getType() {
		return VAL_FUNC;
	}

	/**
	 * 
	 */
	void getValues(Collection values) {
		if (values != null && !values.contains(this))
			values.add(this);
	}
}