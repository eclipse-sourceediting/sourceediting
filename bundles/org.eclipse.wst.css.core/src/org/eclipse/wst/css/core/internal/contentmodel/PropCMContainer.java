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
import java.util.Vector;

/**
 * 
 */
public class PropCMContainer extends PropCMNode {

	private java.util.Vector nodes = new Vector();
	private static java.util.Hashtable instances = null;
	public final static String VAL_ABSOLUTE_SIZE = "absolute-size";//$NON-NLS-1$
	public final static String VAL_BORDER_STYLE = "border-style";//$NON-NLS-1$
	public final static String VAL_BORDER_WIDTH = "border-width";//$NON-NLS-1$
	public final static String VAL_COLOR = "color";//$NON-NLS-1$
	public final static String VAL_SYSTEM_COLOR = "system-color";//$NON-NLS-1$
	public final static String VAL_GENERIC_FAMILY = "generic-family";//$NON-NLS-1$
	public final static String VAL_GENERIC_VOICE = "generic-voice";//$NON-NLS-1$
	public final static String VAL_MARGIN_WIDTH = "margin-width";//$NON-NLS-1$
	public final static String VAL_PADDING_WIDTH = "padding-width";//$NON-NLS-1$
	public final static String VAL_RELATIVE_SIZE = "relative-size";//$NON-NLS-1$

	/**
	 * 
	 */
	protected PropCMContainer(String name) {
		super(name);
	}

	/**
	 * 
	 */
	Object appendChild(Object node) {
		if (!nodes.contains(node))
			nodes.add(node);
		return node;
	}

	/**
	 * 
	 */
	public boolean canHave(String identifier) {
		int nChild = getNumChild();
		for (int i = 0; i < nChild; i++) {
			Object child = getChildAt(i);
			if (child instanceof String && identifier.equalsIgnoreCase((String) child))
				return true;
			if (child instanceof PropCMContainer && ((PropCMContainer) child).canHave(identifier))
				return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public Object getChildAt(int index) {
		if (nodes == null || index < 0 || nodes.size() <= index)
			return null;
		return nodes.elementAt(index);
	}

	/**
	 * 
	 */
	static PropCMContainer getContInstanceOf(String name) {
		if (name == null)
			return null;

		// initialize
		if (instances == null)
			instances = new Hashtable(20);

		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		// query
		Object node = instances.get(name);
		if (node != null)
			return (PropCMContainer) node;

		if (PropCMNode.isLoading()) {
			// register
			node = new PropCMContainer(name);
			instances.put(name, node);
		}

		return (PropCMContainer) node;
	}

	/**
	 * 
	 */
	void getIdentifiers(Set idents) {
		if (idents == null)
			return;
		int nChildren = nodes.size();
		for (int i = 0; i < nChildren; i++) {
			Object node = nodes.elementAt(i);
			if (node instanceof PropCMNode)
				((PropCMNode) node).getIdentifiers(idents);
			else if (node instanceof String)
				idents.add(node);
		}
	}

	/**
	 * 
	 */
	public int getNumChild() {
		if (nodes == null)
			return 0;
		return nodes.size();
	}

	/**
	 * 
	 */
	public short getType() {
		return VAL_CONTAINER;
	}

	/**
	 * 
	 */
	void getValues(Collection values) {
		if (values == null)
			return;
		int nChildren = nodes.size();
		for (int i = 0; i < nChildren; i++) {
			Object node = nodes.elementAt(i);
			if (node instanceof PropCMNode)
				((PropCMNode) node).getValues(values);
			else if (node instanceof String) {
				if (!values.contains(node))
					values.add(node);
			}
		}
	}

	/**
	 * 
	 */
	Object removeChild(Object node) {
		nodes.remove(node);
		return node;
	}
}