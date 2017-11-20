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



import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 
 */
public class PropCMSubProperty extends PropCMProperty {

	// static fields
	private static java.util.Hashtable instances = null;
	public final static String PSUB_BG_POSITION_X = "background-positionH";//$NON-NLS-1$
	public final static String PSUB_BG_POSITION_Y = "background-positionV";//$NON-NLS-1$
	public final static String PSUB_CLIP_TOP = "clipTop";//$NON-NLS-1$
	public final static String PSUB_CLIP_RIGHT = "clipRight";//$NON-NLS-1$
	public final static String PSUB_CLIP_BOTTOM = "clipBottom";//$NON-NLS-1$
	public final static String PSUB_CLIP_LEFT = "clipLeft";//$NON-NLS-1$

	/**
	 * 
	 */
	protected PropCMSubProperty(String name) {
		super(name);
	}

	/**
	 * 
	 */
	public PropCMProperty getContainer() {
		return shorthandContainerAt(0);
	}

	/**
	 * 
	 */
	public static PropCMProperty getInstanceOf(String name) {
		// initialize
		if (instances == null)
			instances = new Hashtable(10);

		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		// query
		Object node = instances.get(name);
		if (node != null)
			return (PropCMProperty) node;

		// register
		if (PropCMNode.isLoading()) {
			node = new PropCMSubProperty(name);
			instances.put(name, node);
		}

		return (PropCMProperty) node;
	}

	/**
	 * 
	 */
	public static Enumeration getNameEnum() {
		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		return instances.keys();
	}

	/**
	 * 
	 */
	public static Enumeration getPropertyEnum() {
		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		return instances.elements();
	}

	/**
	 * 
	 */
	public short getType() {
		return VAL_SUBPROPERTY;
	}

	/**
	 * 
	 */
	public static Vector names() {
		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		return new Vector(instances.keySet());
	}

	/**
	 * 
	 */
	public void setContainer(PropCMProperty prop) {
		if (containers == null)
			containers = new Vector();
		containers.clear();
		containers.add(prop);
	}
}