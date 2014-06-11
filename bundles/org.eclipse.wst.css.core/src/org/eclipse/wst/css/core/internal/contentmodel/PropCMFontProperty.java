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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * 
 */
public class PropCMFontProperty extends PropCMProperty {

	// static fields
	private static java.util.Hashtable instances = null;
	private static java.util.Hashtable cachedIdMap = null;
	// selecting properties
	public final static String PF_FONT_FAMILY = "font-family";//$NON-NLS-1$
	public final static String PF_FONT_STYLE = "font-style";//$NON-NLS-1$
	public final static String PF_FONT_VARIANT = "font-variant";//$NON-NLS-1$
	public final static String PF_FONT_WEIGHT = "font-weight";//$NON-NLS-1$
	public final static String PF_FONT_STRETCH = "font-stretch";//$NON-NLS-1$
	public final static String PF_FONT_SIZE = "font-size";//$NON-NLS-1$
	// qualification properties
	public final static String PF_UNICODE_RANGE = "unicode-range";//$NON-NLS-1$
	// numeric properties
	public final static String PF_UNITS_PER_EM = "units-per-em";//$NON-NLS-1$
	// referencing properties
	public final static String PF_SRC = "src";//$NON-NLS-1$
	// matching properties
	public final static String PF_PANOSE_1 = "panose-1";//$NON-NLS-1$
	public final static String PF_STEMV = "stemv";//$NON-NLS-1$
	public final static String PF_STEMH = "stemh";//$NON-NLS-1$
	public final static String PF_SLOPE = "slope";//$NON-NLS-1$
	public final static String PF_CAP_HEIGHT = "cap-height";//$NON-NLS-1$
	public final static String PF_X_HEIGHT = "x-height";//$NON-NLS-1$
	public final static String PF_ASCENT = "ascent";//$NON-NLS-1$
	public final static String PF_DESCENT = "descent";//$NON-NLS-1$
	// synthesis properties
	public final static String PF_WIDTHS = "widths";//$NON-NLS-1$
	public final static String PF_BBOX = "bbox";//$NON-NLS-1$
	public final static String PF_DEFINITION_SRC = "definition-src";//$NON-NLS-1$
	// alignment properties
	public final static String PF_BASELINE = "baseline";//$NON-NLS-1$
	public final static String PF_CENTERLINE = "centerline";//$NON-NLS-1$
	public final static String PF_MATHLINE = "mathline";//$NON-NLS-1$
	public final static String PF_TOPLINE = "topline";//$NON-NLS-1$

	/**
	 * 
	 */
	protected PropCMFontProperty(String name) {
		super(name);
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
			node = new PropCMFontProperty(name);
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
		return VAL_FONTPROPERTY;
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
	public static List names(Object mediaGroup) {
		if (mediaGroup == null)
			return names();

		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		Vector properties = new Vector();

		Iterator it = instances.values().iterator();
		while (it.hasNext()) {
			PropCMProperty prop = (PropCMProperty) it.next();
			if (prop.getMediaGroups().contains(mediaGroup))
				properties.add(prop.getName());
		}

		return properties;
	}

	/**
	 * If itentifier is null, get all properties
	 */
	public static Vector propertiesFor(String identifier, boolean shorthands) {
		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		// if identifier is null, get all properties
		if (identifier == null)
			return new Vector(instances.values());

		if (cachedIdMap == null) {
			// start cacheing
			cachedIdMap = new Hashtable();
		}
		else {
			// search cached
			Object ret = cachedIdMap.get(identifier + String.valueOf(shorthands));
			if (ret != null)
				return new Vector((Collection) ret);
		}

		// create
		Enumeration properties = getPropertyEnum();
		HashSet set = new HashSet();
		while (properties.hasMoreElements()) {
			PropCMProperty prop = (PropCMProperty) properties.nextElement();
			if (!shorthands && prop.isShorthand())
				continue;
			if (prop.canHave(identifier))
				set.add(prop);
		}

		// cache
		cachedIdMap.put(identifier + String.valueOf(shorthands), set);

		return new Vector(set);
	}
}