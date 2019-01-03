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

public abstract class CMNode {
	private String refName;

	/** Default constructor. */
	public CMNode(String refName) {
		this.refName = refName;
	}

	public String getName() {
		return refName;
	}

	public abstract int getType();

	public abstract void setType(int type);

}
