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



public final class NameValidator {

	private static XML10Names charChecker = new XML10Names((Reader) null);

	public synchronized static final boolean isValid(String name) {

		return charChecker.isValidXML10Name(name);
	}
}
