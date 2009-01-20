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
/**
 * Support for Sequence type.
 */
public class SequenceType extends XPathNode {
	/**
	 * Set internal value for EMPTY.
	 */	
	public static final int EMPTY = 0;
        /**
         * Set internal value for NONE.
         */
	public static final int NONE = 1;
        /**
         * Set internal value for QUESTION.
         */
	public static final int QUESTION = 2;
        /**
         * Set internal value for STAR.
         */
	public static final int STAR = 3;
        /**
         * Set internal value for PLUS.
         */
	public static final int PLUS = 4;

	private int _occ;
	private ItemType _it;
	/**
	 * Constructor for SequenceType.
	 * @param occ Occurence.
	 * @param it Item type.
	 */
	public SequenceType(int occ, ItemType it) {
		_occ = occ;
		_it = it;
	}
	/**
	 * Support for Visitor interface.
	 * @return Result of Visitor operation.
	 */
	@Override
	public Object accept(XPathVisitor v) {
		return v.visit(this);
	}
	/**
	 * Get occurence of item.
	 * @return Result from Int operation.
	 */
	public int occurrence() { return _occ; }
	/**
	 * Support for ItemType interface.
	 * @return Result of ItemType operation.
	 */
	public ItemType item_type() { return _it; }
}
