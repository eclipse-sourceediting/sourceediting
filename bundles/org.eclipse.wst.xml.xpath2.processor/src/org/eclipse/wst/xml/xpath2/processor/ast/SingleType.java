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

package org.eclipse.wst.xml.xpath2.processor.ast;

import org.eclipse.wst.xml.xpath2.processor.types.*;

/**
 * Support for Single types.
 */
public class SingleType extends XPathNode {

	private QName _type;
	private boolean _qmark;

	/**
	 * Constructor for SingleType.
	 * 
	 * @param type
	 *            QName type.
	 * @param qmark
	 *            optional type? (true/false).
	 */
	public SingleType(QName type, boolean qmark) {
		_type = type;
		_qmark = qmark;
	}

	/**
	 * Default Constructor for SingleType.
	 */
	public SingleType(QName type) {
		this(type, false);
	}

	/**
	 * Support for Visitor interface.
	 * 
	 * @return Result of Visitor operation.
	 */
	@Override
	public Object accept(XPathVisitor v) {
		return v.visit(this);
	}

	/**
	 * Set optional type.
	 * 
	 * @return optional type value.
	 */
	public boolean qmark() {
		return _qmark;
	}

	/**
	 * Support for QName interface.
	 * 
	 * @return Result of QName operation.
	 */
	public QName type() {
		return _type;
	}
}
