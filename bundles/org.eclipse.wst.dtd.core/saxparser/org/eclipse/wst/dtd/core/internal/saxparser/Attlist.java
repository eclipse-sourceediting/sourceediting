/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.saxparser;

import java.util.Enumeration;
import java.util.Vector;

public class Attlist extends BaseNode {
	Vector attDefs = new Vector();

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            This attribute list's name; this value is also known as the
	 *            Element type.
	 */
	public Attlist(String name, String ownerDTD) {
		this(name, ownerDTD, null, null);
	}

	public Attlist(String name, String ownerDTD, String comment, ErrorMessage errorMessage) {
		super(name, ownerDTD, comment, errorMessage);
	}


	/**
	 * Adds the specified attribute definition to the end of this attribute
	 * list .
	 * 
	 * @param attDef
	 *            Attribute definition to add to this atribute list.
	 * @return =true if the attribute definition does not already exist in
	 *         this attribute list; otherwise, =false.
	 * @see #elementAt
	 * @see #getAttDef
	 * @see #contains
	 * @see #size
	 */
	public boolean addElement(AttNode attDef) {
		if (contains(attDef.name))
			return false;
		this.attDefs.addElement(attDef);
		return true;
	}

	/**
	 * Returns the attribute definition at the specified <var>index</var> in
	 * this attribute list
	 * 
	 * @param index
	 *            Index into this list of attribute definitions.
	 * @return Attribute definition at the specified index, or <var>null</var>
	 *         if an invalid index.
	 * @see #addElement
	 * @see #getAttDef
	 * @see #contains
	 * @see #size
	 */
	public AttNode elementAt(int index) {
		return (AttNode) this.attDefs.elementAt(index);
	}

	/**
	 * Returns the attribute definition that matches the specified attribute
	 * definition name in this attribute list.
	 * 
	 * @param attDefName
	 *            Attribute definition name to match in this attribute list.
	 * @return The matching attribute definition, or <var>null</var> if the
	 *         attribute is not currently defined.
	 * @see #addElement
	 * @see #elementAt
	 * @see #contains
	 * @see #size
	 * @see #elements
	 */
	public AttNode getAttDef(String attDefName) {
		for (int i = 0; i < size(); i++) {
			AttNode ad = elementAt(i);
			if (attDefName.equals(ad.name))
				return ad;
		}
		return null;
	}

	/**
	 * Returns whether the specified attribute definition name is currently
	 * defined in this attribute list.
	 * 
	 * @param attDefName
	 *            Attribute definition name to match in this attribute list.
	 * @return =true if <var>attDefName</var> is defined; otherwise, =false.
	 * @see #addElement
	 * @see #elementAt
	 * @see #getAttDef
	 * @see #size
	 * @see #elements
	 */
	public boolean contains(String attDefName) {
		return null != getAttDef(attDefName);
	}

	/**
	 * Returns the number of attribute definitions in this attribute list.
	 * 
	 * @return Number of attribute list definitions, or <var>null</var> if no
	 *         definitions defined.
	 * @see #addElement
	 * @see #elementAt
	 * @see #getAttDef
	 * @see #contains
	 */
	public int size() {
		return this.attDefs.size();
	}

	/**
	 * Returns an enumeration of all attribute definitions in this attribute
	 * list.
	 * 
	 * @return An enumeration of all attribute definitions, or <var>null</var>
	 *         if none specified.
	 * @see #addElement
	 * @see #elementAt
	 * @see #getAttDef
	 * @see #contains
	 * @see #size
	 */
	public Enumeration elements() {
		return this.attDefs.elements();
	}

	void setAttDefs(Vector attrs) {
		attDefs = attrs;
	}
}
