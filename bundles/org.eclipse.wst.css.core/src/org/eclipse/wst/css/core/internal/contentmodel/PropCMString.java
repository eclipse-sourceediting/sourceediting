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
package org.eclipse.wst.css.core.internal.contentmodel;



import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

/**
 * 
 */
public class PropCMString extends PropCMNode {

	private static java.util.Hashtable instances = null;
	public final static java.lang.String VAL_FONT = "font";//$NON-NLS-1$
	public final static java.lang.String VAL_COUNTER_ID = "counterId";//$NON-NLS-1$
	public final static java.lang.String VAL_PAGE_ID = "pageId";//$NON-NLS-1$
	public final static java.lang.String VAL_VOICE = "voice";//$NON-NLS-1$
	public final static java.lang.String VAL_ANY = "any"; //$NON-NLS-1$

	/**
	 * 
	 */
	protected PropCMString(String name) {
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
	static public PropCMString getInstanceOf(String name) {
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
			return (PropCMString) node;

		// register
		node = new PropCMString(name);
		instances.put(name, node);

		return (PropCMString) node;
	}

	/**
	 * 
	 */
	public short getType() {
		return VAL_STRING;
	}

	/**
	 * 
	 */
	void getValues(Collection values) {
		if (values != null && !values.contains(this))
			values.add(this);
	}
}
