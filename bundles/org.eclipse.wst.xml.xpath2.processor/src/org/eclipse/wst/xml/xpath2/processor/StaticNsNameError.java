/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor;

/**
 * Static namespace name error class.
 */
public class StaticNsNameError extends StaticNameError {
	
	public StaticNsNameError(String reason) {
		super(reason);
	}

	/**
	 * Constructor.
	 * @param pref is the unknown prefix.
	 * @return the error.
	 */
	public static StaticNsNameError unknown_prefix(String pref) {
		String error = "Unknown prefix";

		if(pref != null)
			error += ": " + pref;
		error += ".";

		return new StaticNsNameError(error);
	}
}
