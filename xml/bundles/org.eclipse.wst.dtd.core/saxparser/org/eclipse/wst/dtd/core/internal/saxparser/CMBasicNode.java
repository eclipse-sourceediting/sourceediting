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

public class CMBasicNode extends CMNode {
	private int type = CMNodeType.EMPTY; // possible types :

	// CMNodeType.EMPTY
	// CMNodeType.ANY
	// CMNodeType.PCDATA

	/** Default constructor. */
	public CMBasicNode(String refName) {
		super(refName);
	}

	public CMBasicNode(String refName, int type) {
		super(refName);
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String toString() {
		return " BasicNodeName: " + getName() + " Type: " + getType() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
