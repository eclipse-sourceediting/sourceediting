/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.emf;

public interface DTDType extends DTDObject {
	// constant strings for dtd attribute types
	public static final String NONE = "None";

	public static final String CDATA = "Character Data (CDATA)";
	public static final String ID = "Identifier (ID)";
	public static final String IDREF = "ID Reference (IDREF)";
	public static final String IDREFS = "ID References (IDREFS)";
	public static final String ENTITY = "Entity Name (ENTITY)";
	public static final String ENTITIES = "Entity Names (ENTITIES)";
	public static final String NMTOKEN = "Name Token (NMTOKEN)";
	public static final String NMTOKENS = "Name Tokens (NMTOKENS)";
	public static final String ENUM_NAME_TOKEN_GROUP = "Enumerated Name Tokens";
	public static final String ENUM_NOTATION_GROUP = "Enumerated NOTATION";

	public String toString();

}
