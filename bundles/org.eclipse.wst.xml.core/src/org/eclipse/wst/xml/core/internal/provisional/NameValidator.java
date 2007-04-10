/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.provisional;

import java.io.Reader;

import org.eclipse.wst.xml.core.internal.parser.XML10Names;

/**
 * This class provides consistent way to pre-check if a string is a valid XML
 * name, before, for example, trying to actually create an Element with it.
 * Attempting to create an Element with an invalid name will throw the appropriate
 * DOM Exception, but often clients want to check the validiting of a name
 * such as based on some user input, long in advance of actually making 
 * the DOM call. And, natually, want to give the user feedback in a more 
 * timely fashion.
 * 
 * ISSUE: is "endns:" really valid xml name? I think not, but this method
 * (currently) says it is.
 * 
 * @plannedfor 1.0
 */
public final class NameValidator {

	private static XML10Names xml10charChecker = null;

	/**
	 * Returns true if <code>name</code> is valid XML name according to XML
	 * 1.0 rules, false otherwise.
	 * 
	 * @param name
	 *            name is the string to test
	 * @return true if valid name according to XML 1.0 rules, false otherwise.
	 */
	public synchronized static final boolean isValid(String name) {

		if (xml10charChecker == null) {
			xml10charChecker = inititailizeXML10Names();
		}
		return xml10charChecker.isValidXML10Name(name);
	}

	private static XML10Names inititailizeXML10Names() {
		return new XML10Names((Reader) null);
	}

	/**
	 * Not intenteded to be instantiated.
	 */
	private NameValidator() {
		super();
	}
}
