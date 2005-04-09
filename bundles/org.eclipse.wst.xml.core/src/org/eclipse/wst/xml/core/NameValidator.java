/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.xml.core;

import java.io.Reader;

import org.eclipse.wst.xml.core.internal.parser.XML10Names;

/**
 * This class provides consistent way to pre-check if a string is a valid XML
 * name, before, for example, trying to actually create an Element with it.
 * Espeically useful when the name to be checked is based on some user input,
 * and must, basically, be validated.
 * 
 * @since 1.0
 */
public final class NameValidator {

	private static XML10Names xml10charChecker = null;

	/**
	 * Returns true if <code>name</code> is valid XML name according to 1.0
	 * rules, false otherwise.
	 * 
	 * @param name
	 * @return boolean
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
