/*******************************************************************************
 * Copyright (c) 2001, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.parser;

import java.io.Reader;

public final class NameValidator {

	private static HTMLNames nameChecker = null;

	/**
	 * Returns <code>true</code> if <code>name</code> is valid HTML name
	 * according to XML 1.0 rules with allowances for scripting language
	 * templates, <code>false</code> otherwise
	 * 
	 * @param name
	 *            name is the string to test
	 * @return true if valid name according to our rules, false otherwise.
	 */
	public synchronized static final boolean isValid(String name) {

		if (nameChecker == null) {
			nameChecker = initializeScanner();
		}
		return nameChecker.isAllowedName(name);
	}

	private static HTMLNames initializeScanner() {
		return new HTMLNames((Reader) null);
	}

	/**
	 * Not intended to be instantiated.
	 */
	private NameValidator() {
		super();
	}
}
