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

public class CMGroupNode extends CMRepeatableNode {
	private int type = CMNodeType.GROUP;
	private int groupKind = CMNodeType.GROUP_SEQUENCE;
	private Vector children = new Vector();

	public CMGroupNode() {
		super("GROUP");
	}

	public int getType() {
		return type;
	}

	// implement super class
	public void setType(int type) {
		// can't change - only one type allows
	}

	public int getGroupKind() {
		return groupKind;
	}

	public void setGroupKind(int kind) {
		groupKind = kind;
	}

	public Vector getChildren() {
		return children;
	}

	public void addChild(CMNode child) {
		children.addElement(child);
	}

	public String toString() {
		String result = "Group ( - kind: " + getGroupKind() + " OccType: " + getOccurrence() + "\n";

		Enumeration en = children.elements();
		while (en.hasMoreElements()) {
			result += " " + en.nextElement();
		}

		result += "Group )";
		return result;
	}
}
