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



import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorCombinator;

/**
 * 
 */
class CSSSelectorCombinator extends CSSSelectorItem implements ICSSSelectorCombinator {

	char fType = ICSSSelectorCombinator.UNKNOWN;

	/**
	 * CSSSelectorCombinator constructor comment.
	 */
	public CSSSelectorCombinator(char type) {
		super();
		setCombinatorType(type);
	}

	/**
	 * @return boolean
	 * @param obj
	 *            java.lang.Object
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || this.getClass() != obj.getClass())
			return false;

		CSSSelectorCombinator foreign = (CSSSelectorCombinator) obj;

		return (getCombinatorType() == foreign.getCombinatorType());

	}

	/**
	 * @return int
	 */
	public char getCombinatorType() {
		return fType;
	}

	/**
	 * @return int
	 */
	public int getItemType() {
		return COMBINATOR;
	}

	/**
	 * @return java.lang.String
	 */
	public String getString() {
		StringBuffer buf = new StringBuffer();
		buf.append(fType);
		return buf.toString();
	}

	void setCombinatorType(char type) {
		fType = type;
	}
}
