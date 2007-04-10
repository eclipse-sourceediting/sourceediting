/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.metamodelimpl;

import java.util.Iterator;

import org.eclipse.wst.css.core.internal.metamodel.CSSMMDescriptor;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;


class CSSMMDescriptorImpl extends CSSMMNodeImpl implements CSSMMDescriptor {


	public CSSMMDescriptorImpl() {
		super();
	}

	public String getType() {
		return TYPE_DESCRIPTOR;
	}

	public String getName() {
		String name = getAttribute(ATTR_NAME);
		if (name != null) {
			return name.toLowerCase();
		}
		else {
			return null;
		}
	}

	/*
	 * @see CSSMMNodeImpl#canContain(CSSMMNode)
	 */
	boolean canContain(CSSMMNode child) {
		if (child == null) {
			return false;
		}
		String type = child.getType();
		return (type == TYPE_NUMBER || type == TYPE_KEYWORD || type == TYPE_FUNCTION || type == TYPE_CONTAINER || type == TYPE_STRING || type == TYPE_SEPARATOR);
	}

	/*
	 * @see CSSMMNodeImpl#getError()
	 */
	short getError() {
		if (getName() == null) {
			return MetaModelErrors.ERROR_NOT_DEFINED;
		}
		else if (getChildCount() == 0) {
			return MetaModelErrors.ERROR_NO_CHILD;
		}
		else {
			return MetaModelErrors.NO_ERROR;
		}
	}

	/*
	 * @see CSSMMDescriptor#getValues()
	 */
	public Iterator getValues() {
		ValueCollector collector = new ValueCollector();
		collector.apply(this);
		return collector.getNodes();
	}

}
