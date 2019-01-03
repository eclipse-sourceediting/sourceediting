/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
package org.eclipse.wst.html.core.internal.contentmodel.chtml;



/**
 * Implementation of CMContent for HTML CM.
 */
abstract class CMContentImpl extends CMNodeImpl implements org.eclipse.wst.xml.core.internal.contentmodel.CMContent {

	public static final int UNBOUNDED = -1;
	/**  -1: it's UNBOUNDED. */
	private int maxOccur = UNBOUNDED;
	/**  0: it's OPTIONAL, 1, it's REQUIRED. */
	private int minOccur = 0;

	/**
	 * The primary consturctor.
	 * Use this one for usual cases.<br>
	 */
	public CMContentImpl(String name, int min, int max) {
		super(name);
		minOccur = min;
		maxOccur = max;
	}

	/**
	 * getMaxOccur method
	 * @return int
	 *
	 * If -1, it's UNBOUNDED.
	 */
	public int getMaxOccur() {
		return maxOccur;
	}

	/**
	 * getMinOccur method
	 * @return int
	 *
	 * If 0, it's OPTIONAL.
	 * If 1, it's REQUIRED.
	 */
	public int getMinOccur() {
		return minOccur;
	}
}
