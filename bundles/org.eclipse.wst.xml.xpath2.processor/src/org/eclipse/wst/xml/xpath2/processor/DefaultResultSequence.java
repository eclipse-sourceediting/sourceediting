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

package org.eclipse.wst.xml.xpath2.processor;

import java.util.*;

import org.eclipse.wst.xml.xpath2.processor.types.*;

/**
 * Default implementation of a result sequence.
 * 
 */
public class DefaultResultSequence extends ResultSequence {

	private List _seq;

	/**
	 * Constructor.
	 * 
	 * an empty array is created
	 */
	public DefaultResultSequence() {
		_seq = new ArrayList();
	}

	/**
	 * @param item
	 *            is added
	 */
	public DefaultResultSequence(AnyType item) {
		this();
		add(item);
	}

	/**
	 * @param item
	 *            is added to array _seq
	 */
	@Override
	public void add(AnyType item) {
		assert item != null;
		_seq.add(item);
	}

	/**
	 * @param rs
	 *            ResultSequence
	 */
	@Override
	public void concat(ResultSequence rs) {
		for (Iterator i = rs.iterator(); i.hasNext();)
			_seq.add(i.next());
	}

	/**
	 * @return the next iteration of array _seq
	 */
	@Override
	public ListIterator iterator() {
		return _seq.listIterator();
	}

	/**
	 * @return integer of the size of array _seq
	 */
	@Override
	public int size() {
		return _seq.size();
	}

	/**
	 * @param i
	 *            is the position of the array item that is wanted.
	 * @return item i from array _seq
	 */
	@Override
	public AnyType get(int i) {
		return (AnyType) _seq.get(i);
	}

	/**
	 * @return first item from array _seq
	 */
	@Override
	public AnyType first() {
		if (_seq.size() == 0)
			return null;

		return get(0);
	}

	/**
	 * Whether or not array _seq is empty
	 * 
	 * @return a boolean
	 */
	@Override
	public boolean empty() {
		return _seq.isEmpty();
	}

	/**
	 * Clears the sequence.
	 */
	@Override
	public void clear() {
		_seq.clear();
	}

	/**
	 * Create a new sequence.
	 * 
	 * @return The new sequence.
	 */
	@Override
	public ResultSequence create_new() {
		return new DefaultResultSequence();
	}
}
