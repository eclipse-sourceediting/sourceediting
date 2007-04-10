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
package org.eclipse.wst.css.core.internal.document;



import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICounter;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.Counter;


/**
 * 
 */
class CounterImpl extends CSSPrimitiveContainer implements ICounter {

	/**
	 * 
	 */
	CounterImpl() {
		super(CSS_COUNTER);
	}

	CounterImpl(CounterImpl that) {
		super(that);
	}

	/**
	 * @return org.eclipse.wst.css.core.model.interfaces.ICSSNode
	 * @param deep
	 *            boolean
	 */
	public ICSSNode cloneNode(boolean deep) {
		CounterImpl cloned = new CounterImpl(this);

		if (deep)
			cloneChildNodes(cloned, deep);

		return cloned;
	}

	/**
	 * This method is used to get the Counter value. If this CSS value doesn't
	 * contain a counter value, a <code>DOMException</code> is raised.
	 * Modification to the corresponding style property can be achieved using
	 * the <code>Counter</code> interface.
	 * 
	 * @return The Counter value.
	 * @exception DOMException
	 *                INVALID_ACCESS_ERR: Raised if the CSS value doesn't
	 *                contain a Counter value (e.g. this is not
	 *                <code>CSS_COUNTER</code>).
	 */
	public Counter getCounterValue() throws DOMException {
		return this;
	}

	/**
	 * This attribute is used for the identifier of the counter.
	 */
	public String getIdentifier() {
		return getAttribute(IDENTIFIER);
	}

	/**
	 * This attribute is used for the style of the list.
	 */
	public String getListStyle() {
		return getAttribute(LISTSTYLE);
	}

	/**
	 * This attribute is used for the separator of the nested counters.
	 */
	public String getSeparator() {
		return getAttribute(SEPARATOR);
	}

	/**
	 * 
	 */
	protected void initPrimitives() {
		return;
	}

	/**
	 * @param identifier
	 *            java.lang.String
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public void setIdentifier(String identifier) throws DOMException {
		setAttribute(IDENTIFIER, identifier);
	}

	/**
	 * @param listStyle
	 *            java.lang.String
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public void setListStyle(String listStyle) throws DOMException {
		setAttribute(LISTSTYLE, listStyle);
	}

	/**
	 * @param Separator
	 *            java.lang.String
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public void setSeparator(String Separator) throws org.w3c.dom.DOMException {
		setAttribute(SEPARATOR, Separator);
	}
}
