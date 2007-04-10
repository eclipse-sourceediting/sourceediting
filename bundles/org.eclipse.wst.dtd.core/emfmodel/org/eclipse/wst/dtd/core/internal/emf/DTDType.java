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

package org.eclipse.wst.dtd.core.internal.emf;

public interface DTDType extends DTDObject {
	// constant strings for dtd attribute types
	public static final String NONE = "None"; //$NON-NLS-1$

	public static final String CDATA = "Character Data (CDATA)"; //$NON-NLS-1$
	public static final String ID = "Identifier (ID)"; //$NON-NLS-1$
	public static final String IDREF = "ID Reference (IDREF)"; //$NON-NLS-1$
	public static final String IDREFS = "ID References (IDREFS)"; //$NON-NLS-1$
	public static final String ENTITY = "Entity Name (ENTITY)"; //$NON-NLS-1$
	public static final String ENTITIES = "Entity Names (ENTITIES)"; //$NON-NLS-1$
	public static final String NMTOKEN = "Name Token (NMTOKEN)"; //$NON-NLS-1$
	public static final String NMTOKENS = "Name Tokens (NMTOKENS)"; //$NON-NLS-1$
	public static final String ENUM_NAME_TOKEN_GROUP = "Enumerated Name Tokens"; //$NON-NLS-1$
	public static final String ENUM_NOTATION_GROUP = "Enumerated NOTATION"; //$NON-NLS-1$

	public String toString();

}
