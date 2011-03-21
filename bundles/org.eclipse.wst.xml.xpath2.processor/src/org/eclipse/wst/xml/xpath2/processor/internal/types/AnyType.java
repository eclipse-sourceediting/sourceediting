/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;

/**
 * Common base for every type
 */
public abstract class AnyType {
	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return Datatype's full pathname
	 */
	public abstract String string_type();

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return Datatype's name
	 */
	public abstract String string_value();
	
	/**
	 * Returns the "new style" of TypeDefinition for this item.
	 * 
	 * @return Type definition (possibly backed by a schema type)
	 */
	public abstract TypeDefinition getTypeDefinition();
}
