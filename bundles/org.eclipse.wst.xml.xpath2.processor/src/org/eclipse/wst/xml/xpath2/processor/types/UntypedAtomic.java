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

import org.eclipse.wst.xml.xpath2.processor.*;
/**
 * A representation of the UntypedAtomic datatype which is used to represent
 * untyped atomic nodes.
 */
public class UntypedAtomic extends AnyAtomicType {
	private String _value;
	/**
	 * Initialises using the supplied String
	 * @param x The String representation of the value of the untyped atomic node
	 */
	public UntypedAtomic(String x) {
		_value = x;
	}
	/**
	 * Retrieves the datatype's full pathname
	 * @return "xdt:untypedAtomic" which is the datatype's full pathname
	 */
	public String string_type() {
		return "xdt:untypedAtomic";
	}
	/**
	 * Retrieves a String representation of the value of this untyped atomic node
	 * @return String representation of the value of this untyped atomic node
	 */
	public String string_value() {
		return _value;
	}
}
