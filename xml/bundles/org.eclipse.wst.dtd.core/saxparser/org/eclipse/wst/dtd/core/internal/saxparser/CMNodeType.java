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

public interface CMNodeType {
	static public int EMPTY = 1;
	static public int ANY = 2;
	static public int PCDATA = 3;
	static public int ELEMENT_REFERENCE = 4;
	static public int ENTITY_REFERENCE = 5;
	static public int GROUP = 6;

	static public int ONE = 7;
	static public int OPTIONAL = 8;
	static public int ONE_OR_MORE = 9;
	static public int ZERO_OR_MORE = 10;

	static public int GROUP_CHOICE = 11;
	static public int GROUP_SEQUENCE = 12;

}
