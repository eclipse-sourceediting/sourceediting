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

import java.util.Enumeration;
import java.util.Vector;

public class DTD {
	//
	// Constants
	//

	Vector externalElements = new Vector();
	String name = null;

	boolean isExceptionDuringParse = false;

	//
	// Constructors
	//

	/**
	 * Constructor.
	 */

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The file name of this DTD.
	 * @see #getName
	 * @see #setName
	 */
	public DTD(String name) {
		this.name = name;
	}

	/**
	 * Returns this DTD's file name.
	 * 
	 * @return This DTD's file name, or <var>null</var> if no name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets this DTD's file name.
	 * 
	 * @param name
	 *            This DTD's name.
	 * @see #getName
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns an Enumeration instance of all external subset children of this
	 * DTD.
	 * 
	 * @return An enumeration of all external subset children of this DTD.
	 */
	public Enumeration externalElements() {
		return externalElements.elements();
	}

	public void addDecl(BaseNode decl) {
		externalElements.addElement(decl);
	}

	public void setIsExceptionDuringParse(boolean on) {
		isExceptionDuringParse = on;
	}

	public boolean getIsExceptionDuringParse() {
		return isExceptionDuringParse;
	}

} // class DTD
