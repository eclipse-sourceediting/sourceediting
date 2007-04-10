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

public final class AttNode {
	//
	// attType
	//
	public static final int CDATA = 0, ID = 1, IDREF = 2, IDREFS = 3, ENTITY = 4, ENTITIES = 5, NMTOKEN = 6, NMTOKENS = 7, NOTATION = 8, ENUMERATION = 9, PEREFERENCE = 10;

	//
	// Keep this array in-sync with the ATTTYPE definitions
	//
	private static final String[] fgAttTypeString = {"CDATA", // 0 //$NON-NLS-1$
				"ID", // 1 //$NON-NLS-1$
				"IDREF", // 2 //$NON-NLS-1$
				"IDREFS", // 3 //$NON-NLS-1$
				"ENTITY", // 4 //$NON-NLS-1$
				"ENTITIES", // 5 //$NON-NLS-1$
				"NMTOKEN", // 6 //$NON-NLS-1$
				"NMTOKENS", // 7 //$NON-NLS-1$
				"NOTATION", // 8 //$NON-NLS-1$
				"ENUMERATION", // 9 (replaced with "NMTOKEN" by SAX //$NON-NLS-1$
								// AttributeList handler)
				"%ENTITYREFERENCE;"}; //$NON-NLS-1$

	//
	// attDefaultType
	//
	public static final int NOFIXED = 1, // AttValue
				REQUIRED = 2, // #REQUIRED
				IMPLIED = 3, // #IMPLIED
				FIXED = 4; // #FIXED AttValue

	public String name = null;
	public String type = null;
	public String defaultType = null;
	public String defaultValue = null;
	public Vector enumList = null; // list of Notations or Enum values

	public int getDeclaredType() {
		int t = -1;
		if (type == null)
			return t;

		if (type.startsWith("%") && type.endsWith(";")) //$NON-NLS-1$ //$NON-NLS-2$
			return PEREFERENCE;

		for (int i = 0; i < fgAttTypeString.length; i++) {
			if (type.equals(fgAttTypeString[i])) {
				t = i;
				break;
			}
		}
		return t;
	}

	public Enumeration elements() {
		if (enumList == null)
			enumList = new Vector();
		return enumList.elements();
	}

	public int getDefaultType() {
		if (defaultType == null)
			return -1;

		if (defaultType.startsWith("%") && defaultType.endsWith(";")) //$NON-NLS-1$ //$NON-NLS-2$
			return PEREFERENCE;
		else if (defaultType.equals("#REQUIRED")) //$NON-NLS-1$
			return REQUIRED;
		else if (defaultType.equals("#IMPLIED")) //$NON-NLS-1$
			return IMPLIED;
		else if (defaultType.equals("#FIXED")) //$NON-NLS-1$
			return FIXED;
		else
			return NOFIXED;
	}

	public String toString() {
		return "Att Name: " + name + " Type: " + type + " defaultType: " + defaultType + " defaultValue: " + defaultValue; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
}
