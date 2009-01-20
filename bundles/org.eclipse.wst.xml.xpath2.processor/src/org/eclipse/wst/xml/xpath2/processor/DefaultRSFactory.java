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

/**
 * Factory implementation which creates sequences of type DefaultResultSequence.
 * 
 */
public class DefaultRSFactory extends ResultSequenceFactory {
	private static final ResultSequence _rs_creator = new DefaultResultSequence();

	public static final int POOL_SIZE = 50;

	private ResultSequence[] _rs_pool = new ResultSequence[POOL_SIZE];
	private int _head_pos;

	/**
	 * Constructor of factory.
	 * 
	 */
	public DefaultRSFactory() {
		for(int i = 0; i < POOL_SIZE; i++)
			_rs_pool[i] = _rs_creator.create_new();
			
		_head_pos = POOL_SIZE - 1;
	}

	@Override
	protected ResultSequence fact_create_new() {
		if(_head_pos > 0) {
			return _rs_pool[_head_pos--];
		}

		return _rs_creator.create_new();
	}

	@Override
	protected void fact_release(ResultSequence rs) {
		int new_pos = _head_pos + 1;

		if( new_pos < POOL_SIZE) {
			rs.clear();
			
			_head_pos = new_pos;
			_rs_pool[new_pos] = rs;
		}
	}

	@Override
	protected void fact_print_debug() {
		System.out.println("Head pos: " + _head_pos);
	}
}
