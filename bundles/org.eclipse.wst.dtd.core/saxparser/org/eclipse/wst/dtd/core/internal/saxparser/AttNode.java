/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
	private static final String[] fgAttTypeString = {"CDATA", // 0
				"ID", // 1
				"IDREF", // 2
				"IDREFS", // 3
				"ENTITY", // 4
				"ENTITIES", // 5
				"NMTOKEN", // 6
				"NMTOKENS", // 7
				"NOTATION", // 8
				"ENUMERATION", // 9 (replaced with "NMTOKEN" by SAX
								// AttributeList handler)
				"%ENTITYREFERENCE;"};

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

		if (type.startsWith("%") && type.endsWith(";"))
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

		if (defaultType.startsWith("%") && defaultType.endsWith(";"))
			return PEREFERENCE;
		else if (defaultType.equals("#REQUIRED"))
			return REQUIRED;
		else if (defaultType.equals("#IMPLIED"))
			return IMPLIED;
		else if (defaultType.equals("#FIXED"))
			return FIXED;
		else
			return NOFIXED;
	}

	public String toString() {
		return "Att Name: " + name + " Type: " + type + " defaultType: " + defaultType + " defaultValue: " + defaultValue;
	}
}
