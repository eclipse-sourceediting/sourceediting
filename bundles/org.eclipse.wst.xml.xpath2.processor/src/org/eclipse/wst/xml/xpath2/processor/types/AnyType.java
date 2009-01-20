/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.types;
/**
 * Common base for every type
 */
public abstract class AnyType {
	/**
	 * Retrieves the datatype's full pathname
	 * @return Datatype's full pathname
	 */
	public abstract String string_type();
	/**
	 * Retrieves the datatype's name
	 * @return Datatype's name
	 */
	public abstract String string_value();
}
