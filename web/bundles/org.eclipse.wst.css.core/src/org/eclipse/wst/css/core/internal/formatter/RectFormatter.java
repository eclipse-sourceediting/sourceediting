/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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
package org.eclipse.wst.css.core.internal.formatter;



/**
 * 
 */
public class RectFormatter extends FunctionFormatter {

	private static RectFormatter instance;

	/**
	 * 
	 */
	RectFormatter() {
		super();
	}

	/**
	 * 
	 */
	protected String getFunc() {
		return "rect(";//$NON-NLS-1$
	}

	/**
	 * 
	 */
	public synchronized static RectFormatter getInstance() {
		if (instance == null)
			instance = new RectFormatter();
		return instance;
	}
}