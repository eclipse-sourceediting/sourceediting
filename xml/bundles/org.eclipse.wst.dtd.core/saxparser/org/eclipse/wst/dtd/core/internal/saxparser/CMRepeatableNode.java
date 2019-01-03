/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.saxparser;

public abstract class CMRepeatableNode extends CMNode {
	private int occType = CMNodeType.ONE;

	public CMRepeatableNode(String refName) {
		super(refName);
	}

	public CMRepeatableNode(String refName, int occType) {
		super(refName);
		this.occType = occType;
	}

	public int getOccurrence() {
		return occType;
	}

	public void setOccurrence(int occType) {
		this.occType = occType;
	}

}
