/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;

import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;



/**
 * Implementation of CMContent for HTML CM.
 */
abstract class CMContentImpl extends CMNodeImpl implements CMContent {

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
