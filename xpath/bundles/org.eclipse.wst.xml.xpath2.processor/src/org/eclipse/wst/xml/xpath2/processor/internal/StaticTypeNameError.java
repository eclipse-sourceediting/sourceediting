/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     David Carver (STAR) - bug 273763 - correct error codes  
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal;

/**
 * Static type name error class.
 */
public class StaticTypeNameError extends StaticNameError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7328671571088574947L;
	public static final String TYPE_NOT_FOUND = "XPST0051";

	/**
	 * Constructor.
	 * 
	 * @param reason
	 *            is the reason for the error.
	 */
	public StaticTypeNameError(String reason) {
		super(TYPE_NOT_FOUND, reason);
	}
}
